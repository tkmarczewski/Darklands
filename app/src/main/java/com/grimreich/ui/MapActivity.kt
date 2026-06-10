package com.grimreich.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.systems.TravelSystem

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
        val mapping = mapOf(
            R.id.point_north_coast to "wybrzeze_polnocne",
            R.id.point_crown_plains to "rowniny_koronne",
            R.id.point_heartland to "serce_krainy",
            R.id.point_south_ruins to "poludniowe_ruiny",
            R.id.point_south_mountains to "gory_poludniowe",
            R.id.point_steppe to "pogranicze_stepowe",
            R.id.point_wild_lands to "ziemie_dzikie"
        )

        mapping.forEach { (viewId, cityId) ->
            findViewById<View>(viewId).setOnClickListener {
                val city = com.grimreich.world.CityCatalogue.get(cityId)
                if (city != null) {
                    UiUtils.showNarrativePopup(
                        this, 
                        city.name.uppercase(), 
                        "Domena: ${city.phenomenon}. Patron: ${city.prophet ?: "Nieznany"}. Czy wyruszasz w drogę?",
                        onDismiss = {
                            TravelSystem.travelTo(cityId, this)
                            finish()
                        }
                    )
                }
            }
        }
    }
}
