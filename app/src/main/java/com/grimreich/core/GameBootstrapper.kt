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
        val existingPlayerName = gameRepository.currentState().playerName
        val existingHeroName = gameRepository.currentState().heroName

        // Clear all session volatile caches
        cityCatalogue.clear()
        cityCatalogue.seedCanonical()
        
        itemCatalogue.seed()
        
        questSystem.clear()
        questSystem.seedIntegratedContent()
        
        dialogueManager.seedBasicDialogues()
        
        worldMap.clear()
        worldMap.seedStage1()

        // Reset repository state to a clean template
        gameRepository.replaceState(GameState())
        val state = gameRepository.currentState()
        
        state.playerName = existingPlayerName
        state.heroName = existingHeroName

        state.world.day = 1
        state.world.timeOfDay = "morning"
        state.world.location = cityCatalogue.startingCityId
        state.grimCurrentRegion = cityCatalogue.startingCityId
        state.gold = 100

        // Ralwing is ALWAYS in the party from start
        val ralwing = Hero(
            id = "hero_ralwing",
            name = "Ralwing",
            age = 40,
            strength = 18,
            agility = 16,
            endurance = 15,
            perception = 12,
            intelligence = 10,
            charisma = 10,
            piety = 10,
            hp = 80,
            maxHp = 80,
            portraitRes = "port_knight"
        )
        state.party.add(ralwing)
        state.activeHeroId = ralwing.id

        // Initial pool of recruits
        state.hireableHeroes.addAll(HeroPool.generatePool(state.grimCurrentRegion, 3))

        gameRepository.persistCurrentState()
    }
}
