package com.grimreich.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.systems.SaveLoadSystem

class MainMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        findViewById<Button>(R.id.btnNewGame).setOnClickListener {
            SaveLoadSystem.clear(this) // Reset for new game
            startActivity(Intent(this, CharacterCreatorActivity::class.java))
        }

        val btnContinue = findViewById<Button>(R.id.btnContinue)
        if (SaveLoadSystem.hasSave(this)) {
            btnContinue.visibility = View.VISIBLE
            btnContinue.text = "KONTYNUUJ PRZYGODĘ"
        } else {
            btnContinue.visibility = View.GONE
        }

        btnContinue.setOnClickListener {
            if (SaveLoadSystem.load(this)) {
                startActivity(Intent(this, HubActivity::class.java))
            } else {
                Toast.makeText(this, "Brak zapisu gry!", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.btnExit).setOnClickListener {
            finishAffinity()
        }

        findViewById<TextView>(R.id.tvDevMenuTrigger)?.setOnClickListener {
            startActivity(Intent(this, DevMenuActivity::class.java))
        }
    }
}
