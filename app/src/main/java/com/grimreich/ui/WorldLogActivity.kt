package com.grimreich.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.systems.ChronicleSystem

class WorldLogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_world_log)

        val logContent = ChronicleSystem.getAll().reversed().joinToString("\n\n") { entry ->
            "DZIEŃ ${entry.day}: ${entry.text}"
        }

        findViewById<TextView>(R.id.tvWorldLogContent).text = 
            if (logContent.isEmpty()) "Kroniki milczą... Twoja podróż dopiero się zaczyna." else logContent

        findViewById<Button>(R.id.btnBackFromLog).setOnClickListener {
            finish()
        }
    }
}
