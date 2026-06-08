package com.grimreich.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.core.GameRepository
import com.grimreich.systems.CombatSystem

class CombatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_combat)

        // Safety: If entered without active combat, allow immediate exit
        if (!GameRepository.state.combat.active) {
            findViewById<TextView>(R.id.tvCombatLog).text = "Brak aktywnego starcia w tym miejscu."
        }

        render()

        findViewById<Button>(R.id.btnAttack).setOnClickListener {
            if (GameRepository.state.combat.active) {
                CombatSystem.playerAttack()
                render()
            }
        }

        findViewById<Button>(R.id.btnFlee).setOnClickListener {
            finish() // Use Flee as exit for now
        }
        
        // Ensure other buttons don't crash
        findViewById<Button>(R.id.btnDefend).setOnClickListener { /* TODO */ }
        findViewById<Button>(R.id.btnUseMist).setOnClickListener { /* TODO */ }
        findViewById<Button>(R.id.btnUseBlood).setOnClickListener { /* TODO */ }
        findViewById<Button>(R.id.btnUseReflection).setOnClickListener { /* TODO */ }
    }

    private fun render() {
        val c = GameRepository.state.combat
        val party = GameRepository.state.party
        
        // Update Log
        findViewById<TextView>(R.id.tvCombatLog).text = c.log.takeLast(5).joinToString("\n")
        
        // Update Title/Round
        findViewById<TextView>(R.id.tvCombatTitle).text = if (c.active) "⚔ WALKA: ${c.enemyName}" else "⚔ KONIEC WALKI"
        
        // Update Party Strip (Simple mapping for now)
        if (party.isNotEmpty()) {
            findViewById<TextView>(R.id.tvChar0Name).text = party[0].name
            findViewById<android.widget.ProgressBar>(R.id.pbChar0HP).progress = (party[0].hp * 100 / party[0].maxHp)
        }
        
        if (!c.active) {
            findViewById<Button>(R.id.btnAttack).isEnabled = false
            findViewById<Button>(R.id.btnFlee).text = "POWRÓT"
        }
    }
}
