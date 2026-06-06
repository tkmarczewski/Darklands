package com.grimreich.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.core.GameRepository
import com.grimreich.systems.EndingSystem

class EndgameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finale)

        val tv = findViewById<TextView>(R.id.finaleText)
        val s = GameRepository.state
        val ending = EndingSystem.resolveEnding(s)

        tv.text = buildString {
            appendLine("=== KONIEC HISTORII ===")
            appendLine(ending.title)
            appendLine()
            appendLine(ending.description)
            appendLine()
            appendLine("LOSY BOHATERÓW:")
            s.party.forEach { hero ->
                appendLine("- ${EndingSystem.getHeroEpilogue(hero)}")
            }
            appendLine()
            appendLine("Dziękujemy za grę w GrimReich 1.5.")
        }
    }
}
