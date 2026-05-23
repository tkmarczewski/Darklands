package com.darklandsmobile.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.darklandsmobile.core.GameRepository
import com.darklandsmobile.databinding.ActivityMainBinding
import com.darklandsmobile.systems.QuestSystem
import com.darklandsmobile.systems.SaveSystem
import com.darklandsmobile.systems.TravelSystem

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        GameRepository.seed()
        render()

        binding.btnParty.setOnClickListener     { startActivity(Intent(this, PartyActivity::class.java)) }
        binding.btnCharacter.setOnClickListener { startActivity(Intent(this, CharacterActivity::class.java)) }
        binding.btnInventory.setOnClickListener { startActivity(Intent(this, InventoryActivity::class.java)) }
        binding.btnMap.setOnClickListener       { startActivity(Intent(this, MapActivity::class.java)) }
        binding.btnPrayer.setOnClickListener    { startActivity(Intent(this, PrayerActivity::class.java)) }
        binding.btnAlchemy.setOnClickListener   { startActivity(Intent(this, AlchemyActivity::class.java)) }
        binding.btnCombat.setOnClickListener    { startActivity(Intent(this, CombatActivity::class.java)) }
        binding.btnEndgame.setOnClickListener   { startActivity(Intent(this, EndgameActivity::class.java)) }
        binding.btnQuest.setOnClickListener {
            binding.tvMain.text = QuestSystem.start("forest_hermit")
            render()
        }
        binding.btnEvent.setOnClickListener {
            val w = GameRepository.state.world
            binding.tvMain.text = TravelSystem.travelTo(
                when (w.region) { "town" -> "road"; "road" -> "forest"; else -> "town" }
            )
            render()
        }
        binding.btnSave.setOnClickListener {
            val snap = SaveSystem.snapshot("Manual save")
            binding.tvMain.text = "Zapisano: v${snap.version}"
            render()
        }
    }

    private fun render() {
        val g = GameRepository.state
        val w = g.world
        binding.tvMain.text = buildString {
            appendLine("Lokalizacja: ${w.location} (${w.region})")
            appendLine("Dzien: ${w.day} | Pora roku: ${TravelSystem.getSeasonDisplay()}")
            appendLine("Pora dnia: ${w.timeOfDay} | Zmeczenie: ${w.fatigue}")
            appendLine("Spotkanie: ${w.lastEncounter}")
            appendLine()
            appendLine("Log:")
            g.logEntries.takeLast(5).forEach { appendLine("- $it") }
        }
    }
}
