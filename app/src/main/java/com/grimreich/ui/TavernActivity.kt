package com.grimreich.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.core.GameRepository
import androidx.activity.compose.setContent
import com.grimreich.ui.tavern.TavernScreen
import com.grimreich.ui.tavern.TavernViewModel

class TavernActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val viewModel = TavernViewModel()
        
        setContent {
            TavernScreen(
                viewModel = viewModel, 
                onHire = { startActivity(Intent(this, RecruitmentActivity::class.java)) },
                onExit = { finish() }
            )
        }
    }
}
