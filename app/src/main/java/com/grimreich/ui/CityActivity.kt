package com.grimreich.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.core.GameRepository
import com.grimreich.systems.SocialEventSystem
import com.grimreich.systems.DialogueManager
import com.grimreich.world.ProceduralNpcGenerator
import com.grimreich.world.CityCatalogue
import com.grimreich.ui.CoastlineActivity

class CityActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city)

        // Ensure canonical data is available
        CityCatalogue.seedCanonical()

        val rawLocation = GameRepository.state.world.location
        val cityId = GameRepository.state.grimCurrentRegion ?: rawLocation.lowercase().replace(" ", "_")
        
        val cityData = CityCatalogue.get(cityId)
        
        // 1. SET CANONICAL BACKGROUND
        val bgResId = resources.getIdentifier(
            cityData?.backgroundDrawable ?: "bg_region_north_coast", 
            "drawable", 
            packageName
        )
        if (bgResId != 0) {
            findViewById<ImageView>(R.id.ivCityBg).setImageResource(bgResId)
        }

        findViewById<TextView>(R.id.cityTitle).text = (cityData?.name ?: rawLocation.replace("_", " ")).uppercase()

        DialogueManager.seedBasicDialogues()
        renderNpcs(cityId)
        renderQuestButtons()
        updateCityStatus(SocialEventSystem.cityAudience(cityId, null))

        findViewById<Button>(R.id.btnTavern).setOnClickListener {
            try {
                val result = SocialEventSystem.runTavernEvent()
                UiUtils.showNarrativePopup(this, "KARCZMA", result)
                renderQuestButtons() // Update buttons after tavern event
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

        // Zdarzenia losowe w mieście
        com.grimreich.systems.RandomEventManager.triggerCityEvent(this)
    }

    private fun updateCityStatus(text: String) {
        findViewById<TextView>(R.id.cityStatus).text = text
    }

    private fun renderNpcs(cityId: String) {
        val container = findViewById<LinearLayout>(R.id.npcListContainer)
        container.removeAllViews()

        val cityData = CityCatalogue.get(cityId)
        
        // Add PROPHET if exists in canonical data
        cityData?.prophet?.let { prophetName ->
            val prophetBtn = Button(this).apply {
                text = "$prophetName (PROROK)"
                styleToGrim()
                setTextColor(android.graphics.Color.parseColor("#FFD700")) // GOLD
                setOnClickListener {
                    val intent = Intent(this@CityActivity, DialogueActivity::class.java).apply {
                        putExtra("npcName", prophetName)
                        putExtra("npcRole", prophetName)
                        putExtra("startNodeId", "${prophetName.lowercase()}_start")
                    }
                    startActivity(intent)
                }
            }
            container.addView(prophetBtn)
        }

        val npcs = ProceduralNpcGenerator.generateForCity(cityId, com.grimreich.core.GrimConstants.World.NPC_GENERATION_SEED_OFFSET)
        npcs.forEach { npc ->
            val btn = Button(this).apply {
                text = "${npc.name} (${npc.role})"
                styleToGrim()
                
                // Set NPC portrait from role if available
                val portraitId = DialogueManager.getPortrait(npc.role)
                // In a real list we'd use icons, for now we keep simple buttons
                
                setOnClickListener {
                    val intent = Intent(this@CityActivity, DialogueActivity::class.java).apply {
                        putExtra("npcName", npc.name)
                        putExtra("npcRole", npc.role)
                        putExtra("startNodeId", npc.startNodeId)
                        putExtra("portrait", portraitId)
                    }
                    startActivity(intent)
                }
            }
            container.addView(btn)
        }

        // 2. MATERIALIZE ECHOES OF PAST HEROES
        val stability = GameRepository.state.world.globalStability
        if (stability < com.grimreich.core.GrimConstants.World.ECHO_MANIFESTATION_THRESHOLD) {
            val maxChance = com.grimreich.core.GrimConstants.World.ECHO_MAX_CHANCE
            val echoChance = (100 - stability) * (maxChance / 100f)
            if (kotlin.random.Random.nextFloat() < echoChance) {
                com.grimreich.core.EchoSystem.getRandomEcho()?.let { echo ->
                    val echoBtn = Button(this).apply {
                        text = "ECHO: ${echo.name} (${echo.currentCareer?.name ?: "Brak"})"
                        styleToGrim()
                        setTextColor(android.graphics.Color.parseColor("#40FFFFFF")) // Ghostly white
                        setOnClickListener {
                            val intent = Intent(this@CityActivity, DialogueActivity::class.java).apply {
                                putExtra("npcName", echo.name)
                                putExtra("npcRole", "ECHO")
                                putExtra("startNodeId", "echo_start")
                                putExtra("portrait", echo.portraitRes)
                            }
                            startActivity(intent)
                        }
                    }
                    container.addView(echoBtn, 0) // Always at top
                }
            }
        }
    }

    private fun Button.styleToGrim() {
        this.setTextColor(androidx.core.content.ContextCompat.getColor(context, R.color.grimGold))
        this.setBackgroundColor(android.graphics.Color.parseColor("#80000000"))
        this.setPadding(16, 16, 16, 16)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 0, 8)
        this.layoutParams = params
    }

    private fun renderQuestButtons() {
        val state = GameRepository.state
        val container = findViewById<LinearLayout>(R.id.npcListContainer)

        // Check if coastline quests are active
        val hasCoastlineQuest = state.quest.activeQuests.contains("quest_north_mist_vision") ||
                                 state.quest.activeQuests.contains("quest_north_lost_echo")

        if (hasCoastlineQuest) {
            val coastBtn = Button(this).apply {
                text = "⚠ IDŹ NA WYBRZEŻE [QUEST]"
                styleToGrim()
                setOnClickListener {
                    val intent = Intent(this@CityActivity, CoastlineActivity::class.java)
                    startActivity(intent)
                }
            }
            container.addView(coastBtn)
        }

        // Check for additional location-based quests with progressive unlock
        // Plains quest - unlocks after completing initial Coastline quest
        val hasPlainsQuest = state.quest.activeQuests.contains("quest_heartland_grain_mystery") ||
                             state.quest.completedQuests.contains("quest_north_mist_vision")
        if (hasPlainsQuest) {
            val plainsBtn = Button(this).apply {
                text = "⚠ IDŹ NA RÓWNINY [QUEST]"
                styleToGrim()
                setOnClickListener {
                    val intent = Intent(this@CityActivity, QuestLocationActivity::class.java)
                    intent.putExtra("questId", "quest_heartland_grain_mystery")
                    startActivity(intent)
                }
            }
            container.addView(plainsBtn)
        }
        
        // Forest quest - unlocks after Plains quest progress
        val hasForestQuest = state.quest.activeQuests.contains("quest_forest_ancient_grove") ||
                            state.quest.completedQuests.contains("quest_heartland_grain_mystery")
        if (hasForestQuest) {
            val forestBtn = Button(this).apply {
                text = "⚠ IDŹ DO LASU [QUEST]"
                styleToGrim()
                setOnClickListener {
                    val intent = Intent(this@CityActivity, QuestLocationActivity::class.java)
                    intent.putExtra("questId", "quest_forest_ancient_grove")
                    startActivity(intent)
                }
            }
            container.addView(forestBtn)
        }
    }
}
