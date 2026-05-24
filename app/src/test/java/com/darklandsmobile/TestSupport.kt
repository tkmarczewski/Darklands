package com.darklandsmobile

import com.darklandsmobile.core.GameRepository
import com.darklandsmobile.core.GameState

/**
 * Pomocnik testowy: ustawia czysty stan GameRepository przed kazdym testem.
 * Systemy gry dzialaja jako Kotlin `object`y na wspolnym singletonie GameRepository,
 * wiec trzeba je odswiezac miedzy testami, zeby uniknac przeciekania stanu.
 */
object TestSupport {
    fun resetRepoEmpty() {
        GameRepository.state = GameState()
    }

    fun resetRepoSeeded() {
        GameRepository.seed()
    }
}
