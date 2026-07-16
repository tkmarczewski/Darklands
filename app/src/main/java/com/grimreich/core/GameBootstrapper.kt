package com.grimreich.core

import android.util.Log
import com.grimreich.grimreich.v1.NPC
import com.grimreich.systems.DialogueManager
import com.grimreich.systems.AtmosphericLogSystem
import com.grimreich.systems.QuestEngine
import com.grimreich.systems.QuestManifest
import com.grimreich.world.HeroPool
import com.grimreich.world.CityCatalogue
import com.grimreich.world.ItemCatalogue
import com.grimreich.systems.ContentValidator
import com.grimreich.systems.ErrorSeverity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameBootstrapper @Inject constructor(
    private val gameRepository: GameRepository,
    private val atmosphericLogSystem: AtmosphericLogSystem,
    private val cityCatalogue: CityCatalogue,
    private val contentValidator: ContentValidator,
    private val worldMap: WorldMap,
    private val heroPool: HeroPool
) {
    suspend fun bootstrapFreshWorld(seed: Int = 1) = withContext(Dispatchers.IO) {
        val oldState = gameRepository.currentState()
        val existingPlayerName = oldState.playerName
        val existingHeroName = oldState.heroName
        val existingLore = oldState.unlockedLoreIds.toSet()
        val existingMeta = oldState.persistentMeta.copy(
            unlockedLegacyBuffs = oldState.persistentMeta.unlockedLegacyBuffs.toMutableSet()
        )

        // Clear session volatile caches (synced already above, but we can re-clear if needed)
        // worldMap is unique to bootstrapper
        worldMap.clear()
        worldMap.seedStage1(seed)

        // Reset state to default. sync() will be triggered by replaceState's internal logic.
        gameRepository.replaceState(GameState())

        // Run content validation
        val validationErrors = contentValidator.validateAll()
        val criticalCount = validationErrors.count { it.severity == ErrorSeverity.CRITICAL }
        if (criticalCount > 0) {
            Log.e("GameBootstrapper", "CRITICAL CONTENT ERROR DETECTED! Found $criticalCount critical issues.")
            // In debug builds we could show an error UI or a persistent notification
        }

        gameRepository.updateState { state ->
            state.playerName = existingPlayerName
            state.heroName = existingHeroName
            state.unlockedLoreIds.addAll(existingLore)

            state.persistentMeta.apply {
                totalSessionsFinished = existingMeta.totalSessionsFinished
                unlockedLegacyBuffs.addAll(existingMeta.unlockedLegacyBuffs)
                maxMetaAwarenessReached = existingMeta.maxMetaAwarenessReached
            }

            // Apply Legacy Buffs
            if (state.persistentMeta.unlockedLegacyBuffs.contains("REINFORCED_ANCHOR")) {
                state.world.globalStability = 100
                state.logEntries.add("[DZIEDZICTWO] Wzmocniona Kotwica: Twoja sesja startuje z pełną stabilnością.")
            }

            state.world.day = 1
            state.world.timeOfDay = "morning"
            state.world.locationId = cityCatalogue.startingCityId
            
            state.gold = GameConstants.INITIAL_GOLD

            // Inicjalizacja sesji - cytat
            val playerName = state.playerName ?: "Nieznajomy"
            val heroName = state.heroName ?: "Wędrowiec"
            state.logEntries.add(atmosphericLogSystem.getRandomMessage(
                System.currentTimeMillis(), 
                playerName, 
                heroName,
                state.world.globalStability
            ))

            // Initial pool of recruits
            state.hireableHeroes.addAll(heroPool.generatePool(GameConstants.MAX_RECRUITS_POOL_SIZE))
        }

        gameRepository.persistCurrentState()
    }
}

