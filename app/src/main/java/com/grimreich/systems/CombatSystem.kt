package com.grimreich.systems

import com.grimreich.core.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CombatSystem @Inject constructor(
    private val gameRepository: GameRepository,
    private val partyRepository: PartyRepository,
    private val inventorySystem: InventorySystem,
    private val moraleSystem: MoraleSystem,
    private val combatRound: CombatRound,
    private val questEngine: QuestEngine,
    private val experienceSystem: ExperienceSystem,
    private val lootSystem: LootSystem,
    private val itemCatalogue: com.grimreich.world.ItemCatalogue
) {
    private var onCombatEnd: (() -> Unit)? = null
    private var currentEnemy: Enemy? = null

    // FIX: Accept GameState parameter to use the mutable copy inside updateState,
    // instead of calling gameRepository.currentState() which returns the stale snapshot.
    private fun heroToCombatant(state: GameState): CombatantState? {
        val hero = state.party.find { it.id == state.activeHeroId } ?: return null
        return CombatantState(
            name = hero.name,
            hp = hero.hp,
            maxHp = hero.maxHp,
            endurance = hero.endurance,
            morale = hero.morale,
            armor = hero.effectiveArmor(state.inventory),
            attackBase = hero.effectiveAttack(state.inventory),
            strength = hero.strength,
            agility = hero.agility,
            intelligence = hero.intelligence,
            perception = hero.perception,
            charisma = hero.charisma,
            piety = hero.piety
        )
    }

    fun startCombat(enemy: Enemy, onEndCallback: (() -> Unit)? = null) {
        onCombatEnd = onEndCallback
        currentEnemy = enemy
        gameRepository.updateState { state ->
            val c = state.combat
            c.active = true
            c.round = 1
            c.enemyName = enemy.name
            c.enemyHp = enemy.stats.maxHp
            c.enemyMaxHp = enemy.stats.maxHp
            c.enemyAttack = enemy.stats.attack
            c.enemyDefense = enemy.stats.defense
            c.enemyAgility = enemy.stats.speed
            c.enemyIntelligence = 10 // default
            c.enemyStrength = enemy.stats.attack / 2 // derivation
            c.log.clear()
            c.log.add("Pojedynek z ${enemy.name} rozpocz\u0119ty!")
        }
    }

    fun playerAttack(): String = resolvePlayerAction("ATTACK")
    fun playerDefend(): String = resolvePlayerAction("DEFEND")
    fun useSkill(skillId: String): String = resolvePlayerAction(skillId)

    fun usePotion(itemId: String): String {
        var result = ""
        gameRepository.updateState { state ->
            val hero = state.party.find { it.id == state.activeHeroId } ?: run { result = "Brak"; return@updateState }
            val potion = state.inventory.find { it.id == itemId } ?: run { result = "Brak"; return@updateState }
            val heal = potion.effects["heal"] ?: 0
            val sanity = potion.effects["sanity"] ?: 0
            if (heal > 0) hero.hp = (hero.hp + heal).coerceAtMost(hero.maxHp)
            if (sanity > 0) hero.sanity = (hero.sanity + sanity).coerceAtMost(100)
            state.inventory.remove(potion)
            state.combat.log.add("${hero.name} wypija ${potion.name}.")
            result = "U\u017cyto"
        }
        return result
    }

    fun useEchoSkill(type: String): String {
        gameRepository.updateState { state ->
            val c = state.combat
            c.log.add("U\u017cyto Echo: $type")
            if (type == "OVERWRITE") {
                c.enemyAttack = (c.enemyAttack / 2).coerceAtLeast(1)
            }
        }
        return "Echo"
    }

    private fun resolvePlayerAction(actionType: String): String {
        var status = ""
        // FIX (deadlock): onCombatEnd must NOT be invoked inside updateState{},
        // because updateState uses synchronized(this) on GameRepository.
        // If the callback triggers another updateState call (e.g. from a quest
        // system or UI handler), it would attempt to re-acquire the same lock on
        // the same thread, causing a deadlock.
        // Fix: capture the pending callback in a local variable inside updateState{},
        // then invoke it AFTER the synchronized block completes.
        var pendingCombatEndCallback: (() -> Unit)? = null

        // OPTIMIZATION: Only update repository once per round
        gameRepository.updateState { state ->
            val c = state.combat
            val hero = state.party.find { it.id == state.activeHeroId } ?: run { status = "Brak bohatera"; return@updateState }
            if (!c.active) { status = "Brak walki"; return@updateState }

            // FIX: Pass state to heroToCombatant so it reads from the mutable copy,
            // not from the stale gameRepository.currentState() snapshot.
            val heroState = heroToCombatant(state) ?: run { status = "Brak bohatera"; return@updateState }
            val enemyState = CombatantState(
                name = c.enemyName,
                hp = c.enemyHp,
                maxHp = c.enemyMaxHp,
                endurance = c.enemyHp / 2,
                morale = 60,
                armor = c.enemyDefense,
                attackBase = c.enemyAttack,
                agility = c.enemyAgility,
                intelligence = c.enemyIntelligence,
                strength = c.enemyStrength
            )

            val skillId = if (actionType != "ATTACK" && actionType != "DEFEND") actionType else null
            val result = combatRound.resolveRound(
                attacker = heroState,
                defender = enemyState,
                skillId = skillId
            )

            c.round++
            c.enemyHp = enemyState.hp
            hero.hp = heroState.hp
            hero.endurance = heroState.endurance
            hero.morale = heroState.morale
            c.log.addAll(result.log)
            if (c.log.size > 50) {
                val toRemove = c.log.size - 50
                repeat(toRemove) { c.log.removeAt(0) }
            }

            if (combatRound.isDefeated(enemyState)) {
                c.active = false
                // FIX: Do NOT invoke onCombatEnd here - would deadlock synchronized(GameRepository).
                // Capture it for invocation after updateState{} completes.
                pendingCombatEndCallback = onCombatEnd
                c.log.add("${c.enemyName} pokonany!")
                
                // --- REWARDS sequence (Final Technical Polish) ---
                currentEnemy?.let { enemy ->
                    val gold = kotlin.random.Random.nextInt(enemy.lootTable.goldMin, enemy.lootTable.goldMax + 1)
                    if (gold > 0) {
                        state.gold += gold
                        c.log.add("Zdobyto $gold G.")
                    }
                    
                    // Grant XP to active hero
                    val xpMsg = experienceSystem.addXp(state.activeHeroId ?: "", enemy.xpReward)
                    c.log.add(xpMsg)
                    
                    // Roll for specific items in loot table
                    enemy.lootTable.itemChances.forEach { (itemId, chance) ->
                        if (kotlin.random.Random.nextFloat() < chance) {
                            if (lootSystem.awardSpecificItemDirect(state, itemId)) {
                                val item = itemCatalogue.get(itemId)
                                c.log.add("Zdobyto: ${item?.name ?: itemId}")
                            }
                        }
                    }
                }
                currentEnemy = null

                state.pendingQuestId?.let { pending ->
                    if (pending.startsWith("COMBAT_WIN:")) {
                        val qId = pending.removePrefix("COMBAT_WIN:")
                        questEngine.advanceStepDirect(state, qId)
                        state.pendingQuestId = null
                    }
                }
            }

            if (combatRound.isDefeated(heroState)) {
                c.active = false
                hero.isDead = true
                hero.hp = 0
                // FIX: Do NOT invoke onCombatEnd here - would deadlock synchronized(GameRepository).
                // Capture it for invocation after updateState{} completes.
                pendingCombatEndCallback = onCombatEnd
            }
            status = "Runda ${c.round}"
        }

        // FIX: Invoke the combat-end callback OUTSIDE of the synchronized updateState{} block
        // to prevent deadlock when the callback itself calls updateState.
        pendingCombatEndCallback?.invoke()

        return status
    }
}
