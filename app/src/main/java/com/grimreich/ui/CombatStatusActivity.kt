package com.grimreich.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.core.GameRepository

class CombatStatusActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_combat_status)

        val g = GameRepository.state
        val partyStatus = g.party.joinToString("\n\n") { h ->
            "${h.name}: HP=${h.hp}/${h.maxHp}, Sanity=${h.sanity}%, Morale=${h.morale}%"
        }
        
        findViewById<TextView>(R.id.combatStatusText).text = if (partyStatus.isBlank()) {
            "Brak bohaterów w drużynie."
        } else {
            partyStatus
        }

        findViewById<Button>(R.id.btnExitCombatStatus).setOnClickListener {
            finish()
        }
    }
}
