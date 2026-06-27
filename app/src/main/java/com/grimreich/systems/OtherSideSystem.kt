package com.grimreich.systems

import com.grimreich.core.GameRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OtherSideSystem @Inject constructor(
    private val gameRepository: GameRepository
) {
    fun enterOtherSide() {
        gameRepository.updateState { s ->
            s.world.echoIntensity = (s.world.echoIntensity + 0.1f).coerceAtMost(1.0f)
            s.logEntries.add("Brama do Drugiej Strony uchylona. Echo gęstnieje.")
        }
    }
}
