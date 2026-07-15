package com.grimreich.core

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AgingSystem @Inject constructor(
    private val gameRepository: GameRepository
) {
    fun applyAging(heroId: String) {
        gameRepository.updateState { state ->
            val hero = state.party.find { it.id == heroId } ?: return@updateState
            applyAgingToHero(hero, state)
        }
    }

    fun applyAgingToHero(hero: Hero, state: GameState) {
        hero.age += 1
        
        // PROGRESSION FIX: Increment years served in current career
        hero.currentCareer?.let { current ->
            val entry = hero.careerHistory.find { it.career == current }
            if (entry != null) {
                // CareerEntry is a data class with val, we need to replace it or change to var.
                // Checking CareerChain.kt... it's a data class with val.
                val updatedEntry = entry.copy(yearsServed = entry.yearsServed + 1)
                val index = hero.careerHistory.indexOf(entry)
                hero.careerHistory[index] = updatedEntry
            }
        }

        if (hero.age > 80) {
            hero.intelligence = (hero.intelligence - 1).coerceAtLeast(1)
            hero.virtue = (hero.virtue - 1).coerceAtLeast(0)
            state.logEntries.add("${hero.name} myśli wolniej z wiekiem. (-1 Inteligencja, -1 Cnota)")
        }
        if (hero.age > 60) {
            hero.strength = (hero.strength - 1).coerceAtLeast(1)
            state.logEntries.add("${hero.name} odczuwa ciężar lat. (-1 Siła)")
        }
        if (hero.age > 40) {
            hero.agility = (hero.agility - 1).coerceAtLeast(1)
            state.logEntries.add("${hero.name} zaczyna odczuwać spowolnienie ruchów. (-1 Zwinność)")
        }
    }
}
