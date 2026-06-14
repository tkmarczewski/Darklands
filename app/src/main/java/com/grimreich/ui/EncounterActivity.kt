package com.grimreich.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.core.GameRepository
import com.grimreich.systems.CombatSystem
import com.grimreich.systems.EncounterSystem
import com.grimreich.systems.EncounterType

class EncounterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_encounter)

        val encounter = EncounterSystem.activeEncounter ?: run {
            finish()
            return
        }

        val tvTitle = findViewById<TextView>(R.id.tvEncounterTitle)
        val tvDesc = findViewById<TextView>(R.id.tvEncounterDesc)
        val tvResult = findViewById<TextView>(R.id.tvEncounterResult)
        val container = findViewById<LinearLayout>(R.id.choicesContainer)
        val btnClose = findViewById<Button>(R.id.btnEncounterClose)

        tvTitle.text = encounter.title
        tvDesc.text = encounter.description

        // If this is a COMBAT encounter, offer a WALCZ button that launches CombatActivity
        if (encounter.type == EncounterType.COMBAT) {
            val btnFight = Button(this).apply {
                text = "⚔ WALCZ!"
                setBackgroundColor(android.graphics.Color.parseColor("#80330000"))
                setTextColor(android.graphics.Color.parseColor("#FFD700"))
                setPadding(16, 24, 16, 24)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.setMargins(0, 0, 0, 8) }
                setOnClickListener {
                    // Start combat using enemy from encounter description
                    CombatSystem.startRandomEncounter()
                    startActivity(Intent(this@EncounterActivity, CombatActivity::class.java))
                    finish()
                }
            }
            container.addView(btnFight)

            val btnFlee = Button(this).apply {
                text = "🏳 UCIEKNIJ"
                setBackgroundColor(android.graphics.Color.parseColor("#80000033"))
                setTextColor(android.graphics.Color.parseColor("#AAAAAA"))
                setPadding(16, 24, 16, 24)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.setMargins(0, 0, 0, 8) }
                setOnClickListener {
                    // Fleeing costs HP
                    GameRepository.state.party.forEach { it.hp = (it.hp - 5).coerceAtLeast(0) }
                    tvResult.text = "Ucieczka! Stracono 5 HP. Wrogowie krzyczą za Wami."
                    tvResult.visibility = View.VISIBLE
                    container.visibility = View.GONE
                    btnClose.visibility = View.VISIBLE
                }
            }
            container.addView(btnFlee)
        } else {
            // Interactive/Resource encounter - show choices
            encounter.choices.forEach { choice ->
                val btn = Button(this).apply {
                    text = choice.label
                    setBackgroundColor(android.graphics.Color.parseColor("#80000033"))
                    setTextColor(android.graphics.Color.parseColor("#FFD700"))
                    setPadding(16, 24, 16, 24)
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).also { it.setMargins(0, 0, 0, 8) }
                    setOnClickListener {
                        val resultText = choice.effect(GameRepository.state)
                        tvResult.text = resultText
                        tvResult.visibility = View.VISIBLE
                        container.visibility = View.GONE
                        btnClose.visibility = View.VISIBLE
                    }
                }
                container.addView(btn)
            }
        }

        btnClose.setOnClickListener {
            EncounterSystem.activeEncounter = null
            finish()
        }
    }
}
