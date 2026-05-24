package com.darklandsmobile.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.darklandsmobile.R
import com.darklandsmobile.systems.QuestSystem

/**
 * Ekran finalnego podsumowania questow (Sprint 15).
 */
class QuestFinalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quest_final)
        findViewById<TextView>(R.id.questFinalText).text = QuestSystem.finalQuestSummary()
    }
}
