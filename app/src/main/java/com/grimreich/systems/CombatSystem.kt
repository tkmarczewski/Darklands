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

    fun startCombat(enemyName: String, enemyHp: Int, enemyAttack: Int, enemyDefense: Int, onEndCallback: (() -> Unit)? = null) {
        onCombatEnd = onEndCallback
        gameRepository.updateState { state ->
            val c = state.combat
            c.active = true
            c.round = 1
            c.enemyName = enemyName
            c.enemyHp = enemyHp
            c.enemyMaxHp = enemyHp
            c.enemyAttack = enemyAttack
            c.enemyDefense = enemyDefense
            
            // Map default bestiary secondary stats for now
            c.enemyAgility = 10
            c.enemyIntelligence = 10
            c.enemyStrength = 10
            
            c.log.clear()
            c.log.add("Pojedynek z $enemyName rozpoczęty!")
        }
    }

    fun playerAttack(): String = resolvePlayerAction("ATTACK")
    fun playerDefend(): String = resolvePlayerAction("DEFEND")

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
            result = "Użyto"
        }
        return result
    }

    fun useEchoSkill(type: String): String {
        gameRepository.updateState { state ->
            val c = state.combat
            c.log.add("Użyto Echo: $type")
            if (type == "OVERWRITE") {
                c.enemyAttack = (c.enemyAttack / 2).coerceAtLeast(1)
            }
        }
        return "Echo"
    }

    private fun resolvePlayerAction(actionType: String): String {
        var status = ""
        gameRepository.updateState { state ->
            val c = state.combat
            val hero = state.party.find { it.id == state.activeHeroId } ?: run { status = "Brak bohatera"; return@updateState }
            if (!c.active) { status = "Brak walki"; return@updateState }
            val heroState = heroToCombatant() ?: run { status = "Brak bohatera"; return@updateState }

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

            val result = combatRound.resolveRound(
                attacker = heroState,
                defender = enemyState
            )

            c.round++
            c.enemyHp = enemyState.hp
            hero.hp = heroState.hp
            hero.endurance = heroState.endurance
            hero.morale = heroState.morale
            
            c.log.addAll(result.log)
            if (c.log.size > 50) {
                repeat(c.log.size - 50) { c.log.removeAt(0) }
            }
            
            if (combatRound.isDefeated(enemyState)) {
                c.active = false
                onCombatEnd?.invoke()
                c.log.add("${c.enemyName} pokonany!")
                
                state.pendingQuestId?.let { pending ->
                    if (pending.startsWith("COMBAT_WIN:")) {
                        val qId = pending.removePrefix("COMBAT_WIN:")
                        questEngine.advanceStep(qId)
                        state.pendingQuestId = null
                    }
                }
            }

            if (combatRound.isDefeated(heroState)) {
                c.active = false
                hero.isDead = true
                hero.hp = 0
                onCombatEnd?.invoke()
            }
            status = "Runda ${c.round}"
        }
        return status
    }
}
