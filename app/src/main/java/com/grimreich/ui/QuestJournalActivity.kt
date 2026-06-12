package com.grimreich.ui

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.systems.QuestSystem

class QuestJournalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quest_journal)

        renderQuests()

        findViewById<Button>(R.id.btnExitJournal).setOnClickListener {
            finish()
        }
    }

    private fun renderQuests() {
        val container = findViewById<LinearLayout>(R.id.questListContainer)
        container.removeAllViews()

        val allQuests = QuestSystem.all()
        if (allQuests.isEmpty()) {
            val tv = TextView(this).apply {
                text = "Twoje kroniki milczą. Nie podjęto jeszcze żadnych prób naprawy rzeczywistości."
                styleToGrim(false)
            }
            container.addView(tv)
            return
        }

        allQuests.forEach { quest ->
            val questView = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                background = androidx.core.content.ContextCompat.getDrawable(context, R.drawable.ui_panel_side)
                setPadding(24, 24, 24, 24)
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 0, 0, 24) }
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
            val statusTv = TextView(this).apply {
                text = "STATUS: ${quest.status}"
                styleToGrim(false)
                setTextColor(android.graphics.Color.parseColor("#FFD700"))
                textSize = 12f
            }

            questView.addView(titleTv)
            questView.addView(descTv)
            questView.addView(statusTv)
            container.addView(questView)
        }
    }

    private fun TextView.styleToGrim(isHeader: Boolean) {
        this.setTextColor(androidx.core.content.ContextCompat.getColor(context, 
            if (isHeader) R.color.grimAccentGold else R.color.grimTextPrimary))
        this.textSize = if (isHeader) 16f else 14f
        this.setPadding(0, 0, 0, 8)
    }
}
