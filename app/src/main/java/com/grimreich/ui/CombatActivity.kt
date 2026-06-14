package com.grimreich.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.core.GameRepository
import com.grimreich.systems.CombatSystem

class CombatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_combat)

        if (!GameRepository.state.combat.active) {
            // No active combat - start a random encounter if triggered from quest
            val questId = GameRepository.state.pendingQuestId
            if (questId != null) {
                // Start a combat encounter for this quest
                CombatSystem.startEncounterForQuest(questId)
            } else {
                // Start a default random encounter
                CombatSystem.startRandomEncounter()
            }
        }

        render()

        findViewById<Button>(R.id.btnAttack).setOnClickListener {
            if (GameRepository.state.combat.active) {
                CombatSystem.playerAttack()
                render()
            }
        }

        findViewById<Button>(R.id.btnDefend).setOnClickListener {
            if (GameRepository.state.combat.active) {
                CombatSystem.playerDefend()
                render()
            }
        }

        findViewById<Button>(R.id.btnUseMist).setOnClickListener {
            if (GameRepository.state.combat.active) {
                CombatSystem.playerUseSpecial("MIST")
                render()
            }
        }

        findViewById<Button>(R.id.btnUseBlood).setOnClickListener {
            if (GameRepository.state.combat.active) {
                CombatSystem.playerUseSpecial("BLOOD")
                render()
            }
        }

        findViewById<Button>(R.id.btnUseReflection).setOnClickListener {
            if (GameRepository.state.combat.active) {
                CombatSystem.playerUseSpecial("REFLECTION")
                render()
            }
        }

        findViewById<Button>(R.id.btnFlee).setOnClickListener {
            // Clear pending quest if fleeing (quest not completed)
            GameRepository.state.pendingQuestId = null
            finish()
        }
    }

    private fun render() {
        val c = GameRepository.state.combat
        val log = c.log.takeLast(8).joinToString("\n")
        findViewById<TextView>(R.id.tvCombatLog).text = log
        findViewById<TextView>(R.id.tvCombatTitle).text =
            if (c.active) "⚔ WALKA: ${c.enemyName}" else "⚔ KONIEC WALKI"

        val btnAttack = findViewById<Button>(R.id.btnAttack)
        val btnDefend = findViewById<Button>(R.id.btnDefend)
        val btnMist = findViewById<Button>(R.id.btnUseMist)
        val btnBlood = findViewById<Button>(R.id.btnUseBlood)
        val btnReflection = findViewById<Button>(R.id.btnUseReflection)
        val btnFlee = findViewById<Button>(R.id.btnFlee)

        if (!c.active) {
            btnAttack.isEnabled = false
            btnDefend.isEnabled = false
            btnMist.isEnabled = false
            btnBlood.isEnabled = false
            btnReflection.isEnabled = false
            btnFlee.text = "POWRÓT"

            // Check victory: all enemies dead (log contains win message or enemy hp <= 0)
            val playerAlive = GameRepository.state.party.any { it.hp > 0 }
            if (playerAlive) {
                // Victory - complete pending quest if any
                val pendingId = GameRepository.state.pendingQuestId
                if (pendingId != null) {
                    try {
                        val completed = com.grimreich.systems.QuestSystem.complete(pendingId)
                        GameRepository.state.pendingQuestId = null
                        val reward = completed.rewardGold
                        val msg = "⚔ ZWY CIĘSTWO!\n\nZadanie ukończone: ${completed.title}\n+$reward złota otrzymano."
                        UiUtils.showNarrativePopup(this, "ZADANIE UKOŃCZONE", msg)
                    } catch (_: Exception) {
                        GameRepository.state.pendingQuestId = null
                    }
                }
            } else {
                // Defeat - clear pending quest
                GameRepository.state.pendingQuestId = null
            }
        } else {
            btnAttack.isEnabled = true
            btnDefend.isEnabled = true
            btnMist.isEnabled = true
            btnBlood.isEnabled = true
            btnReflection.isEnabled = true
            btnFlee.text = "UCIECZKA"
        }
    }
}
