package com.grimreich.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.core.GameRepository
import com.grimreich.systems.SocialEventSystem
import com.grimreich.systems.DialogueManager
import com.grimreich.world.ProceduralNpcGenerator
import com.grimreich.world.CityCatalogue

class CityActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city)

        // Ensure canonical data is available
        CityCatalogue.seedCanonical()

        val rawLocation = GameRepository.state.world.location
        val cityId = rawLocation.lowercase().replace(" ", "_")
        
        // Match canonical name from ID if possible
        val cityData = CityCatalogue.get(cityId)
        findViewById<TextView>(R.id.cityTitle).text = (cityData?.name ?: rawLocation).uppercase()

        DialogueManager.seedBasicDialogues()
        renderNpcs(cityId)
        updateCityStatus(SocialEventSystem.cityAudience(cityId, null))

        findViewById<Button>(R.id.btnTavern).setOnClickListener {
            try {
                val result = SocialEventSystem.runTavernEvent()
                UiUtils.showNarrativePopup(this, "KARCZMA", result)
            } catch (e: Exception) {
                UiUtils.showNarrativePopup(this, "KARCZMA", "Karczmarz milczy, wpatrzony w pęknięcie na ścianie.")
            }
        }

        findViewById<Button>(R.id.btnChurch).setOnClickListener {
            startActivity(Intent(this, SaintsActivity::class.java))
        }

        findViewById<Button>(R.id.btnMarket).setOnClickListener {
            startActivity(Intent(this, TradeActivity::class.java))
        }

        findViewById<Button>(R.id.btnRecruit).setOnClickListener {
            startActivity(Intent(this, RecruitmentActivity::class.java))
        }

        findViewById<Button>(R.id.btnExitCity).setOnClickListener {
            finish()
        }
    }

    private fun updateCityStatus(text: String) {
        findViewById<TextView>(R.id.cityStatus).text = text
    }

    private fun renderNpcs(cityId: String) {
        val container = findViewById<LinearLayout>(R.id.npcListContainer)
        container.removeAllViews()

        val npcs = ProceduralNpcGenerator.generateForCity(cityId, 123)
        npcs.forEach { npc ->
            val btn = Button(this).apply {
                text = "${npc.name} (${npc.role})"
                styleToGrim()
                setOnClickListener {
                    val intent = Intent(this@CityActivity, DialogueActivity::class.java).apply {
                        putExtra("npcName", npc.name)
                        putExtra("npcRole", npc.role)
                        putExtra("startNodeId", npc.startNodeId)
                    }
                    startActivity(intent)
                }
            }
            container.addView(btn)
        }
    }

    private fun Button.styleToGrim() {
        // Basic styling for dynamically added buttons
        this.setTextColor(androidx.core.content.ContextCompat.getColor(context, R.color.grimGold))
        this.setBackgroundColor(androidx.core.content.ContextCompat.getColor(context, R.color.grimBgUltraDark))
        this.setPadding(16, 16, 16, 16)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 0, 8)
        this.layoutParams = params
    }
}
