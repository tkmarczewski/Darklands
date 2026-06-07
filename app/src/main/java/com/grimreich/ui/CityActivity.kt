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

class CityActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city)

        val cityId = GameRepository.state.world.location.lowercase().replace(" ", "_")
        findViewById<TextView>(R.id.cityTitle).text = cityId.uppercase()

        DialogueManager.seedBasicDialogues()
        renderNpcs(cityId)
        updateCityStatus(SocialEventSystem.cityAudience(cityId, null))

        findViewById<Button>(R.id.btnTavern).setOnClickListener {
            val result = SocialEventSystem.runTavernEvent()
            UiUtils.showNarrativePopup(this, "KARCZMA", result)
        }

        findViewById<Button>(R.id.btnChurch).setOnClickListener {
            startActivity(Intent(this, SaintsActivity::class.java))
        }

        findViewById<Button>(R.id.btnMarket).setOnClickListener {
            startActivity(Intent(this, TradeActivity::class.java))
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
            val btn = Button(this)
            btn.text = "${npc.name} (${npc.role})"
            btn.setOnClickListener {
                val intent = Intent(this, DialogueActivity::class.java).apply {
                    putExtra("npcName", npc.name)
                    putExtra("npcRole", npc.role)
                    putExtra("startNodeId", npc.startNodeId)
                }
                startActivity(intent)
            }
            container.addView(btn)
        }
    }
}
