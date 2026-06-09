package com.grimreich.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.core.GameRepository

class CharacterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character)

        val heroId = intent.getStringExtra("heroId")
        val hero = GameRepository.state.party.find { it.id == heroId } ?: GameRepository.state.party.firstOrNull()

        val tvDetail = findViewById<TextView>(R.id.tvCharacterDetail)
        if (hero != null) {
            tvDetail.text = buildString {
                appendLine("BOHATER: ${hero.name.uppercase()}")
                appendLine("Wiek: ${hero.age} | Poziom: ${hero.level}")
                appendLine("HP: ${hero.hp}/${hero.maxHp}")
                appendLine("Morale: ${hero.morale}% | Poczytalność: ${hero.sanity}%")
                appendLine("Skażenie: ${hero.corruption}% | Uznanie: ${hero.divineFavor}")
                appendLine()
                appendLine("--- CECHY ---")
                appendLine("Siła: ${hero.strength} | Zręczność: ${hero.agility}")
                appendLine("Percepcja: ${hero.perception} | Inteligencja: ${hero.intelligence}")
                appendLine("Wytrzymałość: ${hero.endurance} | Charyzma: ${hero.charisma}")
                appendLine("Pobożność: ${hero.piety}")
                appendLine()
                appendLine("--- HISTORIA ---")
                appendLine("To echo pękniętego świata, niosące w sobie iskry dawnej wielkości i cienie nadchodzącego upadku.")
                appendLine("Z każdym krokiem po ziemiach Reichu, jego Cień gęstnieje, a Iskra staje się coraz bardziej niestabilna.")
            }
        } else {
            tvDetail.text = "Brak wybranego bohatera."
        }

        findViewById<Button>(R.id.btnBackFromChar).setOnClickListener {
            finish()
        }
    }
}
