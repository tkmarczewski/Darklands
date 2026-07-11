package com.grimreich.systems

import com.grimreich.core.GameRepository
import javax.inject.Inject
import javax.inject.Singleton

enum class CollapseScenario {
    MIST_OBLIVION, BLOOD_RUIN, REFLECTION_RECKONING, FULLNESS_ASCENSION, CHAOS_DOMINION, ZERO_END
}

@Singleton
class CollapseEngine @Inject constructor(
    private val gameRepository: GameRepository,
    private val worldStabilitySystem: WorldStabilitySystem
) {
    var activeScenario: CollapseScenario? = null

    /**
     * Główny tick upadku świata.
     * FIX: Zmieniono mutacje bezpośrednie na WorldStabilitySystem, co zapewnia
     * clamping oraz poprawne logowanie zmian.
     */
    fun tick(reason: String = "Upływ czasu") {
        val state = gameRepository.currentState()
        
        // Zwiększamy postęp upadku
        worldStabilitySystem.advanceCollapse(0.01f, reason)

        // Jeżeli przekroczono próg, decydujemy o scenariuszu
        if (state.world.collapseProgress > 0.5f && state.world.collapseScenarioId == null) {
            gameRepository.updateState { s ->
                val scenario = decideScenario(s.prayer.faith, s.world.globalStability)
                s.world.collapseScenarioId = scenario.name
            }
        }

        val scenarioId = gameRepository.currentState().world.collapseScenarioId ?: return
        val scenario = try { CollapseScenario.valueOf(scenarioId) } catch (e: Exception) { null }

        scenario?.let { sc ->
            when (sc) {
                CollapseScenario.MIST_OBLIVION -> {
                    // Dodatkowy efekt scenariusza: wzrost echa
                    worldStabilitySystem.changeEcho(0.02f, "Scenariusz Upadku: ${sc.name}")
                }
                CollapseScenario.BLOOD_RUIN -> {
                    // Dodatkowy efekt scenariusza: obrażenia drużyny
                    gameRepository.updateState { s ->
                        s.party.forEach { h -> h.hp = (h.hp - 1).coerceAtLeast(0) }
                    }
                }
                else -> {}
            }
        }
    }

    private fun decideScenario(faith: Int, stability: Int): CollapseScenario {
        return when {
            faith > 70 -> CollapseScenario.FULLNESS_ASCENSION
            stability < 30 -> CollapseScenario.CHAOS_DOMINION
            else -> CollapseScenario.entries.toTypedArray().random()
        }
    }
}
