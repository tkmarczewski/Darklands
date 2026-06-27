package com.grimreich.systems

import com.grimreich.core.*
import dagger.Lazy
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CombatSystem @Inject constructor(
    private val gameRepository: GameRepository,
    private val partyRepository: PartyRepository,
    private val inventorySystem: InventorySystem,
    private val moraleSystem: MoraleSystem,
    private val combatRound: CombatRound,
    private val questSystemProvider: Lazy<QuestSystem>
) {

    private var onCombatEnd: (() -> Unit)? = null

    fun setOnCombatEnd(callback: (() -> Unit)?) {
        onCombatEnd = callback
    }

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

    fun useEchoSkill(skillType: String): String {
        val state = gameRepository.currentState()
        val hero = state.party.find { it.id == state.activeHeroId } ?: return "Brak bohatera"
        val c = state.combat
        if (!c.active) return "Brak walki"

        return when (skillType) {
            "REVISION" -> {
                if (hero.sanity < 5) return "Zbyt mało Poczytalności!"
                hero.sanity = (hero.sanity - 5).coerceAtLeast(0)
                hero.hp = (hero.hp + 15).coerceAtMost(hero.maxHp)
                c.log.add("[REWIZJA] ${hero.name} nagina czas. (+15 HP, -5 Sanity)")
                state.metaAwarenessLevel += 1
                gameRepository.persistCurrentState()
                "REWIZJA"
            }
            "ERASURE" -> {
                if (state.world.globalStability < 10) return "Świat jest zbyt niestabilny!"
                state.world.globalStability = (state.world.globalStability - 10).coerceAtLeast(0)
                state.world.echoIntensity = (state.world.echoIntensity + 0.05f).coerceAtMost(1.0f)
                val dmg = c.enemyHp / 2
                c.enemyHp -= dmg
                c.log.add("[WYMAZANIE] ${hero.name} usuwa dane wroga. (-$dmg HP, -10 Stabilność)")
                if (c.enemyHp <= 0) {
                    c.active = false
                    onCombatEnd?.invoke()
                    c.log.add("${c.enemyName} wymazany!")
                }
                gameRepository.persistCurrentState()
                "WYMAZANIE"
            }
            "OVERWRITE" -> {
                hero.corruption += 15
                c.enemyAttack = (c.enemyAttack / 2).coerceAtLeast(1)
                c.log.add("[NADPISANIE] ${hero.name} zmienia parametry wroga. (-Atak wroga, +15 Korupcja)")
                gameRepository.persistCurrentState()
                "NADPISANIE"
            }
            else -> "Nieznana umiejętność"
        }
    }

    fun usePotion(itemId: String): String {
        val state = gameRepository.currentState()
        val c = state.combat
        val hero = state.party.find { it.id == state.activeHeroId } ?: return "Brak bohatera"
        
        val potion = state.inventory.find { it.id == itemId } ?: return "Brak mikstury"
        val heal = potion.effects["heal"] ?: 0
        val mana = potion.effects["mana"] ?: 0
        
        if (heal > 0) {
            hero.hp = (hero.hp + heal).coerceAtMost(hero.maxHp)
            c.log.add("${hero.name} wypija ${potion.name} (+${heal} HP)")
        }
        if (mana > 0) {
            // Restore endurance/mana equivalent
                        hero.endurance = (hero.endurance + mana).coerceIn(0, 20)
            c.log.add("${hero.name} wypija ${potion.name} (+${mana} Wytrz.)")
        }
        
        state.inventory.remove(potion)
        gameRepository.persistCurrentState()
        return "Użyto ${potion.name}"
    }

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
        
        // CRITICAL FIX: Forces Compose to see a new list reference for the log
        c.log.addAll(result.log)
        val newLog = c.log.toList() 
        c.log.clear()
        c.log.addAll(newLog)

        c.heroEffects = heroState.activeEffects
        c.enemyEffects = enemyState.activeEffects

        val heroMoraleLabel = moraleSystem.computeStatus(result.attackerMorale)
        val enemyMoraleLabel = moraleSystem.computeStatus(result.defenderMorale)
        
        // Enrich combat log with descriptive impact messages
        val sensoryLogs = when {
            result.attackerDamage > 20 -> listOf("${hero.name} wyprowadza morderczy cios! Ostrze zgrzyta o kość, a krew (lub to, co ją zastępuje) tryska na Twoją twarz.")
            result.attackerDamage > 10 -> listOf("${hero.name} trafia czysto. Czujesz opór rozrywanego mięsa i satysfakcjonujące chrupnięcie pancerza.")
            result.attackerDamage > 0 -> listOf("Cios ześlizguje się po powierzchni, zostawiając jedynie powierzchowną rysę na istnieniu wroga.")
            else -> listOf("Powietrze przecina tylko świst Twojej broni. Wróg jest szybki... albo Ty stajesz się wolniejszy.")
        }
        c.log.addAll(sensoryLogs)

        val woundMsg = if (result.defenderWound != WoundType.NONE) " [Rana ${c.enemyName}: ${result.defenderWound}]" else ""
        val heroWoundMsg = if (result.attackerWound != WoundType.NONE) " [Rana ${hero.name}: ${result.attackerWound}]" else ""

        // --- PROJECT CIPHER: SCRIBE BOSS LOGIC ---
        if (c.enemyName.lowercase().contains("skryba") || c.enemyName.lowercase().contains("scribe")) {
            handleScribeBossSpecial(c, result, hero)
        }

        if (combatRound.isDefeated(enemyState)) {
            c.active = false
            onCombatEnd?.invoke()
            c.log.add("${c.enemyName} pokonany!")
            
            // CRITICAL FIX: Signal Quest Success if this was a quest combat
            state.pendingQuestId?.let { pending ->
                if (pending.startsWith("COMBAT_WIN:")) {
                    val qId = pending.removePrefix("COMBAT_WIN:")
                    questSystemProvider.get().markObjectiveComplete(qId)
                }
            }

            // awardLoot handled by LootSystem
            val recovery = combatRound.postCombatRecovery(heroState)
            hero.hp = heroState.hp
            hero.endurance = heroState.endurance
            c.log.add(recovery)
        }

        if (combatRound.isDefeated(heroState)) {
            c.active = false
            hero.isDead = true
            hero.hp = 0
            gameRepository.log("${hero.name} został zgładzony... Jego dusza dryfuje w Pęknięciu.")
            gameRepository.persistCurrentState()
            onCombatEnd?.invoke()
        }

        gameRepository.persistCurrentState()
        return "Runda ${c.round}: ${result.log.joinToString(" | ")} | Morale: $heroMoraleLabel vs $enemyMoraleLabel$woundMsg$heroWoundMsg"
    }

    private fun handleScribeBossSpecial(c: CombatState, result: RoundResult, hero: Hero) {
        val state = gameRepository.currentState()
        // The Scribe is invulnerable to normal damage unless corrupted
        if (!c.log.any { it.contains("[NADPISANIE]") || it.contains("[REWIZJA]") }) {
            c.enemyHp = c.enemyMaxHp 
            c.log.add("[LOG_ADMIN]: Wykryto nieautoryzowaną próbę zmiany wartości HP. Przywracanie stanu fabrycznego.")
        }

        // Scribe attacks with system commands
        if (c.round % 3 == 0) {
            val command = listOf("DELETE_STAMINA", "REALLOCATE_SANITY", "FORCE_CORRUPTION").random()
            when (command) {
                "DELETE_STAMINA" -> {
                    hero.endurance = (hero.endurance - 5).coerceAtLeast(0)
                    c.log.add("[SYSTEM]: Komenda: DELETE_STAMINA. ${hero.name} traci siły.")
                }
                "REALLOCATE_SANITY" -> {
                    hero.sanity = (hero.sanity - 8).coerceAtLeast(0)
                    c.log.add("[SYSTEM]: Komenda: REALLOCATE_SANITY. Twoje myśli zostają przeniesione do sektora tymczasowego.")
                }
                "FORCE_CORRUPTION" -> {
                    hero.corruption = (hero.corruption + 10).coerceAtMost(100)
                    c.log.add("[SYSTEM]: Komenda: FORCE_CORRUPTION. Błąd spójności danych bohatera.")
                }
            }
        }
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
        if (questId == "RAID") {
            // Stats should have been set in state or passed differently.
            // Let's use a simpler approach: get from GameState.pendingQuestId if it starts with RAID
            val state = gameRepository.currentState()
            val pending = state.pendingQuestId
            if (pending?.startsWith("RAID:") == true) {
                val parts = pending.split(":")
                            if (parts.size >= 4) startCombat(parts[1], parts[2].toIntOrNull() ?: 40, parts[3].toIntOrNull() ?: 10, (parts[3].toIntOrNull() ?: 10) / 2)
                return
            }
        }

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
