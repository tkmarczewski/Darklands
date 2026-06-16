package com.grimreich.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.systems.TravelSystem
import com.grimreich.world.CityCatalogue

class MapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_world_atlas)

        // Ensure catalogue is seeded
        CityCatalogue.seedCanonical()

        setupClickListeners()

        findViewById<Button>(R.id.btnExitAtlas).setOnClickListener {
            finish()
        }
    }

    private fun setupClickListeners() {
        val mapping = mapOf(
            R.id.point_north_coast to "wybrzeze_polnocne",
            R.id.point_crown_plains to "rowniny_koronne",
            R.id.point_heartland to "serce_krainy",
            R.id.point_south_ruins to "poludniowe_ruiny",
            R.id.point_south_mountains to "gory_poludniowe",
            R.id.point_steppe to "pogranicze_stepowe",
            R.id.point_wild_lands to "ziemie_dzikie"
        )

        val discovered = com.grimreich.core.GameRepository.state.world.discoveredLocations

        mapping.forEach { (viewId, cityId) ->
            val view = findViewById<View>(viewId)
            
            // DYNAMIC VISIBILITY: Show only discovered or canonical locations
            // For now, canonical are always visible, but we can extend this
            // view?.visibility = if (discovered.contains(cityId) || isCanonical(cityId)) View.VISIBLE else View.GONE

            view?.setOnClickListener {
                val city = CityCatalogue.get(cityId)
                if (city != null) {
                    UiUtils.showChoicePopup(
                        this, 
                        city.name.uppercase(), 
                        "Domena: ${city.phenomenon}. Patron: ${city.prophet ?: "Nieznany"}.\n\nCzy wyruszasz w drogę do tego regionu?",
                        positiveText = "TAK",
                        negativeText = "NIE",
                        onPositive = {
                            TravelSystem.travelTo(cityId, this)
                            finish()
                        }
                    )
                }
            }
        }
    }
}
