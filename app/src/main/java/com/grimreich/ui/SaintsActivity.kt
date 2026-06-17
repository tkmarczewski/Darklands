package com.grimreich.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.core.GameRepository
import com.grimreich.core.SaintCatalogue
import com.grimreich.systems.ChurchSystem

class SaintsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saints)

        render()

        findViewById<Button>(R.id.btnPray).setOnClickListener {
            val state = GameRepository.state
            val hero = state.party.find { it.id == state.activeHeroId } ?: state.party.firstOrNull() ?: return@setOnClickListener
            val msg = ChurchSystem.pray(hero)
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            render()
        }

        findViewById<Button>(R.id.btnCleanse).setOnClickListener {
            val state = GameRepository.state
            val hero = state.party.find { it.id == state.activeHeroId } ?: state.party.firstOrNull() ?: return@setOnClickListener
            val msg = ChurchSystem.cleanseRelic(hero)
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            render()
        }

        findViewById<Button>(R.id.btnExitSaints).setOnClickListener {
            finish()
        }
    }

    private fun render() {
        val saintsList = SaintCatalogue.all()
        val saintsText = if (saintsList.isEmpty()) "Brak świętych." else saintsList.joinToString("\n\n") { saint ->
            "${saint.name}\n  domain: ${saint.domain}\n  patronage: ${saint.patronage}"
        }
        
        val g = GameRepository.state
        val partyStatus = g.party.joinToString("\n") { h ->
            "${h.name}: Favor=${h.divineFavor}, Virtue=${h.virtue}, Corruption=${h.corruption}, Sanity=${h.sanity}%"
        }

        findViewById<TextView>(R.id.saintsStatus).text = buildString {
            append("=== TWOJA DRUŻYNA ===\n")
            append(if (partyStatus.isBlank()) "Brak bohaterów.\n" else partyStatus + "\n")
            append("\n")
            append("=== KATALOG ŚWIĘTYCH ===\n")
            append(saintsText + "\n")
        }
    }
}
