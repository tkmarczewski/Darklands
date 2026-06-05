package com.grimreich.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.systems.SocialEventSystem

/**
 * Ekran miasta (Sprint 8): pokazuje reakcje miasta i ewentualnego swietego patrona.
 * Oczekuje extra "cityId" (default "grimhold") oraz opcjonalnie "saintId".
 */
class CityActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city)

        val cityId  = intent.getStringExtra("cityId") ?: "grimhold"
        val saintId = intent.getStringExtra("saintId")
        findViewById<TextView>(R.id.cityStatus).text =
            SocialEventSystem.cityAudience(cityId, saintId)
    }
}
