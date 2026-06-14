package com.grimreich.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.systems.CityEventSystem
import com.grimreich.systems.QuestSystem
import com.grimreich.systems.QuestStatus
import com.grimreich.core.GameRepository

class CityEventsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_events)
        render()
        findViewById<Button>(R.id.btnExitCityEvents).setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        render()
    }

    private fun render() {
        val cityId = GameRepository.state.grimCurrentRegion ?: ""
        val events = CityEventSystem.getEventsForCity(cityId)
        val allQuests = QuestSystem.all()
        val availableQuests = allQuests.filter { it.cityId == cityId && it.status == QuestStatus.DOSTEPNE }
        val activeQuests = allQuests.filter { it.cityId == cityId && it.status == QuestStatus.AKTYWNE }

        // Set the scrollable text info
        val tv = findViewById<TextView>(R.id.cityEventsStatus)
        tv.text = buildString {
            appendLine("=== WYDARZENIA: ${cityId.replace("_", " ").uppercase()} ===")
            if (events.isEmpty() && availableQuests.isEmpty() && activeQuests.isEmpty()) {
                appendLine("Obecnie w mieście panuje nienaturalna cisza.")
            } else {
                events.forEach { event ->
                    appendLine("- ${event.title}")
                    appendLine("  ${event.description}")
                    appendLine()
                }
            }
            if (availableQuests.isNotEmpty()) {
                appendLine("=== DOSTĘPNE ZADANIA ===")
                availableQuests.forEach { quest ->
                    appendLine("▶ ${quest.title}")
                    appendLine("  ZADANIE: ${quest.objective}")
                    appendLine("  MIEJSCE: ${quest.cityId.replace("_", " ").uppercase()}")
                    appendLine("  NAGRODA: ${quest.rewardGold} złota")
                    appendLine()
                }
            }
            if (activeQuests.isNotEmpty()) {
                appendLine("=== AKTYWNE ZADANIA ===")
                activeQuests.forEach { quest ->
                    appendLine("★ ${quest.title}")
                    appendLine("  ZADANIE: ${quest.objective}")
                    appendLine()
                }
            }
        }

        // Use the dedicated scroll container for dynamic buttons
        val container = findViewById<LinearLayout>(R.id.cityEventsScrollContainer)
        // Remove all dynamic buttons (keep tv = first child)
        while (container.childCount > 1) {
            container.removeViewAt(container.childCount - 1)
        }

        // Buttons for available quests
        availableQuests.forEach { quest ->
            val btn = Button(this).apply {
                text = "✔ PRZYJMIJ ZADANIE: ${quest.title}"
                setBackgroundColor(android.graphics.Color.parseColor("#80001A00"))
                setTextColor(android.graphics.Color.parseColor("#ADFF2F"))
                setPadding(16, 16, 16, 16)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.setMargins(0, 0, 0, 8) }
                setOnClickListener {
                    QuestSystem.activate(quest.id)
                    android.widget.Toast.makeText(context, "Zadanie podjęte!", android.widget.Toast.LENGTH_SHORT).show()
                    render()
                }
            }
            container.addView(btn)
        }

        // Buttons for active quests
        activeQuests.forEach { quest ->
            val isCombat = quest.originType == com.grimreich.systems.QuestOriginType.LOKACJA_PROCEDURALNA ||
                quest.objective.contains("przetrwaj", ignoreCase = true) ||
                quest.objective.contains("pokonaj", ignoreCase = true) ||
                quest.objective.contains("uderz", ignoreCase = true) ||
                quest.objective.contains("złóż ofiarę", ignoreCase = true)
            val isDialogue = quest.objective.contains("porozmawiaj", ignoreCase = true) ||
                quest.objective.contains("przekonaj", ignoreCase = true)

            val btn = Button(this).apply {
                text = when {
                    isCombat -> "⚔ WYKONAJ (WALKA): ${quest.title}"
                    isDialogue -> "🗣 WYKONAJ (NPC): ${quest.title}"
                    else -> "➤ WYKONAJ: ${quest.title}"
                }
                setBackgroundColor(
                    if (isCombat) android.graphics.Color.parseColor("#80330000")
                    else android.graphics.Color.parseColor("#80000033")
                )
                setTextColor(android.graphics.Color.parseColor("#FFD700"))
                setPadding(16, 16, 16, 16)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.setMargins(0, 0, 0, 8) }
                setOnClickListener {
                    when {
                        isCombat -> {
                            GameRepository.state.pendingQuestId = quest.id
                            startActivity(Intent(this@CityEventsActivity, CombatActivity::class.java))
                        }
                        isDialogue -> {
                            val npcHint = quest.objective
                                .substringAfter("porozmawiaj z ", "")
                                .substringAfter("Przekonaj ", "")
                                .substringBefore(" ", quest.originRefId)
                            UiUtils.showNarrativePopup(
                                this@CityEventsActivity,
                                "WSKAŻÓWKA",
                                "Udaj się do: ${quest.cityId.replace("_"," ").uppercase()}\nOdszukaj NPC: $npcHint\n\nZadanie: ${quest.objective}"
                            )
                        }
                        else -> {
                            QuestSystem.complete(quest.id)
                            android.widget.Toast.makeText(context, "Zadanie ukończone! +${quest.rewardGold} złota", android.widget.Toast.LENGTH_LONG).show()
                            render()
                        }
                    }
                }
            }
            container.addView(btn)
        }
    }
}
