package com.grimreich.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.core.GameRepository
import com.grimreich.systems.CombatSystem
import com.grimreich.systems.QuestSystem
import com.grimreich.ui.UiUtils

class CombatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_combat)

        if (!GameRepository.state.combat.active) {
            val questId = GameRepository.state.pendingQuestId
            if (questId != null) {
                CombatSystem.startEncounterForQuest(questId)
            } else {
                CombatSystem.startRandomEncounter()
            }
        }

        setupButtons()
        render()
    }

    override fun onResume() {
        super.onResume()
        render()
    }

    private fun setupButtons() {
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
            val c = GameRepository.state.combat
            if (c.active) {
                // Force end combat (flee)
                c.active = false
                c.log.add("Uciekles z walki!")
                GameRepository.state.pendingQuestId = null
                render()
            } else {
                finish()
            }
        }
    }

    private fun render() {
        val c = GameRepository.state.combat
        val tvTitle = findViewById<TextView>(R.id.tvCombatTitle)
        val btnAttack = findViewById<Button>(R.id.btnAttack)
        val btnDefend = findViewById<Button>(R.id.btnDefend)
        val btnMist = findViewById<Button>(R.id.btnUseMist)
        val btnBlood = findViewById<Button>(R.id.btnUseBlood)
        val btnReflection = findViewById<Button>(R.id.btnUseReflection)
        val btnFlee = findViewById<Button>(R.id.btnFlee)

        if (c.active) {
            tvTitle.text = "% WALKA: ${c.enemyName}  HP: ${c.enemyHp}/${c.enemyMaxHp}"
            btnAttack.isEnabled = true
            btnDefend.isEnabled = true
            btnMist.isEnabled = true
            btnBlood.isEnabled = true
            btnReflection.isEnabled = true
            btnFlee.text = "UCIECZKA"
            btnFlee.isEnabled = true
        } else {
            // Combat ended - check victory or defeat
            val playerAlive = GameRepository.state.party.any { it.hp > 0 }
            val enemyDefeated = c.enemyHp <= 0

            tvTitle.text = if (enemyDefeated && playerAlive) "% ZWYCIESTWO!" else "% KONIEC WALKI"
            btnAttack.isEnabled = false
            btnDefend.isEnabled = false
            btnMist.isEnabled = false
            btnBlood.isEnabled = false
            btnReflection.isEnabled = false
            btnFlee.text = "POWROT"
            btnFlee.isEnabled = true

            if (enemyDefeated && playerAlive) {
                val pendingId = GameRepository.state.pendingQuestId
                if (pendingId != null) {
                    try {
                        val completed = QuestSystem.complete(pendingId)
                        GameRepository.state.pendingQuestId = null
                        val reward = completed.rewardGold
                        val msg = "% ZWYCIESTWO!\n\nZadanie ukonczone: ${completed.title}\n+$reward zlota otrzymano."
                        UiUtils.showNarrativePopup(this, "ZADANIE UKONCZONE", msg)
                    } catch (_: Exception) {
                        GameRepository.state.pendingQuestId = null
                    }
                }
            }
        }
    }
}
