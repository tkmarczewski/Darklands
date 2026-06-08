package com.grimreich.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R

class QuestFinalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quest_final)
        
        findViewById<TextView>(R.id.questFinalText).text = "Finał przygody nie jest jeszcze częścią tego fragmentu rzeczywistości."

        findViewById<Button>(R.id.btnExitQuestFinal).setOnClickListener {
            finish()
        }
    }
}
