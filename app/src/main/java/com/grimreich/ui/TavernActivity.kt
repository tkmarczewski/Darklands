package com.grimreich.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.core.GameRepository
import com.grimreich.systems.SocialEventSystem
import com.grimreich.systems.SaveLoadSystem

class TavernActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tavern)

        findViewById<Button>(R.id.btnRest).setOnClickListener { rest() }
        findViewById<Button>(R.id.btnGossip).setOnClickListener { listenToGossip() }
        findViewById<Button>(R.id.btnHire).setOnClickListener { 
            startActivity(Intent(this, RecruitmentActivity::class.java))
        }
        findViewById<Button>(R.id.btnExitTavern).setOnClickListener { finish() }
    }

    private fun rest() {
        val state = GameRepository.state
        if (state.gold < 50) {
            updateLog("Nie stać cię na nocleg. Karczmarz wskazuje na stajnię...")
            return
        }

        state.gold -= 50
        state.party.forEach { hero ->
            val healAmount = hero.maxHp / 2
            hero.hp = (hero.hp + healAmount).coerceAtMost(hero.maxHp)
            hero.endurance = 20 // Default max or calculated? Using fixed for now.
            hero.sanity = (hero.sanity + 10).coerceAtMost(100)
        }
        
        state.world.day += 1
        state.world.timeOfDay = "Morning"
        
        updateLog("Przespałeś noc w miarę czystym łóżku. Twoje rany się podgoiły, a umysł odpoczął. Jest nowy dzień.")
        SaveLoadSystem.save(this)
    }

    private fun listenToGossip() {
        val gossip = SocialEventSystem.runTavernEvent()
        updateLog(gossip)
    }

    private fun updateLog(text: String) {
        findViewById<TextView>(R.id.tvTavernLog).text = text
    }
}
