package com.grimreich.systems

import com.grimreich.core.CombatRound
import com.grimreich.core.CombatantState
import com.grimreich.core.EquippedItems
import com.grimreich.core.GameRepository
import com.grimreich.core.MoraleSystem
import com.grimreich.core.PartyRepository
import com.grimreich.core.WoundType

object CombatSystem {

    // Build a CombatantState from the active hero
    private fun heroToCombatant(): CombatantState? {
        val hero = PartyRepository.activeHero() ?: return null
        val armorValue = if (hero.equipment["armor"] != null) 3 else 0
        return CombatantState(
            name       = hero.name,
            hp         = hero.hp,
            maxHp      = hero.maxHp,
            endurance  = hero.endurance,
            morale     = 70,
            armor      = armorValue,
            attackBase = 5,
            strength   = hero.strength,
            agility    = hero.agility,
            intelligence = hero.intelligence
        )
    }

    fun startCombat(enemyName: String, enemyHp: Int, enemyAttack: Int, enemyDefense: Int) {
        val c = GameRepository.state.combat
        c.active      = true
        c.round       = 0
        c.enemyName   = enemyName
        c.enemyHp     = enemyHp
        c.enemyMaxHp  = enemyHp
        c.enemyAttack = enemyAttack
        c.enemyDefense = enemyDefense
        c.log.clear()
        c.log.add("Walka z $enemyName rozpoczeta!")
        GameRepository.log("Walka z $enemyName!")
    }

    fun playerAttack(): String {
        return resolvePlayerAction("ATTACK")
    }

    fun playerDefend(): String {
        return resolvePlayerAction("DEFEND")
    }

    fun playerUseSpecial(type: String): String {
        return resolvePlayerAction("SPECIAL_$type")
    }

    private fun resolvePlayerAction(actionType: String): String {
        val c    = GameRepository.state.combat
        val hero = GameRepository.state.party.find { it.id == GameRepository.state.activeHeroId } ?: return "Brak bohatera"
        if (!c.active) return "Brak aktywnej walki"

        val heroState = heroToCombatant() ?: return "Brak bohatera"
        
        // Apply temporary action modifiers
        when (actionType) {
            "DEFEND" -> {
                heroState.armor += 5
                c.log.add("${hero.name} przyjmuje postawę obronną.")
            }
            "SPECIAL_MIST" -> {
                heroState.agility += 5
                c.log.add("${hero.name} wzywa Mgłę!")
            }
            "SPECIAL_BLOOD" -> {
                heroState.strength += 5
                c.log.add("${hero.name} wzmacnia się Krwią!")
            }
            "SPECIAL_REFLECTION" -> {
                heroState.intelligence += 5
                c.log.add("${hero.name} skupia Odbicie!")
            }
        }

        heroState.activeEffects = c.heroEffects
        
        val enemyState = CombatantState(
            name       = c.enemyName,
            hp         = c.enemyHp,
            maxHp      = c.enemyMaxHp,
            endurance  = c.enemyHp / 2,
            morale     = 60,
            armor      = c.enemyDefense,
            attackBase = c.enemyAttack,
            agility    = c.enemyAgility,
            intelligence = c.enemyIntelligence,
            strength = c.enemyStrength,
            activeEffects = c.enemyEffects
        )

        val result = CombatRound.resolveRound(
            attacker        = heroState,
            defender        = enemyState,
            attackerEquipped = InventorySystem.getEquippedItems(hero)
        )
        c.round++

        // Apply results back to legacy CombatState
        c.enemyHp        = enemyState.hp
        hero.hp          = heroState.hp
        hero.endurance   = heroState.endurance
        c.log.addAll(result.log)
        
        c.heroEffects  = heroState.activeEffects
        c.enemyEffects = enemyState.activeEffects

        val heroMorale  = MoraleSystem.computeStatus(result.attackerMorale)
        val enemyMorale = MoraleSystem.computeStatus(result.defenderMorale)
        val woundMsg = if (result.defenderWound != WoundType.NONE)
            " [Rana ${c.enemyName}: ${result.defenderWound}]" else ""
        val heroWoundMsg = if (result.attackerWound != WoundType.NONE)
            " [Rana ${hero.name}: ${result.attackerWound}]" else ""

        val defeated = CombatRound.isDefeated(enemyState)
        if (defeated) {
            c.active = false
            c.log.add("${c.enemyName} pokonany!")
            val lootMsg = LootSystem.awardLoot(0.5f)
            if (lootMsg.isNotEmpty()) c.log.add(lootMsg)
            val recovery = CombatRound.postCombatRecovery(heroState)
            hero.hp        = heroState.hp
            hero.endurance = heroState.endurance
            c.log.add(recovery)
        }
        if (CombatRound.isDefeated(heroState)) {
            c.active = false
            c.log.add("${hero.name} pokonany...")
        }

        val summary = result.log.joinToString(" | ")
        GameRepository.log(summary)
        return "Runda ${c.round}: $summary" +
                " | Morale: $heroMorale vs $enemyMorale" +
                woundMsg + heroWoundMsg
    }

    fun isCombatActive() = GameRepository.state.combat.active

    fun getCombatLog(): List<String> = GameRepository.state.combat.log

    // Sprint 14: prosty status walki dla ekranu CombatStatusActivity (UI sprintu 12+).
    // Zwraca podsumowanie aktualnego stanu walki lub komunikat o braku aktywnej walki.
    fun combatSummary(): String {
        val c = GameRepository.state.combat
        if (!c.active && c.log.isEmpty()) return "Brak danych o walce."
        val header = if (c.active) "Walka aktywna z ${c.enemyName} (runda ${c.round})"
                     else "Walka zakonczona (${c.enemyName})"
        val hpLine = "Wrog HP: ${c.enemyHp}/${c.enemyMaxHp}"
        val tail = c.log.takeLast(8).joinToString("\n") { "- $it" }
        return "$header\n$hpLine\n\n$tail"
    }

    // Convenience methods for encounter-starting from CombatActivity
    fun startRandomEncounter() {
        val encounters = listOf(
            Triple("Bandyci na drodze", 40, 8),
            Triple("Szkielety w ruinach", 35, 7),
            Triple("Wataha wilków", 30, 6),
            Triple("Kultysta - Fanatyk Mgly", 45, 9),
            Triple("Straż miejska - Inkwizytor", 50, 10),
            Triple("Rozbójnik Raubrittera", 55, 12)
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
            // Fallback for legacy or untemplated quests
            val (name, hp, atk) = when {
                questId.contains("blood") || questId.contains("korwi") -> Triple("Demon Krwi", 60, 14)
                questId.contains("shadow") || questId.contains("cien") -> Triple("Strażnik Cienia", 55, 12)
                else -> Triple("Potworna Istota", 45, 10)
            }
            startCombat(name, hp, atk, atk / 2)
        }
    }
}
