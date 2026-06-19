package com.grimreich.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.core.GameRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainMenuActivity : AppCompatActivity() {

    @Inject lateinit var gameRepository: GameRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        findViewById<Button>(R.id.btnNewGame).setOnClickListener {
            gameRepository.clearSessionAndReset()
            startActivity(Intent(this, PlayerIdentityActivity::class.java))
        }

        val btnContinue = findViewById<Button>(R.id.btnContinue)
        val hasSession = gameRepository.hasSession()
        
        if (hasSession) {
            btnContinue.isEnabled = true
            btnContinue.alpha = 1.0f
            btnContinue.text = "KONTYNUUJ PRZYGODĘ"
        } else {
            btnContinue.isEnabled = false
            btnContinue.alpha = 0.5f
            btnContinue.text = "KONTYNUACJA (BRAK SESJI)"
        }

        btnContinue.setOnClickListener {
            if (gameRepository.restoreIfAvailable()) {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Brak aktywnej sesji!", Toast.LENGTH_SHORT).show()
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
