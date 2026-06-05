package com.grimreich.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.systems.CombatSystem

/**
 * Ekran statusu walki (Sprint 14): pokazuje skrocone podsumowanie biezacej / ostatniej walki.
 */
class CombatStatusActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_combat_status)
        findViewById<TextView>(R.id.combatStatusText).text = CombatSystem.combatSummary()
    }
}
