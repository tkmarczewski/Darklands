package com.grimreich.ui

import android.os.Bundle
import android.view.View
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

        val heroId = intent.getStringExtra("heroId")
        val hero = GameRepository.state.party.find { it.id == heroId } ?: GameRepository.state.party.firstOrNull()

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
        
        findViewById<TextView>(R.id.tvHpStatus).text = "HP: ${hero.hp}/${hero.maxHp}"
        findViewById<ProgressBar>(R.id.pbHp).progress = (hero.hp * 100 / hero.maxHp)
        
        findViewById<TextView>(R.id.tvSanityStatus).text = "Poczytalność: ${hero.sanity}%"
        findViewById<ProgressBar>(R.id.pbSanity).progress = hero.sanity

        // Attributes
        renderAttr(R.id.viewStr, "Siła", hero.strength, R.drawable.ic_stats_str)
        renderAttr(R.id.viewAgi, "Zręczność", hero.agility, R.drawable.ic_stats_dex)
        renderAttr(R.id.viewPer, "Percepcja", hero.perception, R.drawable.ic_stats_perception)
        renderAttr(R.id.viewInt, "Inteligencja", hero.intelligence, R.drawable.ic_stats_know)
        renderAttr(R.id.viewEnd, "Wytrzymałość", hero.endurance, R.drawable.ic_stats_endurance)
        renderAttr(R.id.viewCha, "Charyzma", hero.charisma, R.drawable.ic_stats_cha)
        renderAttr(R.id.viewPie, "Pobożność", hero.piety, R.drawable.ic_stats_will)

        // Trait
        findViewById<TextView>(R.id.tvTrait).text = hero.trait?.let { "${it.displayName}: ${it.description}" } ?: "Brak cechy"

        // Abilities
        findViewById<TextView>(R.id.tvAbilities).text = if (hero.abilities.isEmpty()) {
            "Brak zdolności"
        } else {
            hero.abilities.joinToString("\n") { "- ${it.name}: ${it.description}" }
        }

        // Skills
        findViewById<TextView>(R.id.tvSkills).text = hero.skills.entries
            .filter { it.value > 5 }
            .joinToString(", ") { "${it.key}: ${it.value}%" }
            .ifEmpty { "Wszystkie umiejętności na poziomie podstawowym." }
    }

    private fun renderAttr(viewId: Int, label: String, value: Int, iconRes: Int) {
        val view = findViewById<View>(viewId)
        view.findViewById<TextView>(R.id.tvAttrName).text = label
        view.findViewById<TextView>(R.id.tvAttrValue).text = value.toString()
        view.findViewById<ImageView>(R.id.ivAttrIcon).setImageResource(iconRes)
    }
}
