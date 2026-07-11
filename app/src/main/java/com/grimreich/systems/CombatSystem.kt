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

            // Build Initiative Order
            val slots = mutableListOf<InitiativeSlot>()
            state.party.filter { !it.isDead }.forEach { hero ->
                val initVal = hero.agility * 2 + (0..3).random()
                slots.add(InitiativeSlot(hero.id, true, initVal))
            }
            val enemyInit = enemy.stats.speed * 2 + (0..3).random()
            slots.add(InitiativeSlot("ENEMY", false, enemyInit))
            
            state.combat.initiativeOrder.clear()
            state.combat.initiativeOrder.addAll(slots.sortedByDescending { it.initiativeValue })
            state.combat.currentTurnIndex = 0
            
            // Set first acting hero
            val firstSlot = state.combat.initiativeOrder.firstOrNull()
            if (firstSlot?.isPlayer == true) {
                state.combat.activeHeroId = firstSlot.id
            }
        }
    }

    fun playerAttack() = resolvePlayerAction("ATTACK")
    fun playerDefend() = resolvePlayerAction("DEFEND")
    fun useSkill(skillId: String) {
        val skill = SkillCatalogue.allSkills.find { it.id == skillId } ?: return
        
        gameRepository.updateState { state ->
            if (state.world.echoIntensity < skill.echoCost) return@updateState
            state.world.echoIntensity -= skill.echoCost
            
            // Apply stability impact for mind_collapse
            if (skill.id == "mind_collapse") {
                state.world.globalStability = (state.world.globalStability - 10).coerceAtLeast(0)
                state.logEntries.add("Użycie Zapaści Umysłu naruszyło strukturę regionu.")
            }
        }
        resolvePlayerAction("SKILL:$skillId")
    }

    fun setActiveHero(heroId: String) {
        gameRepository.updateState { it.combat.activeHeroId = heroId }
    }

    fun usePotion(itemId: String): String {
        var msg = ""
        gameRepository.updateState { state ->
            val heroId = state.combat.activeHeroId ?: state.party.firstOrNull { !it.isDead }?.id ?: return@updateState
            val hero = state.party.find { it.id == heroId } ?: return@updateState
            val item = state.inventory.find { it.instanceId == itemId } ?: return@updateState
            
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
            
            // 1. Identify who's turn it is
            if (c.initiativeOrder.isEmpty()) return@updateState
            val currentSlot = c.initiativeOrder[c.currentTurnIndex]
            
            val actingHero: Hero?
            val heroCombatant: CombatantState
            
            if (currentSlot.isPlayer) {
                actingHero = state.party.find { it.id == currentSlot.id } ?: return@updateState
                if (actingHero.isDead) {
                    advanceTurn(state)
                    return@updateState
                }
                heroCombatant = heroToCombatant(state, actingHero)
            } else {
                // It's enemy's turn, but this function was called for player action?
                // For now, we assume player action can only be called on player turn.
                // In a full implementation, this might trigger AI turn first.
                actingHero = state.party.find { it.id == c.activeHeroId } 
                    ?: state.party.firstOrNull { !it.isDead } 
                    ?: return@updateState
                heroCombatant = heroToCombatant(state, actingHero)
            }
            
            val aliveHeroes = state.party.filter { !it.isDead }
            if (aliveHeroes.isEmpty()) return@updateState
            
            // Enemy targeting
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

            // Round increment only on full loop
            if (c.currentTurnIndex == 0) {
                c.round++
            }
            result = "Runda ${c.round}"

            val playerRound = when {
                action.startsWith("SKILL:") -> {
                    val skillId = action.substringAfter("SKILL:")
                    combatRound.resolveRound(heroCombatant, enemyCombatant, skillId)
                }
                action == "DEFEND" -> {
                    // FIX: Defend now targets the ACTING hero
                    heroCombatant.armor += 5
                    null
                }
                else -> combatRound.resolveRound(heroCombatant, enemyCombatant)
            }

            if (enemyCombatant.hp > 0 && !currentSlot.isPlayer) {
                val enemyRound = combatRound.resolveRound(enemyCombatant, targetCombatant)
                c.log.addAll(enemyRound.log)
                targetHero.hp = targetCombatant.hp
                if (targetHero.hp <= 0) {
                    targetHero.isDead = true
                    c.log.add("TRAGEDIA: ${targetHero.name} poległ!")
                    state.logEntries.add("ZADANIE: Odzyskaj ciało ${targetHero.name} i zanieś je do Kaplicy.")
                    
                    val corpse = lootSystem.itemCatalogue.get("quest_corpse")?.copy(
                        instanceId = "corpse_${targetHero.id}",
                        name = "Zwłoki: ${targetHero.name}"
                    )
                    if (corpse != null) state.inventory.add(corpse)
                    
                    // Add to Companion Shadows for Etap 6
                    if (state.companionShadows.none { it.id == targetHero.id }) {
                        state.companionShadows.add(targetHero.copy())
                    }
                    
                    // Remove from initiative
                    state.combat.initiativeOrder.removeAll { it.id == targetHero.id }
                }
            }

            if (playerRound != null && currentSlot.isPlayer) {
                c.log.addAll(playerRound.log)
                actingHero.hp = heroCombatant.hp
                if (actingHero.hp <= 0 && !actingHero.isDead) {
                    actingHero.isDead = true
                    c.log.add("TRAGEDIA: ${actingHero.name} poległ!")
                    state.logEntries.add("ZADANIE: Odzyskaj ciało ${actingHero.name} i zanieś je do Kaplicy.")
                    
                    val corpse = lootSystem.itemCatalogue.get("quest_corpse")?.copy(
                        instanceId = "corpse_${actingHero.id}",
                        name = "Zwłoki: ${actingHero.name}"
                    )
                    if (corpse != null) state.inventory.add(corpse)

                    // Add to Companion Shadows for Etap 6
                    if (state.companionShadows.none { it.id == actingHero.id }) {
                        state.companionShadows.add(actingHero.copy())
                    }
                    
                    // Remove from initiative
                    state.combat.initiativeOrder.removeAll { it.id == actingHero.id }
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
            } else {
                advanceTurn(state)
            }
        }

        pendingCombatEndCallback?.invoke()
        return result
    }

    private fun advanceTurn(state: GameState) {
        val c = state.combat
        if (c.initiativeOrder.isEmpty()) return
        
        c.currentTurnIndex = (c.currentTurnIndex + 1) % c.initiativeOrder.size
        
        val nextSlot = c.initiativeOrder[c.currentTurnIndex]
        if (nextSlot.isPlayer) {
            c.activeHeroId = nextSlot.id
        } else {
            // It's enemy's turn. In a real turn-based system, we'd trigger AI here.
            // For now, we stay on previous hero or first alive.
        }
    }
}
