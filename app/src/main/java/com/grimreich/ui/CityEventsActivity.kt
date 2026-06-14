package com.grimreich.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.systems.CityEventSystem
import com.grimreich.systems.QuestSystem
import com.grimreich.systems.QuestStatus
import com.grimreich.core.GameRepository
import com.grimreich.core.BattleEncounter

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
                    appendLine("- ${quest.title}")
                    appendLine("  ZADANIE: ${quest.objective}")
                    appendLine("  CEL: ${quest.cityId.replace("_", " ").uppercase()}")
                    appendLine("  NAGRODA: ${quest.rewardGold} złota")
                    appendLine()
                }
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

        // Dynamic action buttons
        val layout = tv.parent as? LinearLayout ?: return
        // Remove old dynamic buttons (keep tv and exit button)
        val toRemove = mutableListOf<android.view.View>()
        for (i in 0 until layout.childCount) {
            val child = layout.getChildAt(i)
            if (child is Button && child.id != R.id.btnExitCityEvents) {
                toRemove.add(child)
            }
        }
        toRemove.forEach { layout.removeView(it) }

        val exitBtn = findViewById<Button>(R.id.btnExitCityEvents)
        val exitIndex = layout.indexOfChild(exitBtn)

        // Buttons for available quests: PRZYJMIJ
        availableQuests.forEach { quest ->
            val btn = Button(this).apply {
                text = "PRZYJMIJ: ${quest.title}"
                setBackgroundColor(android.graphics.Color.parseColor("#80001A00"))
                setTextColor(android.graphics.Color.parseColor("#ADFF2F"))
                setPadding(16, 16, 16, 16)
                val p = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.setMargins(0, 0, 0, 8) }
                layoutParams = p
                setOnClickListener {
                    QuestSystem.activate(quest.id)
                    android.widget.Toast.makeText(context, "Zadanie podjęte: ${quest.title}", android.widget.Toast.LENGTH_SHORT).show()
                    render()
                }
            }
            layout.addView(btn, exitIndex)
        }

        // Buttons for active quests with combat objective -> launch CombatActivity
        // Buttons for active quests with dialogue objective -> launch CityActivity (NPC)
        activeQuests.forEach { quest ->
            val isCombat = quest.originType == com.grimreich.systems.QuestOriginType.LOKACJA_PROCEDURALNA ||
                quest.objective.contains("przetrwaj", ignoreCase = true) ||
                quest.objective.contains("pokonaj", ignoreCase = true) ||
                quest.objective.contains("walka", ignoreCase = true) ||
                quest.objective.contains("uderz", ignoreCase = true) ||
                quest.objective.contains("złóż ofiarę", ignoreCase = true)
            val isDialogue = quest.objective.contains("porozmawiaj", ignoreCase = true) ||
                quest.objective.contains("przekonaj", ignoreCase = true)

            val btn = Button(this).apply {
                text = if (isCombat) "⚔ WYKONAJ (WALKA): ${quest.title}"
                       else if (isDialogue) "🗣 WYKONAJ (NPC): ${quest.title}"
                       else "✔ WYKONAJ: ${quest.title}"
                setBackgroundColor(
                    if (isCombat) android.graphics.Color.parseColor("#80330000")
                    else android.graphics.Color.parseColor("#80000033")
                )
                setTextColor(android.graphics.Color.parseColor("#FFD700"))
                setPadding(16, 16, 16, 16)
                val p = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.setMargins(0, 0, 0, 8) }
                layoutParams = p
                setOnClickListener {
                    when {
                        isCombat -> {
                            // Store quest id for completion after combat
                            GameRepository.state.pendingQuestId = quest.id
                            startActivity(Intent(this@CityEventsActivity, CombatActivity::class.java))
                        }
                        isDialogue -> {
                            // Go to city to find the NPC
                            val npcHint = quest.objective
                                .substringAfter("porozmawiaj z ", "")
                                .substringAfter("Przekonaj ", "")
                                .substringBefore(" ", quest.originRefId)
                            UiUtils.showNarrativePopup(
                                this@CityEventsActivity,
                                "WSKAŻÓWKA",
                                "Udaj się do miasta ${quest.cityId.replace("_", " ").uppercase()} i odszukaj NPC: $npcHint.\n\nZadanie: ${quest.objective}"
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
            layout.addView(btn, exitIndex)
        }
    }
}
