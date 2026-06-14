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

        val cityId = GameRepository.state.grimCurrentRegion
        val events = CityEventSystem.getEventsForCity(cityId)
        val activeQuests = com.grimreich.systems.QuestSystem.all().filter { it.cityId == cityId && it.status == com.grimreich.systems.QuestStatus.AKTYWNE }
        
        val tv = findViewById<TextView>(R.id.cityEventsStatus)
        tv.text = buildString {
            appendLine("=== WYDARZENIA: ${cityId.uppercase()} ===")
            if (events.isEmpty() && activeQuests.isEmpty()) {
                appendLine("Obecnie w mieście panuje nienaturalna cisza.")
            } else {
                events.forEach { event ->
                    appendLine("- ${event.title}")
                    appendLine("  ${event.description}")
                    appendLine()
                }
                
                if (activeQuests.isNotEmpty()) {
                    appendLine("=== AKTYWNE ZADANIA ===")
                    activeQuests.forEach { quest ->
                        appendLine("- ${quest.title}")
                        appendLine("  ZADANIE: ${quest.objective}")
                        appendLine()
                    }
                }
            }
        }
        
        // Add dynamic execution buttons for active quests
        val layout = tv.parent as android.widget.LinearLayout
        activeQuests.forEach { quest ->
            val btn = Button(this).apply {
                text = "WYKONAJ: ${quest.title}"
                setOnClickListener {
                    com.grimreich.systems.QuestSystem.complete(quest.id)
                    android.widget.Toast.makeText(context, "Zadanie zakończone!", android.widget.Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            layout.addView(btn, layout.indexOfChild(findViewById(R.id.btnExitCityEvents)))
        }

        findViewById<Button>(R.id.btnExitCityEvents).setOnClickListener {
            finish()
        }
    }
}
