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
        com.grimreich.systems.QuestSystem.seedIntegratedContent()

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

    override fun onResume() {
        super.onResume()
        val rawLocation = GameRepository.state.world.location
        val cityId = GameRepository.state.grimCurrentRegion ?: rawLocation.lowercase().replace(" ", "_")
        renderNpcs(cityId)
        renderQuestButtons()
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
            // UNIVERSAL HIDING LOGIC: Hide NPC if their specific associated quest is finished
            val isNpcFinished = com.grimreich.systems.QuestSystem.all().any { 
                (it.originRefId.lowercase() == prophetName.lowercase() || it.id.contains(prophetName.lowercase())) &&
                it.status == com.grimreich.systems.QuestStatus.UKONCZONE 
            }
            
            if (!isNpcFinished) {
                val prophetBtn = Button(androidx.appcompat.view.ContextThemeWrapper(this, R.style.GrimRegionButton), null, 0).apply {
                    text = "$prophetName (PROROK)"
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
        }

        val npcs = ProceduralNpcGenerator.generateForCity(cityId, com.grimreich.core.GrimConstants.World.NPC_GENERATION_SEED_OFFSET)
        npcs.forEach { npc ->
            val btn = Button(androidx.appcompat.view.ContextThemeWrapper(this, R.style.GrimRegionButton), null, 0).apply {
                text = "${npc.name} (${npc.role})"
                
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
                    val echoBtn = Button(androidx.appcompat.view.ContextThemeWrapper(this, R.style.GrimRegionButton), null, 0).apply {
                        text = "ECHO: ${echo.name} (${echo.currentCareer?.name ?: "Brak"})"
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

    private fun renderQuestButtons() {
        val state = GameRepository.state
        val container = findViewById<LinearLayout>(R.id.npcListContainer)

        // 1. CITY-BASED NPC QUESTS (e.g., Aelion's request)
        val activeCityQuests = state.quest.activeQuests.mapNotNull { com.grimreich.systems.QuestSystem.getQuest(it) }
            .filter { it.cityId == (state.grimCurrentRegion ?: "") && it.originType == com.grimreich.systems.QuestOriginType.LOKACJA_NPC }
        
        activeCityQuests.forEach { quest ->
            val questBtn = Button(androidx.appcompat.view.ContextThemeWrapper(this, R.style.GrimRegionButton), null, 0).apply {
                text = "⚠ PRZEJDŹ DO QUESTA: ${quest.title}"
                setTextColor(android.graphics.Color.parseColor("#ADFF2F"))
                setOnClickListener {
                    val intent = Intent(this@CityActivity, DialogueActivity::class.java).apply {
                        putExtra("npcName", quest.originRefId)
                        putExtra("npcRole", quest.originRefId) // USE NPC ID AS ROLE FOR PORTRAITS
                        putExtra("startNodeId", "${quest.originRefId}_quest_start")
                    }
                    startActivity(intent)
                }
            }
            container.addView(questBtn, 0)
        }

        // 2. FIELD QUESTS REMOVED FROM CITY - Moved to Hub exclusively
    }
}
