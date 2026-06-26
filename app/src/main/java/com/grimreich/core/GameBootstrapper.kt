package com.grimreich.core

import com.grimreich.systems.DialogueManager
import com.grimreich.systems.HeroPool
import com.grimreich.systems.QuestSystem
import com.grimreich.world.CityCatalogue
import com.grimreich.world.ItemCatalogue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameBootstrapper @Inject constructor(
    private val gameRepository: GameRepository,
    private val questSystem: QuestSystem,
    private val dialogueManager: DialogueManager,
    private val cityCatalogue: CityCatalogue,
    private val itemCatalogue: ItemCatalogue,
    private val worldMap: WorldMap
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
        
        questSystem.clear()
        questSystem.seedIntegratedContent(seed = (System.currentTimeMillis() % 1000).toInt())
        
        dialogueManager.seedBasicDialogues()
        
        worldMap.clear()
        worldMap.seedStage1()

        // Reset repository state to a clean template
        gameRepository.replaceState(GameState())
        val state = gameRepository.currentState()
        
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
            gameRepository.log("[DZIEDZICTWO] Wzmocniona Kotwica: Twoja sesja startuje z pełną stabilnością.")
        }

        state.world.day = 1
        state.world.timeOfDay = "morning"
        state.world.location = cityCatalogue.startingCityId
        state.grimCurrentRegion = cityCatalogue.startingCityId
        state.gold = GameConstants.INITIAL_GOLD

        // Ralwing is no longer added by default here. 
        // He will be available via Dev Menu or recruitment if desired.

        // Initial pool of recruits
        state.hireableHeroes.addAll(HeroPool.generatePool(state.grimCurrentRegion, GameConstants.MAX_RECRUITS_POOL_SIZE))

        gameRepository.persistCurrentState()
    }
}
