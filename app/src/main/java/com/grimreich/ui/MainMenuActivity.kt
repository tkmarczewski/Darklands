package com.grimreich.ui

import android.content.Intent
import android.os.Bundle
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
            SaveLoadSystem.clear(this) 
            startActivity(Intent(this, CharacterCreatorActivity::class.java))
        }

        val btnContinue = findViewById<Button>(R.id.btnContinue)
        val hasSave = SaveLoadSystem.hasSave(this)
        
        if (hasSave) {
            btnContinue.isEnabled = true
            btnContinue.alpha = 1.0f
            btnContinue.text = "KONTYNUUJ PRZYGODĘ"
        } else {
            btnContinue.isEnabled = false
            btnContinue.alpha = 0.5f
            btnContinue.text = "KONTYNUACJA (BRAK ZAPISU)"
        }

        btnContinue.setOnClickListener {
            if (SaveLoadSystem.load(this)) {
                // REDIRECT TO SINGLE ACTIVITY
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
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
