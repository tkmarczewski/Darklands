package com.grimreich.core

import com.grimreich.grimreich.v1.*
import com.grimreich.systems.DialogueManager
import com.grimreich.systems.QuestSystem
import com.grimreich.systems.StatePersistenceManager
import com.grimreich.world.CityCatalogue
import com.grimreich.world.ItemCatalogue
import dagger.Lazy
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepository @Inject constructor(
    private val questSystemProvider: Lazy<QuestSystem>,
    private val dialogueManagerProvider: Lazy<DialogueManager>,
    private val persistence: StatePersistenceManager,
    private val cityCatalogue: CityCatalogue,
    private val itemCatalogue: ItemCatalogue,
) {
    private val questSystem get() = questSystemProvider.get()
    private val dialogueManager get() = dialogueManagerProvider.get()
    private var state: GameState = GameState()

    fun currentState(): GameState = state

    fun replaceState(newState: GameState) {
        state = newState
    }

    fun updateState(transform: (GameState) -> GameState) {
        state = transform(state)
        persistCurrentState()
    }

    fun seed() {
        state = GameState()

        val hero1 = Hero(
            id = "hero_1", name = "Friedrich", age = 25,
            strength = 14, agility = 10, intelligence = 12,
            endurance = 13, charisma = 9, piety = 8,
            hp = 35, maxHp = 35
        ).also {
            it.skills["sword"] = 40
            it.skills["riding"] = 20
        }

        state.party.add(hero1)
        state.activeHeroId = hero1.id
        state.gold = 100

        cityCatalogue.clear()
        cityCatalogue.seedCanonical()
        
        itemCatalogue.seed()

        state.grimCurrentRegion = "wybrzeze_polnocne"
        state.world.location = "wybrzeze_polnocne"

        questSystem.clear()
        questSystem.seedIntegratedContent()
        dialogueManager.seedBasicDialogues()

        state.hireableHeroes.addAll(
            listOf(
                Hero(id = "rec_borg", name = "Borg Ironfoot", age = 34, strength = 15, endurance = 14, hp = 30, maxHp = 30),
                Hero(id = "rec_elara", name = "Elara Shadow", age = 22, agility = 16, perception = 15, hp = 20, maxHp = 20)
            )
        )

        state.inventory.add(Item("sword_01", "Żelazny Miecz", "weapon", "weapon", 50, 2.5, effects = mapOf("attack" to 8)))
        state.logEntries.add("Początek nowej ery w Grimreich.")
        persistCurrentState()
    }

    fun log(msg: String) {
        state.logEntries.add(msg)
        if (state.logEntries.size > 100) state.logEntries.removeAt(0)
        persistCurrentState()
    }

    fun sync() {}

    fun restoreIfAvailable(): Boolean {
        val restored = persistence.restore() ?: return false
        state = restored.toDomain()
        return true
    }

    fun persistCurrentState() {
        persistence.persist(state.toDto())
    }

    fun hasSession(): Boolean = persistence.exists()

    fun clearSessionAndReset() {
        persistence.clear()
        state = GameState()
    }
}
