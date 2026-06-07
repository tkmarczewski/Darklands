package com.grimreich.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.systems.EndingSystem

class FinaleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finale)
        findViewById<TextView>(R.id.finaleText).text = EndingSystem.finaleStatus()
        
        findViewById<android.widget.Button>(R.id.btnExitFinale).setOnClickListener {
            finish()
        }
    }
}
