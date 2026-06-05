package com.grimreich.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.core.GameRepository
import com.grimreich.systems.TravelSystem

class MapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val tv = findViewById<TextView>(R.id.tvMap)
        val w = GameRepository.state.world

        tv.text = buildString {
            appendLine("=== MAPA SWIATA ===")
            appendLine("Aktualna lokacja: ${w.location}")
            appendLine("Region: ${w.region}")
            appendLine("Dzien: ${w.day}")
            appendLine("Pora roku: ${TravelSystem.getSeasonDisplay()}")
            appendLine("Pora dnia: ${w.timeOfDay}")
            appendLine("Zmeczenie: ${w.fatigue}")
            appendLine("Ostatnie spotkanie: ${w.lastEncounter}")
            appendLine()
            appendLine("Mapa MVP jest obecnie uproszczona.")
            appendLine("Pelny widok polaczen miast zostanie przywrocony po stabilizacji vertical slice.")
        }
    }
}