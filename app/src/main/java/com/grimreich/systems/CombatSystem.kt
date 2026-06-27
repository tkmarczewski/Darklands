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
    private val questEngine: QuestEngine
) {

    private var onCombatEnd: (() -> Unit)? = null

    private fun heroToCombatant(): CombatantState? {
        val hero = partyRepository.activeHero() ?: return null
        val state = gameRepository.currentState()
        
        return CombatantState(
            name = hero.name,
            hp = hero.hp,
            maxHp = hero.maxHp,
            endurance = hero.endurance,
            morale = 70,
            armor = hero.effectiveArmor(state.inventory),
            attackBase = hero.effectiveAttack(state.inventory),
            strength = hero.strength,
            agility = hero.agility,
            intelligence = hero.intelligence
        )
    }

    fun startCombat(enemyName: String, enemyHp: Int, enemyAttack: Int, enemyDefense: Int, onEndCallback: (() -> Unit)? = null) {
        onCombatEnd = onEndCallback
        val state = gameRepository.currentState()
        val c = state.combat
        c.active = true
        c.round = 1
        c.enemyName = enemyName
        c.enemyHp = enemyHp
        c.enemyMaxHp = enemyHp
        c.enemyAttack = enemyAttack
        c.enemyDefense = enemyDefense
        c.log.clear()
        c.log.add("Pojedynek z $enemyName rozpoczęty!")
        gameRepository.persistCurrentState()
    }

    fun playerAttack(): String = resolvePlayerAction("ATTACK")
    fun playerDefend(): String = resolvePlayerAction("DEFEND")

    fun usePotion(itemId: String): String {
        val state = gameRepository.currentState()
        val hero = state.party.find { it.id == state.activeHeroId } ?: return "Brak"
        val potion = state.inventory.find { it.id == itemId } ?: return "Brak"
        hero.hp = (hero.hp + 20).coerceAtMost(hero.maxHp)
        state.inventory.remove(potion)
        state.combat.log.add("${hero.name} wypija ${potion.name}.")
        gameRepository.persistCurrentState()
        return "Użyto"
    }

    fun useEchoSkill(type: String): String {
        val state = gameRepository.currentState()
        val c = state.combat
        c.log.add("Użyto Echo: $type")
        if (type == "OVERWRITE") {
            c.enemyAttack = (c.enemyAttack / 2).coerceAtLeast(1)
        }
        gameRepository.persistCurrentState()
        return "Echo"
    }

    private fun resolvePlayerAction(actionType: String): String {
        val state = gameRepository.currentState()
        val c = state.combat
        val hero = state.party.find { it.id == state.activeHeroId } ?: return "Brak bohatera"
        if (!c.active) return "Brak walki"
        val heroState = heroToCombatant() ?: return "Brak bohatera"

        val enemyState = CombatantState(
            name = c.enemyName,
            hp = c.enemyHp,
            maxHp = c.enemyMaxHp,
            endurance = c.enemyHp / 2,
            morale = 60,
            armor = c.enemyDefense,
            attackBase = c.enemyAttack
        )

        val result = combatRound.resolveRound(
            attacker = heroState,
            defender = enemyState,
            attackerEquipped = inventorySystem.getEquippedItems(hero)
        )

        c.round++
        c.enemyHp = enemyState.hp
        hero.hp = heroState.hp
        hero.endurance = heroState.endurance
        
        c.log.addAll(result.log)
        val newLog = c.log.toList()
        c.log.clear()
        c.log.addAll(newLog)

        if (combatRound.isDefeated(enemyState)) {
            c.active = false
            onCombatEnd?.invoke()
            c.log.add("${c.enemyName} pokonany!")
            
            state.pendingQuestId?.let { pending ->
                if (pending.startsWith("COMBAT_WIN:")) {
                    val qId = pending.removePrefix("COMBAT_WIN:")
                    questEngine.advanceStep(qId)
                }
            }
        }

        if (combatRound.isDefeated(heroState)) {
            c.active = false
            hero.isDead = true
            hero.hp = 0
            onCombatEnd?.invoke()
        }

        gameRepository.persistCurrentState()
        return "Runda ${c.round}"
    }
}
