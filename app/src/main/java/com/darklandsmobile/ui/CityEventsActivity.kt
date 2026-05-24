package com.darklandsmobile.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.darklandsmobile.R
import com.darklandsmobile.systems.CityEventSystem

/**
 * Ekran eventow miasta (Sprint 10): odpala pojedynczy event przez CityEventSystem
 * dla miasta przekazanego w extra "cityId" (default "magdeburg").
 */
class CityEventsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_events)

        val cityId = intent.getStringExtra("cityId") ?: "magdeburg"
        findViewById<TextView>(R.id.cityEventsStatus).text =
            CityEventSystem.runCityEvent(cityId)
    }
}
