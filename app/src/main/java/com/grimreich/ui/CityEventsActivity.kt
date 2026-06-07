package com.grimreich.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.systems.CityEventSystem
import com.grimreich.core.GameRepository

class CityEventsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_events)

        val cityId = GameRepository.state.world.location.lowercase().replace(" ", "_")
        val events = CityEventSystem.getEventsForCity(cityId)
        
        val tv = findViewById<TextView>(R.id.cityEventsStatus)
        tv.text = buildString {
            appendLine("=== WYDARZENIA: ${cityId.uppercase()} ===")
            if (events.isEmpty()) {
                appendLine("Obecnie w mieście panuje nienaturalna cisza.")
            } else {
                events.forEach { event ->
                    appendLine("- ${event.title}")
                    appendLine("  ${event.description}")
                    appendLine()
                }
            }
        }

        findViewById<Button>(R.id.btnExitCityEvents).setOnClickListener {
            finish()
        }
    }
}
