package com.darklandsmobile.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.darklandsmobile.R
import com.darklandsmobile.core.GameRepository
import com.darklandsmobile.core.PartyRepository
import com.darklandsmobile.core.WorldState

class EncounterActivity : AppCompatActivity() {

    private lateinit var tvInfo: TextView
    private lateinit var btnFight: Button
    private lateinit var btnFlee: Button
    private lateinit var btnNegotiate: Button
    private lateinit var btnBack: Button

    private var encounterType: String = "bandits"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_encounter)

        tvInfo       = findViewById(R.id.tvEncounterInfo)
        btnFight     = findViewById(R.id.btnFight)
        btnFlee      = findViewById(R.id.btnFlee)
        btnNegotiate = findViewById(R.id.btnNegotiate)
        btnBack      = findViewById(R.id.btnEncounterBack)

        encounterType = intent.getStringExtra("encounter_type") ?: "bandits"

        renderEncounter()

        btnFight.setOnClickListener {
            val state = GameRepository.state
            state.lastEncounter = encounterType
            tvInfo.append("\nWybrano: Walka! Przejdz do ekranu walki.")
            btnFight.isEnabled = false
            btnFlee.isEnabled = false
            btnNegotiate.isEnabled = false
        }

        btnFlee.setOnClickListener {
            val state = GameRepository.state
            val hero  = PartyRepository.activeHero()
            val agilityCheck = (hero?.agility ?: 5) >= 6
            if (agilityCheck) {
                tvInfo.append("\nUciekles skutecznie!")
                state.lastEncounter = "fled"
            } else {
                tvInfo.append("\nNie udalo sie uciec! Jestes zmuszony walczyc.")
            }
            btnFlee.isEnabled = false
        }

        btnNegotiate.setOnClickListener {
            val hero = PartyRepository.activeHero()
            val reputationBonus = if ((hero?.reputation ?: 0) > 50) " [Reputacja pomaga]"
                                  else ""
            tvInfo.append("\nProba negocjacji...$reputationBonus")
            val success = (1..100).random() <= (30 + (hero?.reputation ?: 0) / 5)
            if (success) {
                tvInfo.append("\nNegocjacja udana - wrogowie odeszli.")
                GameRepository.state.lastEncounter = "negotiated"
            } else {
                tvInfo.append("\nNegocjacja nie powiodla sie - do walki!")
            }
            btnNegotiate.isEnabled = false
        }

        btnBack.setOnClickListener { finish() }
    }

    private fun renderEncounter() {
        val state = GameRepository.state
        val hero  = PartyRepository.activeHero()
        tvInfo.text = buildString {
            appendLine("=== SPOTKANIE ===")
            appendLine("Dzien: ${state.day}  Pora: ${state.timeOfDay}")
            appendLine("Region: ${state.region}")
            appendLine()
            when (encounterType) {
                "bandits"    -> appendLine("Na drodze napotykasz band zbrojnych rozbojnikow!")
                "raubritter" -> appendLine("Rycerz-rabus Raubritter zagrodziI Ci droge!")
                "wolves"     -> appendLine("Z lasu wybiega wataha wilkow!")
                "merchants"  -> appendLine("Napotykasz karawane kupcow.")
                else         -> appendLine("Nieznane spotkanie: $encounterType")
            }
            appendLine()
            if (hero != null) {
                appendLine("Bohater: ${hero.name}")
                appendLine("Zdrowie: ${hero.health}/${hero.maxHealth}")
            }
        }
    }
}
