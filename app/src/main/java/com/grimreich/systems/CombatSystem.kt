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
    private val lootSystem: LootSystem
) {
    private var onCombatEnd: (() -> Unit)? = null
    private var currentEnemy: Enemy? = null
    private var pendingCombatEndCallback: (() -> Unit)? = null

    private fun heroToCombatant(state: GameState, hero: Hero): CombatantState {
        return CombatantState(
            name = hero.name,
            hp = hero.hp,
            maxHp = hero.maxHp,
            endurance = 10,
            morale = hero.morale,
            armor = hero.effectiveArmor(state.inventory),
            attackBase = hero.effectiveAttack(state.inventory),
            strength = hero.strength,
            agility = hero.agility,
            intelligence = hero.intelligence,
            perception = hero.perception,
            piety = hero.piety,
            charisma = hero.charisma,
            activeEffects = mutableListOf()
        )
    }

    fun startCombat(enemy: Enemy, onEnd: (() -> Unit)? = null) {
        currentEnemy = enemy
        onCombatEnd = onEnd
        
        gameRepository.updateState { state ->
            state.combat.active = true
            state.combat.round = 1
            state.combat.enemyName = enemy.name
            state.combat.enemyType = enemy.type.name
            state.combat.enemyHp = enemy.stats.maxHp
            state.combat.enemyMaxHp = enemy.stats.maxHp
            state.combat.enemyAttack = enemy.stats.attack
            state.combat.enemyDefense = enemy.stats.defense
            state.combat.enemyStamina = 10
            state.combat.activeHeroId = state.party.firstOrNull { !it.isDead }?.id
            state.combat.log.clear()
            state.combat.log.add("Początek walki z ${enemy.name}!")
        }
    }

    fun playerAttack() = resolvePlayerAction("ATTACK")
    fun playerDefend() = resolvePlayerAction("DEFEND")
    fun useSkill(skillId: String) = resolvePlayerAction("SKILL:$skillId")
    
    fun setActiveHero(heroId: String) {
        gameRepository.updateState { it.combat.activeHeroId = heroId }
    }

    fun usePotion(itemId: String): String {
        var msg = ""
        gameRepository.updateState { state ->
            val heroId = state.combat.activeHeroId ?: state.party.firstOrNull { !it.isDead }?.id ?: return@updateState
            val hero = state.party.find { it.id == heroId } ?: return@updateState
            val item = state.inventory.find { it.id == itemId } ?: return@updateState
            
            if (item.effects.containsKey("heal")) {
                val heal = item.effects["heal"] ?: 0
                hero.hp = (hero.hp + heal).coerceAtMost(hero.maxHp)
                state.inventory.remove(item)
                msg = "Użyto ${item.name}: +$heal HP."
                state.combat.log.add(msg)
            }
        }
        return msg
    }

    fun useEchoSkill(skillId: String): String {
        var msg = ""
        gameRepository.updateState { state ->
            if (state.world.echoIntensity > 0.5f) {
                msg = "Użyto mocy echa: $skillId!"
                state.combat.log.add(msg)
                state.world.echoIntensity -= 0.1f
            }
        }
        return msg
    }

    fun resolvePlayerAction(action: String): String {
        var result = ""
        pendingCombatEndCallback = null

        if (currentEnemy == null && gameRepository.currentState().combat.active) {
            val savedTypeStr = gameRepository.currentState().combat.enemyType
            val type = try { 
                if (savedTypeStr != null) EnemyType.valueOf(savedTypeStr) else EnemyType.BANDIT 
            } catch (e: Exception) { 
                EnemyType.BANDIT 
            }
            currentEnemy = Bestiary.get(type)
        }

        gameRepository.updateState { state ->
            val c = state.combat
            val enemy = currentEnemy ?: return@updateState
            
            val activeHero = state.party.find { it.id == c.activeHeroId } 
                ?: state.party.firstOrNull { !it.isDead } 
                ?: return@updateState
            
            val heroCombatant = heroToCombatant(state, activeHero)
            
            val aliveHeroes = state.party.filter { !it.isDead }
            if (aliveHeroes.isEmpty()) return@updateState
            
            val targetHero = aliveHeroes.random()
            c.currentTargetHeroId = targetHero.id
            val targetCombatant = heroToCombatant(state, targetHero)

            val enemyCombatant = CombatantState(
                name = enemy.name,
                hp = c.enemyHp,
                maxHp = c.enemyMaxHp,
                endurance = c.enemyStamina,
                morale = 80,
                armor = enemy.stats.defense / 2,
                attackBase = enemy.stats.attack,
                strength = 10,
                agility = 10,
                intelligence = 10,
                activeEffects = c.enemyEffects.toMutableList()
            )

            c.round++
            result = "Runda ${c.round}"

            val playerRound = when {
                action.startsWith("SKILL:") -> {
                    val skillId = action.substringAfter("SKILL:")
                    combatRound.resolveRound(heroCombatant, enemyCombatant, skillId)
                }
                action == "DEFEND" -> {
                    targetCombatant.armor += 5
                    null
                }
                else -> combatRound.resolveRound(heroCombatant, enemyCombatant)
            }

            if (enemyCombatant.hp > 0) {
                val enemyRound = combatRound.resolveRound(enemyCombatant, targetCombatant)
                c.log.addAll(enemyRound.log)
                targetHero.hp = targetCombatant.hp
                if (targetHero.hp <= 0) {
                    targetHero.isDead = true
                    c.log.add("TRAGEDIA: ${targetHero.name} poległ!")
                    state.logEntries.add("ZADANIE: Odzyskaj ciało ${targetHero.name} i zanieś je do Kaplicy.")
                    
                    val corpse = lootSystem.itemCatalogue.get("quest_corpse")?.copy(
                        name = "Zwłoki: ${targetHero.name}",
                        id = "corpse_${targetHero.id}"
                    )
                    if (corpse != null) state.inventory.add(corpse)
                }
            }

            if (playerRound != null) {
                c.log.addAll(playerRound.log)
                activeHero.hp = heroCombatant.hp
                if (activeHero.hp <= 0 && !activeHero.isDead) {
                    activeHero.isDead = true
                    c.log.add("TRAGEDIA: ${activeHero.name} poległ!")
                    state.logEntries.add("ZADANIE: Odzyskaj ciało ${activeHero.name} i zanieś je do Kaplicy.")
                    
                    val corpse = lootSystem.itemCatalogue.get("quest_corpse")?.copy(
                        name = "Zwłoki: ${activeHero.name}",
                        id = "corpse_${activeHero.id}"
                    )
                    if (corpse != null) state.inventory.add(corpse)
                }
            }
            
            c.enemyHp = enemyCombatant.hp
            c.enemyStamina = enemyCombatant.endurance

            if (enemyCombatant.hp <= 0) {
                c.active = false
                pendingCombatEndCallback = onCombatEnd
                c.log.add("${c.enemyName} pokonany!")
                
                currentEnemy?.let { enemyDef ->
                    val xpMsgs = experienceSystem.addPartyXpDirect(state, enemyDef.xpReward)
                    xpMsgs.forEach { c.log.add(it) }
                    val lootMsgs = lootSystem.awardLootFromTableDirect(state, enemyDef.lootTable)
                    lootMsgs.forEach { c.log.add(it) }
                }
                currentEnemy = null

                state.pendingQuestId?.let { pending ->
                    val rawId = pending.removePrefix("FINALIZE:").removePrefix("COMBAT_WIN:")
                    questEngine.advanceStepDirect(state, rawId)
                    state.pendingQuestId = null
                }
            } else if (state.party.all { it.isDead }) {
                c.active = false
                c.log.add("Cała drużyna poległa!")
                pendingCombatEndCallback = onCombatEnd
            }
        }

        pendingCombatEndCallback?.invoke()
        return result
    }
}
