package com.darklandsmobile.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.darklandsmobile.core.AgingSystem
import com.darklandsmobile.core.CareerChain
import com.darklandsmobile.core.CharacterFactory
import com.darklandsmobile.core.GameRepository
import com.darklandsmobile.databinding.ActivityCharacterBinding

class CharacterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCharacterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCharacterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        render()
        setupButtons()
    }

    private fun render() {
        val g = GameRepository.state
        val hero = g.party.members.firstOrNull() ?: return
        val sb = StringBuilder()

        sb.appendLine("=== POSTAĆ ===")
        sb.appendLine()
        sb.appendLine("Imię: ${hero.name}")
        sb.appendLine("Wiek: ${hero.age} (${AgingSystem.ageDescription(hero.age)})")
        sb.appendLine()

        // Kariera
        val careerName = hero.currentCareer?.displayName ?: "Brak"
        sb.appendLine("Kariera: $careerName")
        if (hero.careerHistory.isNotEmpty()) {
            sb.appendLine("Historia karier: ${hero.careerHistory.joinToString(", ") { it.career.displayName }}")
        }
        sb.appendLine()

        // Statystyki
        sb.appendLine("STATYSTYKI")
        sb.appendLine("HP: ${hero.hp} / ${hero.maxHp}")
        sb.appendLine("Siła: ${hero.strength}")
        sb.appendLine("Zwinność: ${hero.agility}")
        sb.appendLine("Inteligencja: ${hero.intelligence}")
        sb.appendLine("Wytrzymałość: ${hero.endurance}")
        sb.appendLine("Charyzma: ${hero.charisma}")
        sb.appendLine("Pobożność: ${hero.piety}")
        sb.appendLine("Cnota: ${hero.virtue}")
        sb.appendLine()

        // Dostępne kariery
        val available = CareerChain.availableCareers(hero)
        if (available.isNotEmpty()) {
            sb.appendLine("DOSTĘPNE KARIERY")
            available.forEach { c ->
                sb.appendLine("  ${c.displayName}: ${c.description}")
            }
        } else {
            sb.appendLine("Brak dostępnych karier dla tego bohatera.")
        }

        binding.textCharacter.text = sb.toString()
    }

    private fun setupButtons() {
        // Przyciski do tworzenia bohatera z szablonu
        binding.btnKnight?.setOnClickListener {
            val hero = CharacterFactory.createKnight("Rycerz")
            val g = GameRepository.state
            g.party.members.clear()
            g.party.members.add(hero)
            render()
        }
        binding.btnScholar?.setOnClickListener {
            val hero = CharacterFactory.createScholar("Uczony")
            val g = GameRepository.state
            g.party.members.clear()
            g.party.members.add(hero)
            render()
        }
        binding.btnMercenary?.setOnClickListener {
            val hero = CharacterFactory.createMercenary("Najemnik")
            val g = GameRepository.state
            g.party.members.clear()
            g.party.members.add(hero)
            render()
        }
        binding.btnMonk?.setOnClickListener {
            val hero = CharacterFactory.createMonk("Mnich")
            val g = GameRepository.state
            g.party.members.clear()
            g.party.members.add(hero)
            render()
        }
        binding.btnAge?.setOnClickListener {
            val g = GameRepository.state
            val hero = g.party.members.firstOrNull() ?: return@setOnClickListener
            val (aged, msgs) = AgingSystem.applyAging(hero, 5)
            g.party.members[0] = aged
            render()
        }
    }
}
