package com.grimreich.ui

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.core.GameRepository
import com.grimreich.core.Hero

class CharacterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character)

        val heroId = intent.getStringExtra("heroId") ?: GameRepository.state.activeHeroId
        val hero = GameRepository.state.party.find { it.id == heroId }
        
        if (hero != null) {
            renderHero(hero)
        }

        findViewById<Button>(R.id.btnBackFromChar).setOnClickListener {
            finish()
        }
    }

    private fun renderHero(hero: Hero) {
        findViewById<TextView>(R.id.tvHeroName).text = hero.name.uppercase()
        findViewById<TextView>(R.id.tvHeroLevel).text = "Poziom ${hero.level}"
        
        val hpPercent = (hero.hp * 100 / hero.maxHp).coerceIn(0, 100)
        findViewById<TextView>(R.id.tvHpStatus).text = "HP: ${hero.hp}/${hero.maxHp}"
        findViewById<ProgressBar>(R.id.pbHp).progress = hpPercent

        findViewById<TextView>(R.id.tvSanityStatus).text = "Poczytalność: ${hero.sanity}%"
        findViewById<ProgressBar>(R.id.pbSanity).progress = hero.sanity

        // Portrait
        val portrait = findViewById<ImageView>(R.id.ivPortrait)
        val resId = resources.getIdentifier(hero.portraitRes, "drawable", packageName).let {
            if (it == 0) R.drawable.port_knight else it
        }
        portrait.setImageResource(resId)

        // Attributes
        findViewById<TextView>(R.id.tvStatStr).text = "Siła: ${hero.strength}"
        findViewById<TextView>(R.id.tvStatAgi).text = "Zręczność: ${hero.agility}"
        findViewById<TextView>(R.id.tvStatPer).text = "Percepcja: ${hero.perception}"
        findViewById<TextView>(R.id.tvStatInt).text = "Inteligencja: ${hero.intelligence}"
        findViewById<TextView>(R.id.tvStatEnd).text = "Wytrzymałość: ${hero.endurance}"
        findViewById<TextView>(R.id.tvStatCha).text = "Charyzma: ${hero.charisma}"
        findViewById<TextView>(R.id.tvStatPie).text = "Pobożność: ${hero.piety}"

        // Equipment
        val inv = com.grimreich.systems.InventorySystem
        val gear = inv.getEquippedItems(hero)
        findViewById<TextView>(R.id.tvEquippedWeapon).text = "Broń: ${gear.weapon?.name ?: "Brak"}"
        findViewById<TextView>(R.id.tvEquippedArmor).text = "Pancerz: ${gear.bodyArmor?.name ?: "Brak"}"
        
        // Traits & Skills
        findViewById<TextView>(R.id.tvTrait).text = hero.trait?.name ?: "Brak cechy"
        findViewById<TextView>(R.id.tvAbilities).text = if (hero.abilities.isEmpty()) "Brak zdolności" else hero.abilities.joinToString("\n") { it.name }
        
        val topSkills = hero.skills.entries.sortedByDescending { it.value }.take(5)
        findViewById<TextView>(R.id.tvSkills).text = topSkills.joinToString("\n") { "${it.key}: ${it.value}%" }
    }
}
