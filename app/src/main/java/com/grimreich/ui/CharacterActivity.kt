package com.grimreich.ui

import android.os.Bundle
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.core.GameRepository
import com.grimreich.core.Hero
import com.grimreich.systems.DialogueManager

class CharacterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character)

        val heroId = intent.getStringExtra("heroId")
        android.util.Log.d("GrimReich", "Opening character sheet for ID: $heroId")
        
        val hero = GameRepository.state.party.find { it.id == heroId }
        
        if (hero != null) {
            renderHero(hero)
        } else {
            android.util.Log.e("GrimReich", "Hero NOT FOUND in party!")
            // Fallback: try first hero
            val firstHero = GameRepository.state.party.firstOrNull()
            if (firstHero != null) {
                renderHero(firstHero)
            } else {
                finish()
            }
        }

        findViewById<Button>(R.id.btnExitCharacter).setOnClickListener {
            finish()
        }
    }

    private fun renderHero(hero: Hero) {
        findViewById<TextView>(R.id.tvHeroName).text = hero.name
        findViewById<TextView>(R.id.tvHeroRole).text = hero.currentCareer?.name?.uppercase() ?: "WĘDROWIEC"
        findViewById<ProgressBar>(R.id.pbHeroHP).progress = if (hero.maxHp > 0) (hero.hp * 100 / hero.maxHp).coerceAtMost(100) else 0
        findViewById<ProgressBar>(R.id.pbHeroSanity).progress = hero.sanity

        // CANONICAL PORTRAIT MAPPING
        val portraitName = DialogueManager.getPortrait(hero.currentCareer?.name ?: "rogue")
        val portResId = resources.getIdentifier(portraitName, "drawable", packageName)
        if (portResId != 0) {
            findViewById<ImageView>(R.id.ivHeroPortrait).setImageResource(portResId)
        }

        val grid = findViewById<GridLayout>(R.id.glStats)
        grid.removeAllViews()

        val stats = mapOf(
            "Siła" to hero.strength,
            "Zręczność" to hero.agility,
            "Percepcja" to hero.perception,
            "Inteligencja" to hero.intelligence,
            "Wytrzymałość" to hero.endurance,
            "Charyzma" to hero.charisma,
            "Pobożność" to hero.piety
        )

        stats.forEach { (key, value) ->
            val label = TextView(this).apply {
                text = "${key.uppercase()}: "
                styleToGrim(true)
            }
            val valView = TextView(this).apply {
                text = value.toString()
                styleToGrim(false)
            }
            grid.addView(label)
            grid.addView(valView)
        }
    }

    private fun TextView.styleToGrim(isLabel: Boolean) {
        this.setTextColor(androidx.core.content.ContextCompat.getColor(context, 
            if (isLabel) R.color.grimTextPrimary else R.color.grimGold))
        this.setPadding(8, 8, 32, 8)
        this.textSize = 14f
    }
}
