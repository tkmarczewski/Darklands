package com.darklandsmobile.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.darklandsmobile.R
import com.darklandsmobile.core.GameRepository
import com.darklandsmobile.core.MoraleSystem
import com.darklandsmobile.core.PartyRepository
import com.darklandsmobile.systems.CombatSystem

class CombatActivity : AppCompatActivity() {

    private lateinit var tvCombatTitle: TextView
    private lateinit var tvEnemyStatus: TextView
    private lateinit var tvHeroStatus: TextView
    private lateinit var tvCombatLog: TextView
    private lateinit var btnStartCombat: Button
    private lateinit var btnAttack: Button
    private lateinit var btnFlee: Button
    private lateinit var btnBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_combat)

        tvCombatTitle  = findViewById(R.id.tvCombatTitle)
        tvEnemyStatus  = findViewById(R.id.tvEnemyStatus)
        tvHeroStatus   = findViewById(R.id.tvHeroStatus)
        tvCombatLog    = findViewById(R.id.tvCombatLog)
        btnStartCombat = findViewById(R.id.btnStartCombat)
        btnAttack      = findViewById(R.id.btnAttack)
        btnFlee        = findViewById(R.id.btnFlee)
        btnBack        = findViewById(R.id.btnBack)

        renderStatus()

        btnStartCombat.setOnClickListener {
            CombatSystem.startCombat(
                enemyName   = "Rozbojnik",
                enemyHp     = 30,
                enemyAttack = 4,
                enemyDefense = 2
            )
            btnAttack.isEnabled = true
            btnFlee.isEnabled   = true
            btnStartCombat.isEnabled = false
            renderStatus()
        }

        btnAttack.setOnClickListener {
            val result = CombatSystem.playerAttack()
            renderStatus()
            if (!CombatSystem.isCombatActive()) {
                btnAttack.isEnabled = false
                btnFlee.isEnabled   = false
                btnStartCombat.isEnabled = true
            }
        }

        btnFlee.setOnClickListener {
            val c = GameRepository.state.combat
            c.active = false
            c.log.add("Ucieczka! Druzyna porzucila pole bitwy.")
            GameRepository.log("Ucieczka z walki.")
            btnAttack.isEnabled = false
            btnFlee.isEnabled   = false
            btnStartCombat.isEnabled = true
            renderStatus()
        }

        btnBack.setOnClickListener { finish() }
    }

    private fun renderStatus() {
        val c    = GameRepository.state.combat
        val hero = PartyRepository.activeHero()

        if (hero != null) {
            val moraleStatus = MoraleSystem.computeStatus(70)
            tvHeroStatus.text = buildString {
                append("Bohater: ${hero.name}")
                append(" | HP: ${maxOf(0, hero.hp)}/${hero.maxHp}")
                append(" | Sila: ${hero.strength}")
                append(" | Morale: $moraleStatus")
            }
        } else {
            tvHeroStatus.text = "Bohater: brak"
        }

        if (c.active) {
            tvCombatTitle.text = "=== WALKA: Runda ${c.round} ==="
            tvEnemyStatus.text = buildString {
                append("Przeciwnik: ${c.enemyName}")
                append(" | HP: ${maxOf(0, c.enemyHp)}/${c.enemyMaxHp}")
                append(" | ATK: ${c.enemyAttack} DEF: ${c.enemyDefense}")
            }
        } else if (c.enemyName.isNotEmpty() && !c.active) {
            tvCombatTitle.text = if (c.enemyHp <= 0)
                "=== ZWYCIESTWO! ==="
            else
                "=== WALKA ZAKONCZONA ==="
            tvEnemyStatus.text = "${c.enemyName} | HP: ${maxOf(0, c.enemyHp)}/${c.enemyMaxHp}"
        } else {
            tvCombatTitle.text = "=== WALKA ==="
            tvEnemyStatus.text = "Wybierz przeciwnika i rozpocznij walke."
        }

        val logLines = CombatSystem.getCombatLog().takeLast(8)
        tvCombatLog.text = logLines.joinToString("\n")
    }
}
