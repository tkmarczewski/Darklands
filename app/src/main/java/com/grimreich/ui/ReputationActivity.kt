package com.grimreich.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.systems.ReputationSystem

/**
 * Ekran reputacji (Sprint 9): tabelarycznie pokazuje reputacje per miasto.
 * Frakcje sa renderowane warunkowo - obecny model ReputationState moze ich nie wystawiac.
 */
class ReputationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reputation)

        val cityRep = ReputationSystem.allCities().entries
            .joinToString("\n") { (city, value) -> "$city: $value" }
            .ifBlank { "  brak danych" }
        findViewById<TextView>(R.id.reputationStatus).text =
            "Miasta:\n$cityRep"
    }
}
