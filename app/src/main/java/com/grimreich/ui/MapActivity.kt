package com.grimreich.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.core.GameRepository
import com.grimreich.systems.TravelSystem

class MapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        renderMap()

        findViewById<Button>(R.id.btnTravelForest).setOnClickListener {
            TravelSystem.travelTo("forest", this)
            renderMap()
        }

        findViewById<Button>(R.id.btnTravelMountains).setOnClickListener {
            TravelSystem.travelTo("mountains", this)
            renderMap()
        }
    }

    private fun renderMap() {
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
        }
    }
}
