package com.grimreich.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.systems.ReputationSystem

class ReputationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reputation)

        val cityRep = ReputationSystem.allCities().entries.joinToString("\n") { (city, rep) ->
            "Region: $city | Wpływy: $rep"
        }
        val factionRep = listOf("knights", "merchants", "church", "commoners").joinToString("\n") { f ->
            "Frakcja: $f | Reputacja: ${ReputationSystem.getFactionRep(f)}"
        }

        findViewById<TextView>(R.id.reputationStatus).text = buildString {
            appendLine("=== REPUTACJA REGIONALNA ===")
            appendLine(cityRep.ifBlank { "Brak danych." })
            appendLine()
            appendLine("=== REPUTACJA FRAKCJI ===")
            appendLine(factionRep)
        }

        findViewById<Button>(R.id.btnExitReputation).setOnClickListener {
            finish()
        }
    }
}
