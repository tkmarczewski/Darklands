package com.grimreich.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R

class MapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_world_atlas)

        setupClickListeners()

        findViewById<Button>(R.id.btnExitAtlas).setOnClickListener {
            finish()
        }
    }

    private fun setupClickListeners() {
        findViewById<View>(R.id.point_city_of_crowns).setOnClickListener {
            UiUtils.showNarrativePopup(
                this, 
                "MIASTO KORONY", 
                "Serce Reichu, gdzie złoto i krew płyną tym samym strumieniem. Tu Solarian dyktuje Prawa, a Valdros wykonuje wyroki."
            )
        }

        findViewById<View>(R.id.point_northern_coast).setOnClickListener {
            UiUtils.showNarrativePopup(
                this, 
                "WYBRZEŻE PÓŁNOCNE", 
                "Kraina skąpana w wiecznej Mgle. Miejsce, gdzie Pamięć zaciera się pod wpływem Proroka Aeliona."
            )
        }
    }
}
