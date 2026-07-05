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
    
    // Captured to avoid deadlock in synchronized block
    private var pendingCombatEndCallback: (() -> Unit)? = null

    private fun heroToCombatant(state: GameState): CombatantState? {
        val hero = state.party.find { it.id == state.activeHeroId } 
            ?: state.party.firstOrNull { !it.isDead }
            ?: return null
        return CombatantState(
            name = hero.name,
            hp = hero.hp,
            maxHp = hero.maxHp,
            endurance = state.combat.heroStamina,
            morale = hero.morale,
            armor = hero.effectiveArmor(state.inventory),
            attackBase = hero.effectiveAttack(state.inventory),
            strength = hero.strength,
            agility = hero.agility,
            intelligence = hero.intelligence,
            perception = hero.perception,
            piety = hero.piety,
            charisma = hero.charisma,
            activeEffects = state.combat.heroEffects.toMutableList()
        )
    }

    fun startCombat(enemy: Enemy, onEnd: (() -> Unit)? = null) {
        currentEnemy = enemy
        onCombatEnd = onEnd
        
        gameRepository.updateState { state ->
            state.combat.active = true
            state.combat.round = 1
            state.combat.enemyName = enemy.name
            state.combat.enemyHp = enemy.stats.maxHp
            state.combat.enemyMaxHp = enemy.stats.maxHp
            state.combat.enemyAttack = enemy.stats.attack
            state.combat.enemyDefense = enemy.stats.defense
            state.combat.enemyStamina = 10
            state.combat.heroStamina = 10
            state.combat.log.clear()
            state.combat.log.add("Początek walki z ${enemy.name}!")
        }
    }

    fun playerAttack() = resolvePlayerAction("ATTACK")
    fun playerDefend() = resolvePlayerAction("DEFEND")
    fun useSkill(skillId: String) = resolvePlayerAction("SKILL:$skillId")
    
    fun usePotion(itemId: String): String {
        var msg = ""
        gameRepository.updateState { state ->
            val hero = state.party.find { it.id == state.activeHeroId } ?: return@updateState
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
            if (state.grimEchoIntensity > 0.5f) {
                msg = "Użyto mocy echa: $skillId!"
                state.combat.log.add(msg)
                state.grimEchoIntensity -= 0.1f
            }
        }
        return msg
    }

    fun resolvePlayerAction(action: String): String {
        var result = ""
        pendingCombatEndCallback = null

        // AUDIT RECOVERY: Re-initialize enemy if lost on process death
        if (currentEnemy == null && gameRepository.currentState().combat.active) {
            currentEnemy = Bestiary.get(EnemyType.BANDIT)
        }

        gameRepository.updateState { state ->
            val c = state.combat
            val enemy = currentEnemy ?: return@updateState
            val heroCombatant = heroToCombatant(state) ?: return@updateState
            
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

            val roundResult = when {
                action.startsWith("SKILL:") -> {
                    val skillId = action.substringAfter("SKILL:")
                    combatRound.resolveRound(heroCombatant, enemyCombatant, skillId)
                }
                action == "DEFEND" -> {
                    heroCombatant.armor += 5
                    combatRound.resolveRound(enemyCombatant, heroCombatant)
                }
                else -> combatRound.resolveRound(heroCombatant, enemyCombatant)
            }

            c.log.addAll(roundResult.log)
            c.enemyHp = enemyCombatant.hp
            c.enemyStamina = enemyCombatant.endurance
            c.heroStamina = heroCombatant.endurance
            
            // Sync back hero HP and morale
            state.party.find { it.id == state.activeHeroId }?.let { h ->
                h.hp = heroCombatant.hp
                h.morale = heroCombatant.morale
            }

            if (enemyCombatant.hp <= 0) {
                c.active = false
                pendingCombatEndCallback = onCombatEnd
                c.log.add("${c.enemyName} pokonany!")
                
                // --- REWARDS sequence (Final Technical Polish) ---
                currentEnemy?.let { enemyDef ->
                    // Grant XP to all living party members
                    val xpMsgs = experienceSystem.addPartyXpDirect(state, enemyDef.xpReward)
                    xpMsgs.forEach { c.log.add(it) }
                    
                    // Award loot using centralized LootSystem
                    val lootMsgs = lootSystem.awardLootFromTableDirect(state, enemyDef.lootTable)
                    lootMsgs.forEach { c.log.add(it) }
                }
                currentEnemy = null

                state.pendingQuestId?.let { pending ->
                    questEngine.advanceStepDirect(state, pending)
                }
            } else if (heroCombatant.hp <= 0) {
                c.active = false
                c.log.add("Porażka! ${heroCombatant.name} poległ w walce.")
                state.party.find { it.id == state.activeHeroId }?.isDead = true
                pendingCombatEndCallback = onCombatEnd
            }
            
            c.round++
            result = "Runda ${c.round}"
        }

        // Invoke callback OUTSIDE synchronized block
        pendingCombatEndCallback?.invoke()
        
        return result
    }
}
