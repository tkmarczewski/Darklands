package com.grimreich.ui

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.core.GameRepository
import com.grimreich.systems.CombatSystem
import com.grimreich.systems.SkillCatalogue

class CombatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_combat)

        // Ensure combat is active
        if (GameRepository.state.combat.enemyHp <= 0) {
            CombatSystem.startCombat("Cień Przeszłości", 50, 5, 80)
        }

        render()

        findViewById<Button>(R.id.btnAttack).setOnClickListener {
            val result = CombatSystem.playerAttack()
            render()
        }
        
        val skillsContainer = findViewById<LinearLayout>(R.id.skillsContainer)
        SkillCatalogue.allSkills.forEach { skill ->
            val btn = Button(this)
            btn.text = skill.name
            btn.setOnClickListener {
                // Simplified skill usage logic
                val hero = GameRepository.state.party.firstOrNull() ?: return@setOnClickListener
                val msg = skill.effect(
                    com.grimreich.core.CombatantState(hero.name, hero.hp, hero.maxHp, 20, hero.morale, 0),
                    com.grimreich.core.CombatantState("Enemy", GameRepository.state.combat.enemyHp, 50, 20, 80, 0)
                )
                GameRepository.state.combat.log.add(msg)
                render()
            }
            skillsContainer.addView(btn)
        }
    }

    private fun render() {
        val c = GameRepository.state.combat
        val hero = GameRepository.state.party.firstOrNull() ?: return
        
        findViewById<TextView>(R.id.heroStatus).text = "${hero.name}\nHP: ${hero.hp}/${hero.maxHp}\nMorale: ${hero.morale}"
        findViewById<TextView>(R.id.enemyStatus).text = "${c.enemyName}\nHP: ${c.enemyHp}/50"
        
        findViewById<TextView>(R.id.combatLog).text = c.log.takeLast(10).joinToString("\n")
        
        if (!c.active) {
            findViewById<Button>(R.id.btnAttack).text = "WALKA ZAKOŃCZONA"
            findViewById<Button>(R.id.btnAttack).isEnabled = false
        }
        
        findViewById<Button>(R.id.btnExitCombat).setOnClickListener {
            finish()
        }
    }
}
