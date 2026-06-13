package com.grimreich.ui

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.core.GameRepository
import com.grimreich.core.Hero

class RecruitmentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recruitment)

        render()

        findViewById<Button>(R.id.btnExitRecruit).setOnClickListener {
            finish()
        }
    }

    private fun render() {
        val container = findViewById<LinearLayout>(R.id.recruitListContainer)
        container.removeAllViews()

        val hireables = GameRepository.state.hireableHeroes
        
        // SEED IF EMPTY
        if (hireables.isEmpty()) {
            repeat(4) {
                val name = com.grimreich.world.ProceduralNpcGenerator.generateName()
                val age = 18 + (Math.random() * 40).toInt()
                val career = com.grimreich.core.Career.values().filter { it.minAge <= age }.random()
                
                hireables.add(com.grimreich.core.Hero(
                    id = java.util.UUID.randomUUID().toString(),
                    name = name,
                    age = age,
                    currentCareer = career,
                    hp = 25 + (Math.random() * 15).toInt(),
                    maxHp = 40,
                    portraitRes = com.grimreich.systems.DialogueManager.getPortrait(career.name)
                ))
            }
        }

        if (hireables.isEmpty()) {
            findViewById<TextView>(R.id.tvRecruitStatus).text = "Karczma jest pusta... nikt nie szuka obecnie przygód."
            return
        }

        hireables.forEach { hero ->
            val btn = Button(this).apply {
                text = "${hero.name} (${hero.currentCareer?.name ?: "Wędrowiec"}) - 100 G"
                styleToGrim()
                setOnClickListener { tryHire(hero) }
            }
            container.addView(btn)
        }
    }

    private fun tryHire(hero: Hero) {
        val state = GameRepository.state
        if (state.gold < 100) {
            Toast.makeText(this, "Brak złota!", Toast.LENGTH_SHORT).show()
            return
        }
        if (state.party.size >= 4) {
            Toast.makeText(this, "Drużyna jest pełna!", Toast.LENGTH_SHORT).show()
            return
        }

        state.gold -= 100
        state.hireableHeroes.remove(hero)
        state.party.add(hero)
        render()
        Toast.makeText(this, "${hero.name} dołączył do drużyny!", Toast.LENGTH_SHORT).show()
    }

    private fun Button.styleToGrim() {
        this.setTextColor(androidx.core.content.ContextCompat.getColor(context, R.color.grimGold))
        this.setBackgroundColor(android.graphics.Color.parseColor("#80000000"))
        this.setPadding(16, 16, 16, 16)
        this.typeface = android.graphics.Typeface.MONOSPACE
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { setMargins(0, 0, 0, 12) }
        this.layoutParams = params
    }
}
