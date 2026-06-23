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
    private val combatRound: CombatRound
) {

    private var onCombatEnd: (() -> Unit)? = null

    fun setOnCombatEnd(callback: (() -> Unit)?) {
        onCombatEnd = callback
    }

    private fun heroToCombatant(): CombatantState? {
        val hero = partyRepository.activeHero() ?: return null
        val armorValue = if (hero.equipment["armor"] != null) 3 else 0
        return CombatantState(
            name = hero.name,
            hp = hero.hp,
            maxHp = hero.maxHp,
            endurance = hero.endurance,
            morale = 70,
            armor = armorValue,
            attackBase = 5,
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
        c.round = 1 // Start at round 1
        c.enemyName = enemyName
        c.enemyHp = enemyHp
        c.enemyMaxHp = enemyHp
        c.enemyAttack = enemyAttack
        c.enemyDefense = enemyDefense
        c.log.clear()
        c.log.add("Pojedynek z $enemyName rozpoczety!")

        gameRepository.log("Rozpoczeto walke: $enemyName")
        gameRepository.persistCurrentState()
    }

    fun playerAttack(): String = resolvePlayerAction("ATTACK")
    fun playerDefend(): String = resolvePlayerAction("DEFEND")
    fun playerUseSpecial(type: String): String = resolvePlayerAction("SPECIAL_$type")

    private fun resolvePlayerAction(actionType: String): String {
        val state = gameRepository.currentState()
        val c = state.combat
        val hero = state.party.find { it.id == state.activeHeroId } ?: return "Brak bohatera"
        if (!c.active) return "Brak aktywnej walki"
        val heroState = heroToCombatant() ?: return "Brak bohatera"

        when (actionType) {
            "DEFEND" -> {
                heroState.armor += 5
                c.log.add("${hero.name} przyjmuje postawe obronna.")
            }
            "SPECIAL_MIST" -> {
                val bonus = 5 + (hero.piety * GrimConstants.Combat.PIETY_SKILL_SCALING).toInt()
                heroState.agility += bonus
                c.log.add("${hero.name} wzywa Mglę! (+${bonus} ZRC)")
            }
            "SPECIAL_BLOOD" -> {
                val bonus = 5 + (hero.piety * GrimConstants.Combat.PIETY_SKILL_SCALING).toInt()
                heroState.strength += bonus
                c.log.add("${hero.name} wzmacnia się Krwią! (+${bonus} SIŁ)")
            }
            "SPECIAL_REFLECTION" -> {
                val bonus = 5 + (hero.piety * GrimConstants.Combat.PIETY_SKILL_SCALING).toInt()
                heroState.intelligence += bonus
                c.log.add("${hero.name} skupia Odbicie! (+${bonus} INT)")
            }
        }

        heroState.activeEffects = c.heroEffects
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
            strength = c.enemyStrength,
            activeEffects = c.enemyEffects
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
        c.heroEffects = heroState.activeEffects
        c.enemyEffects = enemyState.activeEffects

        val heroMoraleLabel = moraleSystem.computeStatus(result.attackerMorale)
        val enemyMoraleLabel = moraleSystem.computeStatus(result.defenderMorale)
        val woundMsg = if (result.defenderWound != WoundType.NONE) " [Rana ${c.enemyName}: ${result.defenderWound}]" else ""
        val heroWoundMsg = if (result.attackerWound != WoundType.NONE) " [Rana ${hero.name}: ${result.attackerWound}]" else ""

        if (combatRound.isDefeated(enemyState)) {
            c.active = false
            onCombatEnd?.invoke()
            c.log.add("${c.enemyName} pokonany!")
            // awardLoot handled by LootSystem
            val recovery = combatRound.postCombatRecovery(heroState)
            hero.hp = heroState.hp
            hero.endurance = heroState.endurance
            c.log.add(recovery)
        }

        if (combatRound.isDefeated(heroState)) {
            c.active = false
            onCombatEnd?.invoke()
            c.log.add("${hero.name} pokonany...")
        }

        gameRepository.persistCurrentState()
        return "Runda ${c.round}: ${result.log.joinToString(" | ")} | Morale: $heroMoraleLabel vs $enemyMoraleLabel$woundMsg$heroWoundMsg"
    }

    fun isCombatActive() = gameRepository.currentState().combat.active

    fun getCombatLog(): List<String> = gameRepository.currentState().combat.log

    fun combatSummary(): String {
        val c = gameRepository.currentState().combat
        if (!c.active && c.log.isEmpty()) return "Brak danych o walce."
        val header = if (c.active) "Walka aktywna z ${c.enemyName} (runda ${c.round})"
            else "Walka zakonczona (${c.enemyName})"
        val hpLine = "Wrog HP: ${c.enemyHp}/${c.enemyMaxHp}"
        val tail = c.log.takeLast(8).joinToString("\n") { "- $it" }
        return "$header\n$hpLine\n\n$tail"
    }

    fun startRandomEncounter() {
        val encounters = listOf(
            Triple("Bandyci na drodze", 40, 8),
            Triple("Szkielety w ruinach", 35, 7),
            Triple("Wataha wilkow", 30, 6),
            Triple("Kultysta - Fanatyk Mgley", 45, 9),
            Triple("Straz miejska - Inkwizytor", 50, 10),
            Triple("Rozbojnik Raubrittera", 55, 12)
        )
        val enc = encounters.random()
        startCombat(enc.first, enc.second, enc.third, enc.third / 2)
    }

    fun startEncounterForQuest(questId: String) {
        val template = QuestRegistry.allTemplates.find { it.id == questId }
            ?: QuestRegistry.bloodChain.stages.find { it.id == questId }
            ?: QuestRegistry.verdictChain.stages.find { it.id == questId }
        if (template != null) {
            val stats = template.enemyStats ?: QuestRegistry.EnemyStats("Potworna Istota", 45, 10, 5)
            startCombat(stats.name, stats.hp, stats.atk, stats.def)
        } else {
            val (name, hp, atk) = when {
                questId.contains("blood") || questId.contains("korwi") -> Triple("Demon Krwi", 60, 14)
                questId.contains("shadow") || questId.contains("cien") -> Triple("Straznik Cienia", 55, 12)
                else -> Triple("Potworna Istota", 45, 10)
            }
            startCombat(name, hp, atk, atk / 2)
        }
    }
}
