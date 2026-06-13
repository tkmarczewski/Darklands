package com.grimreich.ui

import android.os.Bundle
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

        findViewById<Button>(R.id.btnDefend).setOnClickListener {
            if (GameRepository.state.combat.active) {
                CombatSystem.playerDefend()
                render()
            }
        }

        findViewById<Button>(R.id.btnUseMist).setOnClickListener {
            if (GameRepository.state.combat.active) {
                CombatSystem.playerUseSpecial("MIST")
                render()
            }
        }

        findViewById<Button>(R.id.btnUseBlood).setOnClickListener {
            if (GameRepository.state.combat.active) {
                CombatSystem.playerUseSpecial("BLOOD")
                render()
            }
        }

        findViewById<Button>(R.id.btnUseReflection).setOnClickListener {
            if (GameRepository.state.combat.active) {
                CombatSystem.playerUseSpecial("REFLECTION")
                render()
            }
        }

        findViewById<Button>(R.id.btnFlee).setOnClickListener {
            finish()
        }
    }

    private fun render() {
        val c = GameRepository.state.combat
        val party = GameRepository.state.party
        
        findViewById<TextView>(R.id.tvCombatLog).text = c.log.takeLast(5).joinToString("\n")
        findViewById<TextView>(R.id.tvCombatTitle).text = if (c.active) "⚔ WALKA: ${c.enemyName}" else "⚔ KONIEC WALKI"
        
        if (party.isNotEmpty()) {
            findViewById<TextView>(R.id.tvChar0Name).text = party[0].name
            findViewById<android.widget.ProgressBar>(R.id.pbChar0HP).progress = if (party[0].maxHp > 0) (party[0].hp * 100 / party[0].maxHp).coerceAtMost(100) else 0
        }
        
        if (!c.active) {
            findViewById<Button>(R.id.btnAttack).isEnabled = false
            findViewById<Button>(R.id.btnDefend).isEnabled = false
            findViewById<Button>(R.id.btnUseMist).isEnabled = false
            findViewById<Button>(R.id.btnUseBlood).isEnabled = false
            findViewById<Button>(R.id.btnUseReflection).isEnabled = false
            findViewById<Button>(R.id.btnFlee).text = "POWRÓT"
        }
    }
}
