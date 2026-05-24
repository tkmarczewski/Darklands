package com.darklandsmobile.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.darklandsmobile.core.AgingSystem
import com.darklandsmobile.core.CareerChain
import com.darklandsmobile.core.GameRepository
import com.darklandsmobile.databinding.ActivityCharacterBinding

// Ekran postaci - wypisuje atrybuty pierwszego bohatera z druzyny + dostepne kariery.
// Layout (activity_character.xml) ma jedno TextView (tvCharacter); kreacja postaci uruchamia sie z Hub.
class CharacterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCharacterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCharacterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        render()
    }

    private fun render() {
        val g = GameRepository.state
        val hero = g.party.firstOrNull() ?: run {
            binding.tvCharacter.text = "Brak bohatera w druzynie."
            return
        }
        val sb = StringBuilder()

        sb.appendLine("=== POSTAC ===")
        sb.appendLine()
        sb.appendLine("Imie: ${hero.name}")
        sb.appendLine("Wiek: ${hero.age} (${AgingSystem.ageDescription(hero.age)})")
        sb.appendLine()

        val careerName = hero.currentCareer?.displayName ?: "Brak"
        sb.appendLine("Kariera: $careerName")
        if (hero.careerHistory.isNotEmpty()) {
            sb.appendLine("Historia karier: ${hero.careerHistory.joinToString(", ") { it.career.displayName }}")
        }
        sb.appendLine()

        sb.appendLine("STATYSTYKI")
        sb.appendLine("HP: ${hero.hp} / ${hero.maxHp}")
        sb.appendLine("Sila: ${hero.strength}")
        sb.appendLine("Zwinnosc: ${hero.agility}")
        sb.appendLine("Inteligencja: ${hero.intelligence}")
        sb.appendLine("Wytrzymalosc: ${hero.endurance}")
        sb.appendLine("Charyzma: ${hero.charisma}")
        sb.appendLine("Poboznosc: ${hero.piety}")
        sb.appendLine("Cnota: ${hero.virtue}")
        sb.appendLine()

        val available = CareerChain.availableCareers(hero)
        if (available.isNotEmpty()) {
            sb.appendLine("DOSTEPNE KARIERY")
            available.forEach { c ->
                sb.appendLine("  ${c.displayName}: ${c.description}")
            }
        } else {
            sb.appendLine("Brak dostepnych karier dla tego bohatera.")
        }

        binding.tvCharacter.text = sb.toString()
    }
}
