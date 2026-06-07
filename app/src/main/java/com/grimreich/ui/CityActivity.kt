package com.grimreich.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.core.GameRepository
import com.grimreich.systems.SocialEventSystem

class CityActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city)

        val cityId = GameRepository.state.world.location.lowercase()
        findViewById<TextView>(R.id.cityStatus).text = SocialEventSystem.cityAudience(cityId, null)

        findViewById<Button>(R.id.btnTavern).setOnClickListener {
            val result = SocialEventSystem.runTavernEvent()
            Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnChurch).setOnClickListener {
            startActivity(Intent(this, SaintsActivity::class.java))
        }

        findViewById<Button>(R.id.btnMarket).setOnClickListener {
            startActivity(Intent(this, TradeActivity::class.java))
        }
    }
}
