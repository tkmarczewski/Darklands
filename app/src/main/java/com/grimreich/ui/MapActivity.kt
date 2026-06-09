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
        findViewById<View>(R.id.point_city_of_crowns).setOnClickListener {
            UiUtils.showNarrativePopup(
                this, 
                "MIASTO KORONY", 
                "Serce Reichu, gdzie złoto i krew płyną tym samym strumieniem. Czy chcesz wyruszyć w drogę?",
                onDismiss = {
                    TravelSystem.travelTo("miasto_korony", this)
                }
            )
        }

        findViewById<View>(R.id.point_frost_port).setOnClickListener {
            UiUtils.showNarrativePopup(
                this, 
                "PORT MROŹNY", 
                "Miejsce, gdzie Pamięć zaciera się pod wpływem Proroka Aeliona. Podróż zajmie wiele godzin.",
                onDismiss = {
                    TravelSystem.travelTo("port_mrozny", this)
                }
            )
        }

        findViewById<View>(R.id.point_order_keep).setOnClickListener {
            UiUtils.showNarrativePopup(
                this, 
                "TWIERDZA ZAKONU", 
                "Miejsce Próby. Tu ostrze Valdrosa dyktuje wyroki.",
                onDismiss = {
                    TravelSystem.travelTo("twierdza_zakonu", this)
                }
            )
        }
    }
}
