package com.grimreich.core

import com.grimreich.grimreich.v1.NPC
import com.grimreich.systems.DialogueManager
import com.grimreich.systems.QuestEngine
import com.grimreich.systems.QuestManifest
import com.grimreich.world.HeroPool
import com.grimreich.world.CityCatalogue
import com.grimreich.world.ItemCatalogue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameBootstrapper @Inject constructor(
    private val gameRepository: GameRepository,
    private val questEngine: QuestEngine,
    private val questManifest: QuestManifest,
    private val dialogueManager: DialogueManager,
    private val cityCatalogue: CityCatalogue,
    private val itemCatalogue: ItemCatalogue,
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

        // Clear all session volatile caches
        cityCatalogue.clear()
        cityCatalogue.seedCanonical()
        itemCatalogue.seed()
        dialogueManager.seedBasicDialogues()
        worldMap.clear()
        worldMap.seedStage1(seed)

        // FIX-QUESTS: Reset state BEFORE seeding quests so the registry is
        // populated into a clean slate, not later overwritten by replaceState().
        gameRepository.replaceState(GameState())

        // Seed quests AFTER state reset so they are always available.
        questManifest.seed()

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
            state.world.location = cityCatalogue.startingCityId
            // FIX-QUESTS: grimCurrentRegion must match startingCityId so that
            // getAllRelevantQuestsForCity() finds quests for the starting city.
            state.grimCurrentRegion = cityCatalogue.startingCityId
            state.gold = GameConstants.INITIAL_GOLD

            // Initial pool of recruits
            state.hireableHeroes.addAll(heroPool.generatePool(GameConstants.MAX_RECRUITS_POOL_SIZE))
        }

        gameRepository.persistCurrentState()
    }
}
