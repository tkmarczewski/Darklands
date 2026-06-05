package com.grimreich.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.core.SaintCatalogue

/**
 * Ekran swietych (Sprint 10): czytelna lista patronow z SaintCatalogue.
 */
class SaintsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saints)

        val saintsText = SaintCatalogue.all().joinToString("\n\n") { saint ->
            "${saint.name}\n  domain: ${saint.domain}\n  patronage: ${saint.patronage}"
        }
        findViewById<TextView>(R.id.saintsStatus).text =
            saintsText.ifBlank { "Brak swietych" }
    }
}
