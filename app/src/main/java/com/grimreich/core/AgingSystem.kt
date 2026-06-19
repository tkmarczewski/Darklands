package com.grimreich.core

import javax.inject.Inject
import javax.inject.Singleton

data class AgingEffect(
    val strDelta: Int = 0,
    val agiDelta: Int = 0,
    val intDelta: Int = 0,
    val virtueDelta: Int = 0,
    val description: String = ""
)

@Singleton
class AgingSystem @Inject constructor(
    private val gameRepository: GameRepository
) {
    fun applyAging(hero: Hero) {
        if (hero.age > 60) {
            hero.strength -= 1
            gameRepository.log("${hero.name} odczuwa upływ czasu.")
        }
        gameRepository.persistCurrentState()
    }
}
