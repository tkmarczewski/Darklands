package com.darklandsmobile.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.darklandsmobile.core.PlayerState
import com.darklandsmobile.databinding.ActivityMainBinding
import com.darklandsmobile.systems.GameLoopController
import com.darklandsmobile.systems.QuestJournalSystem

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
                === DARKLANDS MVP ===
                Bootstrap complete.
                Current city: ${playerState.currentCityId}
                Gold: ${playerState.gold}
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
                    showText("No available quests in ${playerState.currentCityId}.")
                } else {
                    playerState = GameLoopController.acceptQuest(playerState, firstQuest.id)
                    showText(
                        """
                        Accepted quest: ${firstQuest.title}
                        Quest id: ${firstQuest.id}
                        """.trimIndent()
                    )
                }
            }
        }

        binding.btnTravel.setOnClickListener {
            guardInitialized {
                if (playerState.activeQuestId == null) {
                    showText("No active quest. Accept a quest first.")
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
                    showText("No active quest to resolve.")
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

        binding.tvMain.text =
            """
            === DARKLANDS MVP ===
            Press "Bootstrap" to initialize the vertical slice.
            """.trimIndent()
    }

    private fun guardInitialized(action: () -> Unit) {
        if (!initialized) {
            showText("Initialize the MVP first with Bootstrap.")
            return
        }
        action()
    }

    private fun showText(text: String) {
        binding.tvMain.text = text
    }
}