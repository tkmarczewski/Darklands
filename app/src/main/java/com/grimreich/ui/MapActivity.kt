package com.grimreich.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import androidx.activity.compose.setContent
import com.grimreich.ui.map.WorldMapScreen
import com.grimreich.ui.map.WorldMapViewModel
import com.grimreich.world.CityCatalogue

class MapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ensure catalogue is seeded
        CityCatalogue.seedCanonical()

        val viewModel = WorldMapViewModel()

        setContent {
            WorldMapScreen(viewModel = viewModel, onBack = { finish() })
        }
    }
}
