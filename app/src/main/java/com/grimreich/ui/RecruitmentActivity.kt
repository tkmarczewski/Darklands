package com.grimreich.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
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

        val state = GameRepository.state
        val candidates = state.hireableHeroes

        if (candidates.isEmpty()) {
            val tv = TextView(this)
            tv.text = "Karczma jest pusta... nikt nie szuka obecnie przygód."
            tv.setTextAppearance(androidx.appcompat.R.style.TextAppearance_AppCompat_Body1)
            tv.setTextColor(ContextCompat.getColor(this, R.color.grimTextPrimary))
            tv.textAlignment = View.TEXT_ALIGNMENT_CENTER
            tv.setPadding(0, 50, 0, 0)
            container.addView(tv)
            return
        }

        candidates.forEach { cand ->
            val btn = Button(this, null, 0, R.style.GrimCombatButton)
            btn.text = "Wynajmij ${cand.name} (50G)"
            btn.setOnClickListener {
                tryHire(cand)
            }
            container.addView(btn)
        }
    }

    private fun tryHire(hero: Hero) {
        val state = GameRepository.state
        
        if (state.party.size >= 4) {
            showNotice("Drużyna jest pełna! (Max 4 osoby)")
            return
        }

        if (state.gold >= 50) {
            state.gold -= 50
            state.party.add(hero)
            state.hireableHeroes.remove(hero)
            
            showNotice("Zrekrutowano: ${hero.name}")
            render() // Refresh list
        } else {
            showNotice("Brak złota!")
        }
    }

    private fun showNotice(msg: String) {
        val view = findViewById<View>(android.R.id.content)
        val snack = Snackbar.make(view, msg, Snackbar.LENGTH_SHORT)
        snack.setBackgroundTint(ContextCompat.getColor(this, R.color.grimBgSide))
        snack.setTextColor(ContextCompat.getColor(this, R.color.grimAccentGold))
        snack.show()
    }
}
