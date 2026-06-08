package com.grimreich.ui

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
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

        findViewById<Button>(R.id.btnBackFromRecruit).setOnClickListener {
            finish()
        }
    }

    private fun render() {
        val container = findViewById<LinearLayout>(R.id.recruitContainer)
        container.removeAllViews()

        val candidates = listOf(
            Hero(id = "rec_1", name = "Borg Ironfoot", age = 34, hp = 30, maxHp = 30),
            Hero(id = "rec_2", name = "Elara Shadow", age = 22, hp = 20, maxHp = 20),
            Hero(id = "rec_3", name = "Father Silas", age = 50, hp = 25, maxHp = 25)
        )

        candidates.forEach { cand ->
            val btn = Button(this)
            btn.text = "Wynajmij ${cand.name} (50g)"
            btn.setOnClickListener {
                if (GameRepository.state.gold >= 50) {
                    GameRepository.state.gold -= 50
                    GameRepository.state.party.add(cand)
                    Toast.makeText(this, "Rekrutacja pomyślna!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Brak złota!", Toast.LENGTH_SHORT).show()
                }
            }
            container.addView(btn)
        }
    }
}
