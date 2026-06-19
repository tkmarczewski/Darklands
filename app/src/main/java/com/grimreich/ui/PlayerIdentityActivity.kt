package com.grimreich.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.core.GameRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PlayerIdentityActivity : AppCompatActivity() {

    @Inject lateinit var gameRepository: GameRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_identity)

        findViewById<TextView>(R.id.tvIdentityHint).text =
            "Podaj swoje imię. To imię należy do Ciebie, nie do bohatera."

        val etPlayerName = findViewById<EditText>(R.id.etPlayerName)

        findViewById<Button>(R.id.btnContinueToCreator).setOnClickListener {
            val playerName = etPlayerName.text.toString().trim()

            if (playerName.isBlank()) {
                Toast.makeText(this, "Podaj swoje imię.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val state = gameRepository.currentState()
            state.playerName = playerName
            state.characterNameLocked = true
            gameRepository.persistCurrentState()

            startActivity(Intent(this, CharacterCreatorActivity::class.java))
        }
    }
}
