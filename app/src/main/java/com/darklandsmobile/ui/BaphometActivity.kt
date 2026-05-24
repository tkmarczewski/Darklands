package com.darklandsmobile.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.darklandsmobile.R
import com.darklandsmobile.systems.EndingSystem

/**
 * Ekran finalu (Sprint 17 - Baphomet / koniec gry). Pokazuje status zakonczenia.
 */
class BaphometActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_baphomet)
        findViewById<TextView>(R.id.baphometText).text = EndingSystem.finaleStatus()
    }
}
