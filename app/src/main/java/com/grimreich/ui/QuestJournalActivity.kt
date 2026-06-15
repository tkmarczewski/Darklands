package com.grimreich.ui

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.systems.QuestSystem

class QuestJournalActivity : AppCompatActivity() {
    private var currentFilter: com.grimreich.systems.QuestStatus = com.grimreich.systems.QuestStatus.DOSTEPNE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quest_journal)

        setupTabs()
        renderQuests()

        findViewById<Button>(R.id.btnExitJournal).setOnClickListener {
            finish()
        }
    }

    private fun setupTabs() {
        findViewById<Button>(R.id.tabAvailable).setOnClickListener {
            currentFilter = com.grimreich.systems.QuestStatus.DOSTEPNE
            renderQuests()
        }
        findViewById<Button>(R.id.tabActive).setOnClickListener {
            currentFilter = com.grimreich.systems.QuestStatus.AKTYWNE
            renderQuests()
        }
        findViewById<Button>(R.id.tabCompleted).setOnClickListener {
            currentFilter = com.grimreich.systems.QuestStatus.UKONCZONE
            renderQuests()
        }
    }

    private fun renderQuests() {
        val container = findViewById<LinearLayout>(R.id.questListContainer)
        container.removeAllViews()

        // Visual feedback for active tab
        updateTabStyles()

        val allQuests = QuestSystem.all().filter { it.status == currentFilter }
        if (allQuests.isEmpty()) {
            val tv = TextView(this).apply {
                text = when(currentFilter) {
                    com.grimreich.systems.QuestStatus.DOSTEPNE -> "Brak nowych zadań w okolicy."
                    com.grimreich.systems.QuestStatus.AKTYWNE -> "Nie podjęto obecnie żadnych zadań."
                    com.grimreich.systems.QuestStatus.UKONCZONE -> "Twoje kroniki milczą o ukończonych czynach."
                    else -> "Brak danych."
                }
                styleToGrim(false)
            }
            container.addView(tv)
            return
        }

        allQuests.forEach { quest ->
            val questView = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                background = android.graphics.drawable.ColorDrawable(android.graphics.Color.parseColor("#40FFFFFF"))
                setPadding(32, 32, 32, 32)
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 0, 0, 32) }
                layoutParams = params
            }

            val titleTv = TextView(this).apply {
                text = quest.title.uppercase()
                styleToGrim(true)
            }
            val descTv = TextView(this).apply {
                text = quest.description
                styleToGrim(false)
            }
            
            val objTv = TextView(this).apply {
                text = "ZADANIE: ${quest.objective}"
                styleToGrim(false)
                setTextColor(android.graphics.Color.parseColor("#ADFF2F")) // GreenYellow
                textSize = 12f
            }
            
            val goalTv = TextView(this).apply {
                text = "CEL: ${quest.cityId.replace("_", " ").uppercase()}"
                styleToGrim(false)
                setTextColor(android.graphics.Color.parseColor("#00AAFF"))
                textSize = 12f
            }

            val statusTv = TextView(this).apply {
                text = "STATUS: ${quest.status}"
                styleToGrim(false)
                setTextColor(android.graphics.Color.parseColor("#FFD700"))
                textSize = 12f
            }
            
            val rewardTv = TextView(this).apply {
                text = "NAGRODA: ${quest.rewardGold} złota"
                styleToGrim(false)
                setTextColor(android.graphics.Color.parseColor("#FFD700"))
                textSize = 12f
            }

            questView.addView(titleTv)
            questView.addView(descTv)
            questView.addView(objTv)
            questView.addView(goalTv)
            questView.addView(statusTv)
            questView.addView(rewardTv)

            if (quest.status == com.grimreich.systems.QuestStatus.DOSTEPNE) {
                val acceptBtn = Button(androidx.appcompat.view.ContextThemeWrapper(this, R.style.GrimSmallButton), null, 0).apply {
                    text = "PRZYJMIJ ZADANIE"
                    setOnClickListener {
                        com.grimreich.systems.QuestSystem.activate(quest.id)
                        android.widget.Toast.makeText(context, "Zadanie podjęte!", android.widget.Toast.LENGTH_SHORT).show()
                        renderQuests()
                    }
                }
                questView.addView(acceptBtn)
            }

            container.addView(questView)
        }
    }

    private fun updateTabStyles() {
        val gold = android.graphics.Color.parseColor("#FFD700")
        val grey = android.graphics.Color.parseColor("#888888")

        findViewById<Button>(R.id.tabAvailable).setTextColor(if (currentFilter == com.grimreich.systems.QuestStatus.DOSTEPNE) gold else grey)
        findViewById<Button>(R.id.tabActive).setTextColor(if (currentFilter == com.grimreich.systems.QuestStatus.AKTYWNE) gold else grey)
        findViewById<Button>(R.id.tabCompleted).setTextColor(if (currentFilter == com.grimreich.systems.QuestStatus.UKONCZONE) gold else grey)
    }

    private fun TextView.styleToGrim(isHeader: Boolean) {
        this.setTextColor(androidx.core.content.ContextCompat.getColor(context, 
            if (isHeader) R.color.grimAccentGold else R.color.grimTextPrimary))
        this.textSize = if (isHeader) 16f else 14f
        this.setPadding(0, 0, 0, 8)
    }
}
