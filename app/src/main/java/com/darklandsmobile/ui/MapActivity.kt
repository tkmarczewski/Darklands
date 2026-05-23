package com.darklandsmobile.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.darklandsmobile.R
import com.darklandsmobile.core.GameRepository
import com.darklandsmobile.core.WorldMap

class MapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        val tv = findViewById<TextView>(R.id.tvMap)
        val w = GameRepository.state.world
        val sb = StringBuilder()
        sb.appendLine("=== MAPA SWIATA ===")
        sb.appendLine()
        WorldMap.regions.forEach { region ->
            val marker = if (region == w.region) ">> " else "   "
            sb.appendLine("$marker${region.uppercase()}")
        }
        sb.appendLine()
        sb.appendLine("Aktualna lokacja: ${w.location} (${w.region})")
        tv.text = sb.toString()
    }
}
