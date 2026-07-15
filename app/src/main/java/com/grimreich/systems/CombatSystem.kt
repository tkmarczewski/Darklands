package com.grimreich.systems

import com.grimreich.core.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CombatSystem @Inject constructor(
    private val gameRepository: GameRepository,
    private val moraleSystem: MoraleSystem,
    private val combatRound: CombatRound,
    private val questEngine: QuestEngine,
    private val experienceSystem: ExperienceSystem,
    private val lootSystem: LootSystem,
) {
    private var onCombatEnd: (() -> Unit)? = null
    private var pendingCombatEndCallback: (() -> Unit)? = null

    private fun heroToCombatant(state: GameState, hero: Hero): CombatantState {
        return CombatantState(
            name = hero.name,
            hp = hero.hp,
            maxHp = hero.maxHp,
            endurance = hero.effectiveEndurance(),
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
            state.combat.enemyAgility = enemy.stats.speed
            state.combat.enemyIntelligence = 10 // Baseline for procedural enemies
            state.combat.enemyStrength = 10
            state.combat.enemyStamina = 10
            state.combat.activeHeroId = state.party.firstOrNull { !it.isDead }?.id
            state.combat.log.clear()
            state.combat.log.add("Początek walki z ${enemy.name}!")

            // Build Initiative Order
            val slots = mutableListOf<InitiativeSlot>()
            state.party.filter { !it.isDead }.forEach { hero ->
                val initVal = (hero.agility * 2) + combatRound.randomProvider.nextInt(0, 4)
                slots.add(InitiativeSlot(hero.id, isPlayer = true, initiativeValue = initVal))
            }
            val enemyInit = (enemy.stats.speed * 2) + combatRound.randomProvider.nextInt(0, 4)
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
                state.world.globalStability = (state.world.globalStability - GameConstants.SKILL_STABILITY_LOSS_HEAVY).coerceAtLeast(0)
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

        gameRepository.updateState { state ->
            val c = state.combat
            if (c.active && c.initiativeOrder.isNotEmpty()) {
                // 1. Advance Round and Recalculate if at start
                if (c.currentTurnIndex == 0) {
                    c.round++
                    recalculateInitiative(state)
                }
                result = "Runda ${c.round}"

                // 2. Ensure Player Turn
                var currentSlot = c.initiativeOrder.getOrNull(c.currentTurnIndex)
                if (currentSlot != null && !currentSlot.isPlayer) {
                    resolveEnemyTurnsInternal(state)
                    if (c.active) {
                        currentSlot = c.initiativeOrder.getOrNull(c.currentTurnIndex)
                    }
                }

                // 3. Process Action if slot is valid and is Player
                if (c.active && currentSlot != null && currentSlot.isPlayer) {
                    state.party.find { it.id == currentSlot.id }?.let { actingHero ->
                        if (actingHero.isDead) {
                            advanceTurn(state)
                        } else {
                            processHeroAction(state, actingHero, action)
                            
                            // Check Combat End Condition
                            if (c.enemyHp <= 0) {
                                handleCombatWin(state, c)
                            } else {
                                advanceTurn(state)
                                resolveEnemyTurnsInternal(state)
                            }
                        }
                    }
                }
            }
        }

        // Invoke callbacks outside the updateState block
        if (!gameRepository.currentState().combat.active) {
            onCombatEnd?.invoke()
        }

        return result
    }

    private fun processHeroAction(state: GameState, hero: Hero, action: String) {
        val c = state.combat
        val heroCombatant = heroToCombatant(state, hero)
        val enemyCombatant = getEnemyCombatant(c)

        val playerRound = when {
            action.startsWith("SKILL:") -> {
                val skillId = action.substringAfter("SKILL:")
                combatRound.resolveRound(heroCombatant, enemyCombatant, skillId)
            }
            action == "DEFEND" -> {
                heroCombatant.armor += 5
                combatRound.resolveRound(heroCombatant, enemyCombatant, "system_defend")
            }
            else -> combatRound.resolveRound(heroCombatant, enemyCombatant)
        }

        c.log.addAll(playerRound.log)
        hero.hp = heroCombatant.hp
        c.enemyHp = enemyCombatant.hp
        c.enemyStamina = enemyCombatant.endurance

        if (hero.hp <= 0) {
            handleHeroDeath(state, hero)
        }
    }

    private fun resolveEnemyTurnsInternal(state: GameState) {
        val c = state.combat
        val enemyTypeStr = c.enemyType ?: "BANDIT"
        val enemyDef = try { EnemyType.valueOf(enemyTypeStr) } catch (e: Exception) { EnemyType.BANDIT }
        val enemyAi = Bestiary.get(enemyDef).ai

        while (c.active && !c.initiativeOrder[c.currentTurnIndex].isPlayer) {
            val aliveHeroes = state.party.filter { !it.isDead }
            if (aliveHeroes.isEmpty()) {
                c.active = false
                c.log.add("Wszyscy bohaterowie polegli!")
                return
            }

            // AI STRATEGY: Target Selection
            val targetHero = when (enemyAi) {
                EnemyAI.TACTICAL -> aliveHeroes.minBy { it.hp }
                EnemyAI.BERSERK -> aliveHeroes.maxBy { it.morale }
                EnemyAI.RANGED -> aliveHeroes.minBy { it.agility }
                else -> {
                    val index = combatRound.randomProvider.nextInt(aliveHeroes.size)
                    aliveHeroes[index]
                }
            }

            val enemyCombatant = getEnemyCombatant(c)
            val targetCombatant = heroToCombatant(state, targetHero)

            // AI STRATEGY: Action Choice
            if (enemyAi == EnemyAI.DEFENSIVE && combatRound.randomProvider.nextInt(100) < 30) {
                enemyCombatant.armor += 10
                c.log.add("Tura przeciwnika: ${c.enemyName} przyjmuje postawę obronną!")
                val enemyRound = combatRound.resolveRound(enemyCombatant, targetCombatant, "system_defend")
                c.log.addAll(enemyRound.log)
            } else {
                c.log.add("Tura przeciwnika: ${c.enemyName} atakuje ${targetHero.name}!")
                val enemyRound = combatRound.resolveRound(enemyCombatant, targetCombatant)
                c.log.addAll(enemyRound.log)
            }

            targetHero.hp = targetCombatant.hp
            c.enemyStamina = enemyCombatant.endurance

            if (targetHero.hp <= 0) handleHeroDeath(state, targetHero)

            if (state.party.all { it.isDead }) {
                c.active = false
                c.log.add("Cała drużyna poległa!")
                return
            }

            advanceTurn(state)
        }
    }

    private fun recalculateInitiative(state: GameState) {
        val c = state.combat
        val slots = mutableListOf<InitiativeSlot>()
        
        state.party.filter { !it.isDead }.forEach { hero ->
            // --- INITIATIVE V2: Agility-Based ---
            // Base value is effective Agility (with equipment/mutations)
            val baseAgility = hero.effectiveAgility()
            val healthPenalty = if (hero.hp < hero.maxHp / 3) -3 else 0
            val moraleBonus = if (hero.morale > 70) 2 else 0
            
            val initVal = (baseAgility + healthPenalty + moraleBonus + combatRound.randomProvider.nextInt(0, 10)).coerceAtLeast(1)
            slots.add(InitiativeSlot(hero.id, true, initVal))
        }
        
        // Enemy init
        val typeStr = c.enemyType ?: "BANDIT"
        val type = try { EnemyType.valueOf(typeStr) } catch (e: Exception) { EnemyType.BANDIT }
        val enemy = Bestiary.get(type)
        // Enemy speed is their agility equivalent
        val enemyInit = enemy.stats.speed + combatRound.randomProvider.nextInt(0, 10)
        slots.add(InitiativeSlot("ENEMY", false, enemyInit))

        c.initiativeOrder.clear()
        c.initiativeOrder.addAll(slots.sortedByDescending { it.initiativeValue })
        c.currentTurnIndex = 0
        
        state.logEntries.add("TRIBUNAL_LOG_014: Ustalono sekwencję działań (Inicjatywa).")
    }

    private fun getEnemyCombatant(c: CombatState) = CombatantState(
        name = c.enemyName,
        hp = c.enemyHp,
        maxHp = c.enemyMaxHp,
        endurance = c.enemyStamina,
        morale = 80,
        armor = c.enemyDefense,
        attackBase = c.enemyAttack,
        strength = c.enemyStrength,
        agility = c.enemyAgility,
        intelligence = c.enemyIntelligence,
        activeEffects = c.enemyEffects.toMutableList()
    )

    private fun handleHeroDeath(state: GameState, hero: Hero) {
        hero.isDead = true
        state.combat.log.add("TRAGEDIA: ${hero.name} poległ!")
        state.logEntries.add("ZADANIE: Odzyskaj ciało ${hero.name} i zanieś je do Kaplicy.")
        
        lootSystem.itemCatalogue.get("quest_corpse")?.copy(
            instanceId = "corpse_${hero.id}",
            name = "Zwłoki: ${hero.name}"
        )?.let { state.inventory.add(it) }
        
        if (state.companionShadows.none { it.id == hero.id }) {
            state.companionShadows.add(hero.deepCopy())
        }
        state.combat.initiativeOrder.removeAll { it.id == hero.id }
    }

    private fun handleCombatWin(state: GameState, c: CombatState) {
        c.active = false
        c.log.add("${c.enemyName} pokonany!")
        
        val typeStr = c.enemyType
        val type = try { 
            if (typeStr != null) EnemyType.valueOf(typeStr) else null 
        } catch (e: Exception) { null }
        
        val enemyDef = type?.let { Bestiary.get(it) }
        if (enemyDef != null) {
            // --- QUANTUM SCAN: Ontological Mass Impact ---
            // If the enemy had significant mass, it increases meta awareness
            if (enemyDef.ontologicalMass >= 50) {
                state.metaAwarenessLevel += 1
                c.log.add("TRIBUNAL_LOG_014: Masa ontologiczna celu zintegrowana. Meta-percepcja wzrasta.")
            }

            // --- SYSTEM TRAUMY (Funkcjonalność A) ---
            checkForTrauma(state, enemyDef)

            experienceSystem.addPartyXpDirect(state, enemyDef.xpReward).forEach { c.log.add(it) }
            lootSystem.awardLootFromTableDirect(state, enemyDef.lootTable).forEach { c.log.add(it) }
        }

        val action = state.pendingAction
        if (action is com.grimreich.core.PendingWorldAction.QuestCombatWin) {
            android.util.Log.d("CombatSystem", "[QUEST] Advancing quest ${action.questId} after win - TO BE CHECKED")
            questEngine.advanceStepDirect(state, action.questId)
            state.pendingAction = com.grimreich.core.PendingWorldAction.None
        }

        // TO BE CHECKED: Auto-advance ALL active quests if they have a COMBAT step matching the enemy
        val currentEnemyType = state.combat.enemyType
        state.quest.activeQuestIds.toList().forEach { qId ->
            val def = questEngine.getDefinition(qId)
            val progress = state.quest.progress[qId]
            if (def != null && progress != null) {
                val currentStep = def.steps.getOrNull(progress.currentStepIndex)
                if (currentStep?.type == StepType.COMBAT && (currentStep.targetId == "ANY" || currentStep.targetId == currentEnemyType)) {
                    questEngine.advanceStepDirect(state, qId)
                }
            }
        }
    }

    private fun checkForTrauma(state: GameState, enemy: Enemy) {
        state.party.filter { !it.isDead }.forEach { hero ->
            // Szansa na traumę wzrasta wraz z siłą przeciwnika i niską stabilnością
            val chance = when {
                enemy.type == EnemyType.PAST_SHADE_ELITE -> GameConstants.TRAUMA_CHANCE_ELITE
                enemy.ontologicalMass >= 30 -> GameConstants.TRAUMA_CHANCE_MASSIVE
                hero.ontologicalStability < 50f -> GameConstants.TRAUMA_CHANCE_UNSTABLE
                else -> GameConstants.TRAUMA_CHANCE_BASE
            }

            if (kotlin.random.Random.nextFloat() < chance) {
                val trauma = TraumaCatalog.getRandomTrauma()
                if (hero.traumaMarks.none { it.id == trauma.id }) {
                    hero.traumaMarks.add(trauma)
                    hero.ontologicalStability -= GameConstants.TRAUMA_STABILITY_LOSS
                    state.combat.log.add("TRAUMA: ${hero.name} zyskał: ${trauma.name}!")
                    state.logEntries.add("ONTOLOGIA: ${hero.name} doznał pęknięcia psychicznego: ${trauma.name}.")
                    hero.normalize()
                }
            }
        }
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
