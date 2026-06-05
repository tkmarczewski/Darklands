package com.grimreich.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.core.PlayerState
import com.grimreich.databinding.ActivityMainBinding
import com.grimreich.systems.GameLoopController
import com.grimreich.systems.QuestJournalSystem

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var playerState: PlayerState = PlayerState()
    private var initialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBootstrap.setOnClickListener {
            playerState = GameLoopController.bootstrap(seed = 1)
            initialized = true
            showText(
                """
                === GRIMREICH 1.0 ===
                Bootstrap complete.
                Miasto startowe: ${playerState.currentCityId}
                Złoto: ${playerState.gold}
                """.trimIndent()
            )
        }

        binding.btnCity.setOnClickListener {
            guardInitialized {
                val city = GameLoopController.cityScreen(playerState)
                showText(GameplayScreens.renderCity(city))
            }
        }

        binding.btnAcceptQuest.setOnClickListener {
            guardInitialized {
                val city = GameLoopController.cityScreen(playerState)
                val firstQuest = city.availableQuests.firstOrNull()
                if (firstQuest == null) {
                    showText("Brak dostępnych questów w ${playerState.currentCityId}.")
                } else {
                    playerState = GameLoopController.acceptQuest(playerState, firstQuest.id)
                    showText(
                        """Przyjęto quest: ${firstQuest.title}
ID questa: ${firstQuest.id}""".trimIndent()
                    )
                }
            }
        }

        binding.btnTravel.setOnClickListener {
            guardInitialized {
                if (playerState.activeQuestId == null) {
                    showText("Brak aktywnego questa. Najpierw przyjmij zadanie.")
                } else {
                    val (updated, travel) = GameLoopController.travelToQuest(playerState)
                    playerState = updated
                    showText(GameplayScreens.renderTravel(travel))
                }
            }
        }

        binding.btnResolve.setOnClickListener {
            guardInitialized {
                if (playerState.activeQuestId == null) {
                    showText("Brak aktywnego questa do rozwiązania.")
                } else {
                    val (updated, resolution) = GameLoopController.resolveActiveQuest(playerState)
                    playerState = updated
                    showText(GameplayScreens.renderResolution(resolution))
                }
            }
        }

        binding.btnJournal.setOnClickListener {
            guardInitialized {
                val journal = QuestJournalSystem.build(playerState)
                showText(GameplayScreens.renderJournal(journal))
            }
        }

        binding.tvMain.text = getString(com.grimreich.R.string.grimreich_mvp_prompt)
    }

    private fun guardInitialized(action: () -> Unit) {
        if (!initialized) {
            showText("Najpierw zainicjuj grę przez Bootstrap.")
            return
        }
        action()
    }

    private fun showText(text: String) {
        binding.tvMain.text = text
    }
}
