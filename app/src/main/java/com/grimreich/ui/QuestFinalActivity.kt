package com.grimreich.ui

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.core.GameRepository
import com.grimreich.systems.EndgameQuestChain
import com.grimreich.systems.EndgameQuestStatus

class QuestFinalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quest_final)

        renderMainQuests()

        findViewById<Button>(R.id.btnExitQuestFinal).setOnClickListener {
            finish()
        }
    }

    private fun renderMainQuests() {
        val state = GameRepository.state
        val container = findViewById<LinearLayout>(R.id.mainQuestContainer)
        val tvStats = findViewById<TextView>(R.id.tvPlayerStats)

        // Display player stats
        val hero = state.party.firstOrNull()
        if (hero != null) {
            val cityRep = state.reputation.city[state.grimCurrentRegion] ?: 0
            tvStats.text = "Wiara: ${hero.piety} | Cnota: ${hero.virtue} | Reputacja: $cityRep"
        } else {
            tvStats.text = "Brak bohatera"
        }

        container.removeAllViews()

        for (quest in EndgameQuestChain.quests) {
            // Determine quest status
            val isCompleted = state.quest.completedEndgameQuests.contains(quest.id)
            val isActive = state.quest.activeEndgameQuests.contains(quest.id)
            val isAvailable = !isCompleted && !isActive && meetsRequirements(quest.id)
            val isLocked = !isCompleted && !isActive && !isAvailable

            // Create quest card
            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(24, 16, 24, 16)
                setBackgroundColor(when {
                    isCompleted -> Color.parseColor("#2a4a2a")
                    isActive -> Color.parseColor("#3a3a1a")
                    isAvailable -> Color.parseColor("#2a2a3a")
                    else -> Color.parseColor("#1a1a1a")
                })
                val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                lp.setMargins(0, 0, 0, 16)
                layoutParams = lp
            }

            // Quest title
            val tvTitle = TextView(this).apply {
                text = quest.title
                textSize = 16f
                setTextColor(when {
                    isCompleted -> Color.parseColor("#88cc88")
                    isActive -> Color.parseColor("#cccc66")
                    isAvailable -> Color.parseColor("#c8a96e")
                    else -> Color.parseColor("#666666")
                })
                typeface = Typeface.MONOSPACE
                setTypeface(typeface, Typeface.BOLD)
            }
            card.addView(tvTitle)

            // Quest description
            val tvDesc = TextView(this).apply {
                text = quest.description
                textSize = 12f
                setTextColor(Color.parseColor("#AAAAAA"))
                typeface = Typeface.MONOSPACE
                val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                lp.setMargins(0, 8, 0, 0)
                layoutParams = lp
            }
            card.addView(tvDesc)

            // Requirements display
            if (!isCompleted) {
                val reqText = buildRequirementsText(quest.id)
                val tvReq = TextView(this).apply {
                    text = reqText
                    textSize = 10f
                    setTextColor(Color.parseColor("#888888"))
                    typeface = Typeface.MONOSPACE
                    val lp = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    lp.setMargins(0, 8, 0, 0)
                    layoutParams = lp
                }
                card.addView(tvReq)
            }

            // Rewards display
            val rewardText = "Nagrody: ${quest.rewards.gold}z, Wiara +${quest.rewards.faithBonus}, Rep +${quest.rewards.reputationBonus}"
            val tvReward = TextView(this).apply {
                text = rewardText
                textSize = 10f
                setTextColor(Color.parseColor("#66aa66"))
                typeface = Typeface.MONOSPACE
                val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                lp.setMargins(0, 4, 0, 0)
                layoutParams = lp
            }
            card.addView(tvReward)

            // Status text
            val statusText = when {
                isCompleted -> "[UKOŃCZONE]"
                isActive -> "[AKTYWNE]"
                isAvailable -> "[DOSTĘPNE]"
                else -> "[ZABLOKOWANE]"
            }
            val tvStatus = TextView(this).apply {
                text = statusText
                textSize = 11f
                setTextColor(when {
                    isCompleted -> Color.parseColor("#88cc88")
                    isActive -> Color.parseColor("#cccc66")
                    isAvailable -> Color.parseColor("#c8a96e")
                    else -> Color.parseColor("#666666")
                })
                typeface = Typeface.MONOSPACE
                setTypeface(typeface, Typeface.BOLD)
                gravity = Gravity.CENTER
                val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                lp.setMargins(0, 8, 0, 0)
                layoutParams = lp
            }
            card.addView(tvStatus)

            // Action buttons
            if (isAvailable) {
                val btnStart = Button(this).apply {
                    text = "ROZPOCZNIJ"
                    textSize = 12f
                    setBackgroundColor(Color.parseColor("#3a5a3a"))
                    setTextColor(Color.parseColor("#c8a96e"))
                    typeface = Typeface.MONOSPACE
                    val lp = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    lp.setMargins(0, 12, 0, 0)
                    layoutParams = lp
                    setOnClickListener {
                        startMainQuest(quest.id)
                    }
                }
                card.addView(btnStart)
            } else if (isActive) {
                val btnComplete = Button(this).apply {
                    text = "UKOŃCZ ZADANIE"
                    textSize = 12f
                    setBackgroundColor(Color.parseColor("#5a5a2a"))
                    setTextColor(Color.parseColor("#cccc66"))
                    typeface = Typeface.MONOSPACE
                    val lp = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    lp.setMargins(0, 12, 0, 0)
                    layoutParams = lp
                    setOnClickListener {
                        completeMainQuest(quest.id)
                    }
                }
                card.addView(btnComplete)
            }

            container.addView(card)
        }
    }

    private fun meetsRequirements(questId: String): Boolean {
        val state = GameRepository.state
        val quest = EndgameQuestChain.quests.find { it.id == questId } ?: return false
        val hero = state.party.firstOrNull() ?: return false
        val cityRep = state.reputation.city[state.grimCurrentRegion] ?: 0
        val maxRep = state.reputation.city.values.maxOrNull() ?: 0

        // Check all requirements
        if (hero.piety < quest.requirements.minFaith) return false
        if (hero.virtue < quest.requirements.minVirtue) return false
        if (cityRep < quest.requirements.minCityReputation) return false
        if (maxRep < quest.requirements.minFactionReputation) return false

        // Check prerequisite quests
        for (reqId in quest.requirements.requiredQuestIds) {
            if (!state.quest.completedEndgameQuests.contains(reqId)) {
                return false
            }
        }

        return true
    }

    private fun buildRequirementsText(questId: String): String {
        val quest = EndgameQuestChain.quests.find { it.id == questId } ?: return ""
        val state = GameRepository.state
        val hero = state.party.firstOrNull()
        val cityRep = state.reputation.city[state.grimCurrentRegion] ?: 0
        val maxRep = state.reputation.city.values.maxOrNull() ?: 0

        val lines = mutableListOf<String>()
        if (quest.requirements.minFaith > 0) {
            val check = if (hero != null && hero.piety >= quest.requirements.minFaith) "✓" else "✗"
            lines.add("$check Wiara: ${hero?.piety ?: 0}/${quest.requirements.minFaith}")
        }
        if (quest.requirements.minVirtue > 0) {
            val check = if (hero != null && hero.virtue >= quest.requirements.minVirtue) "✓" else "✗"
            lines.add("$check Cnota: ${hero?.virtue ?: 0}/${quest.requirements.minVirtue}")
        }
        if (quest.requirements.minCityReputation > 0) {
            val check = if (cityRep >= quest.requirements.minCityReputation) "✓" else "✗"
            lines.add("$check Reputacja (miasto): $cityRep/${quest.requirements.minCityReputation}")
        }
        if (quest.requirements.minFactionReputation > 0) {
            val check = if (maxRep >= quest.requirements.minFactionReputation) "✓" else "✗"
            lines.add("$check Reputacja (frakcja): $maxRep/${quest.requirements.minFactionReputation}")
        }
        if (quest.requirements.requiredQuestIds.isNotEmpty()) {
            for (reqId in quest.requirements.requiredQuestIds) {
                val reqQuest = EndgameQuestChain.quests.find { it.id == reqId }
                val check = if (state.quest.completedEndgameQuests.contains(reqId)) "✓" else "✗"
                lines.add("$check Wymaga: ${reqQuest?.title ?: reqId}")
            }
        }

        return "Wymagania:\n" + lines.joinToString("\n")
    }

    private fun startMainQuest(questId: String) {
        val state = GameRepository.state
        if (!state.quest.activeEndgameQuests.contains(questId)) {
            state.quest.activeEndgameQuests.add(questId)
        }
        renderMainQuests()
    }

    private fun completeMainQuest(questId: String) {
        val state = GameRepository.state
        val quest = EndgameQuestChain.quests.find { it.id == questId }
        if (quest != null) {
            // Award rewards
            state.gold += quest.rewards.gold
            val hero = state.party.firstOrNull()
            if (hero != null) {
                hero.piety += quest.rewards.faithBonus
                hero.virtue += quest.rewards.divineFavorBonus
            }
            val currentCity = state.grimCurrentRegion
            val currentRep = state.reputation.city[currentCity] ?: 0
            state.reputation.city[currentCity] = currentRep + quest.rewards.reputationBonus

            // Update quest state
            state.quest.activeEndgameQuests.remove(questId)
            if (!state.quest.completedEndgameQuests.contains(questId)) {
                state.quest.completedEndgameQuests.add(questId)
            }

            // Add to journal log
            state.logEntries.add(0, "Ukończono quest główny: ${quest.title}")
        }
        renderMainQuests()
    }
}
