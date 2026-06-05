package com.grimreich.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.core.GameRepository
import com.grimreich.systems.EncounterSystem

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

        encounter.choices.forEach { choice ->
            val btn = Button(this)
            btn.text = choice.label
            btn.setOnClickListener {
                val resultText = choice.effect(GameRepository.state)
                tvResult.text = resultText
                container.visibility = View.GONE
                btnClose.visibility = View.VISIBLE
            }
            container.addView(btn)
        }

        btnClose.setOnClickListener {
            EncounterSystem.activeEncounter = null
            finish()
        }
    }
}
