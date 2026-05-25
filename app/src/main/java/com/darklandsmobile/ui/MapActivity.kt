package com.darklandsmobile.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.darklandsmobile.R
import com.darklandsmobile.core.GameRepository
import com.darklandsmobile.core.WorldMap
import com.darklandsmobile.systems.TravelSystem

class MapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val tv = findViewById<TextView>(R.id.tvMap)
        val w = GameRepository.state.world
        val sb = StringBuilder()

        sb.appendLine("=== MAPA SWIATA ===")
        sb.appendLine("Dzien: ${w.day} | Pora roku: ${TravelSystem.getSeasonDisplay()}")
        sb.appendLine("Pora dnia: ${w.timeOfDay} | Zmeczenie: ${w.fatigue}")
        sb.appendLine()

        val nodes = WorldMap.all()
        val regions = nodes.map { node -> node.region }.distinct()

        regions.forEach { region ->
            val marker = if (region == w.region) ">> " else "   "
            sb.appendLine("$marker${region.uppercase()}")

            nodes.filter { node -> node.region == region }
                .forEach { node ->
                    val locMarker = if (node.name == w.location) "  * " else "    "
                    sb.appendLine("$locMarker${node.name}")
                }
        }

        sb.appendLine()
        sb.appendLine("Aktualna lokacja: ${w.location} (${w.region})")
        if (w.lastEncounter != "none") {
            sb.appendLine("Ostatnie spotkanie: ${w.lastEncounter}")
        }

        tv.text = sb.toString()
    }
}