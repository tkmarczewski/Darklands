package com.grimreich.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.core.GameRepository
import androidx.activity.compose.setContent
import com.grimreich.ui.saints.SaintsScreen
import com.grimreich.ui.saints.SaintsViewModel

class SaintsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val viewModel = SaintsViewModel()
        
        setContent {
            SaintsScreen(viewModel = viewModel, onExit = { finish() })
        }
    }
}
