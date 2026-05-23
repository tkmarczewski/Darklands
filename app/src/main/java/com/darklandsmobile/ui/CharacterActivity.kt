package com.darklandsmobile.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.darklandsmobile.core.GameRepository
import com.darklandsmobile.databinding.ActivityCharacterBinding

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
        val hero = g.party.members.firstOrNull() ?: return
        val sb = StringBuilder()
        sb.appendLine("=== POSTAC ===")
        sb.appendLine()
        sb.appendLine("Imie: ${hero.name}")
        sb.appendLine("Klasa: ${hero.heroClass}")
        sb.appendLine()
        sb.appendLine("STATYSTYKI")
        sb.appendLine("HP: ${hero.hp} / ${hero.maxHp}")
        sb.appendLine("Sila: ${hero.strength}")
        sb.appendLine("Zrecznosc: ${hero.dexterity}")
        sb.appendLine("Inteligencja: ${hero.intelligence}")
        sb.appendLine("Wiara: ${hero.faith}")
        sb.appendLine("Reputacja: ${hero.reputation}")
        sb.appendLine()
        sb.appendLine("Zloto: ${g.party.gold}")
        binding.tvCharacter.text = sb.toString()
    }
}
