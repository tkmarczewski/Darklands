package com.grimreich.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.systems.EconomySystem

/**
 * Ekran handlu (Sprint 11): wylicza lokalna cene danego basePrice w wybranym miescie.
 * Extras: "cityId" (default "grimhold") i "basePrice" (default 100).
 */
class TradeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trade)

        val cityId    = intent.getStringExtra("cityId") ?: "grimhold"
        val basePrice = intent.getIntExtra("basePrice", 100)
        val price     = EconomySystem.priceInCity(cityId, basePrice)
        findViewById<TextView>(R.id.tradeStatus).text =
            "Miasto: $cityId\nCena bazowa: $basePrice\nCena lokalna: $price"
    }
}
