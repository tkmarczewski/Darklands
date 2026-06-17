package com.grimreich.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.core.GameRepository
import com.grimreich.systems.RealTimeEventManager
import com.grimreich.systems.SaveLoadSystem
import com.grimreich.systems.CalendarAuraSystem
import com.grimreich.world.CityCatalogue

class HubActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hub)
        
        // Ensure content is seeded if missing (fixes missing quests after load)
        com.grimreich.systems.QuestSystem.seedIntegratedContent()
        
        // CONSOLIDATED ONTOLOGICAL STATUS
        val realTimeEvent = RealTimeEventManager.checkRealTimeEvents(this)
        val aura = CalendarAuraSystem.getCurrentAura()
        
        if (realTimeEvent != null || aura.name != "DZIEŃ FENOMENÓW") {
            val message = buildString {
                if (realTimeEvent != null) {
                    append(realTimeEvent)
                    append("\n\n")
                }
                if (aura.name != "DZIEŃ FENOMENÓW") {
                    append("AURA DNIA: ${aura.name}\n")
                    append("${aura.description}\n")
                    append("Efekt: ${aura.effect}")
                }
            }
            UiUtils.showNarrativePopup(this, "ONTOLOGIA DNIA", message)
        }

        setupNavigation()
        setupDevTrigger()
        
        // Zdarzenia losowe w HUBie
        com.grimreich.systems.RandomEventManager.triggerHubEvent(this)

        render()
    }

    private fun setupNavigation() {
        findViewById<Button>(R.id.openCity)?.setOnClickListener { 
            startActivity(Intent(this, CityActivity::class.java)) 
        }
        findViewById<Button>(R.id.openMap)?.setOnClickListener { 
            startActivity(Intent(this, MapActivity::class.java)) 
        }
        findViewById<Button>(R.id.openInventory)?.setOnClickListener { 
            startActivity(Intent(this, InventoryActivity::class.java)) 
        }
        findViewById<Button>(R.id.openQuests)?.setOnClickListener { 
            startActivity(Intent(this, QuestJournalActivity::class.java)) 
        }
        findViewById<Button>(R.id.openReputation)?.setOnClickListener { 
            startActivity(Intent(this, ReputationActivity::class.java)) 
        }
        findViewById<Button>(R.id.openSaints)?.setOnClickListener { 
            startActivity(Intent(this, SaintsActivity::class.java)) 
        }
        findViewById<Button>(R.id.openCityEvents)?.setOnClickListener {
             startActivity(Intent(this, CityEventsActivity::class.java))
        }
        findViewById<Button>(R.id.openTransfer)?.setOnClickListener {
             // CRITICAL: Refresh seeding before opening transfer to ensure names/state are fresh
             com.grimreich.systems.QuestSystem.seedIntegratedContent()
             startActivity(Intent(this, InventoryTransferActivity::class.java))
        }
        findViewById<Button>(R.id.openFinale)?.setOnClickListener {
             startActivity(Intent(this, FinaleActivity::class.java))
        }
        findViewById<Button>(R.id.openCombatStatus)?.setOnClickListener { 
             startActivity(Intent(this, CombatActivity::class.java)) 
        }
    }

    private fun setupDevTrigger() {
        findViewById<TextView>(R.id.tvDevMenuTrigger)?.setOnClickListener {
            startActivity(Intent(this, DevMenuActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        render()
        SaveLoadSystem.save(this)
    }

    private fun render() {
        val state = GameRepository.state
        val world = state.world
        
        val timeLabel = when(world.timeOfDay.lowercase()) {
            "morning" -> "Poranek"
            "midday" -> "Południe"
            "afternoon" -> "Popołudnie"
            "dusk" -> "Zmierzch"
            "evening" -> "Wieczór"
            else -> world.timeOfDay
        }
        
        val cityData = com.grimreich.world.CityCatalogue.get(state.grimCurrentRegion)
        val locationName = cityData?.name ?: world.location.replace("_", " ")
        val formattedName = locationName.uppercase(java.util.Locale.ROOT)
        
        findViewById<TextView>(R.id.tvTime)?.text = "$formattedName | DZIEŃ ${world.day} | $timeLabel"

        // Active Quest Log Mini
        val activeQuest = com.grimreich.systems.QuestSystem.all().find { it.status == com.grimreich.systems.QuestStatus.AKTYWNE }
        findViewById<TextView>(R.id.tvLogMini)?.text = if (activeQuest != null) {
            "AKTYWNE: ${activeQuest.title}\nZADANIE: ${activeQuest.objective}"
        } else {
            "Kroniki pękniętego świata..."
        }

        findViewById<Button>(R.id.openCombatStatus)?.visibility = 
            if (state.combat.active) View.VISIBLE else View.GONE
            
        findViewById<Button>(R.id.openFinale)?.visibility = 
            if (world.globalStability < 30 || com.grimreich.systems.QuestSystem.all().find { it.id == "eq3_pilgrimage" }?.status == com.grimreich.systems.QuestStatus.UKONCZONE) View.VISIBLE else View.GONE

        renderFieldQuestButtons()
        renderPartyStrip()
    }

    private fun renderFieldQuestButtons() {
        val state = GameRepository.state
        val container = findViewById<LinearLayout>(R.id.fieldQuestContainer) ?: return
        container.removeAllViews()

        // 1. COLLECT ALL ACTIVE FIELD QUESTS
        val activeFieldQuests = state.quest.activeQuests.mapNotNull { com.grimreich.systems.QuestSystem.getQuest(it) }
            .filter { it.originType == com.grimreich.systems.QuestOriginType.LOKACJA_PROCEDURALNA }

        activeFieldQuests.forEach { quest ->
            val btn = Button(this).apply {
                text = "⚠ EKSPEDYCJA: ${quest.title}"
                styleToGrim()
                setOnClickListener {
                    // Logic to start combat or location activity
                    state.pendingQuestId = quest.id
                    startActivity(Intent(this@HubActivity, CombatActivity::class.java))
                }
            }
            container.addView(btn)
        }

        // 2. NARRATIVE CANONICALS (Manual check for legacy ones if not in QuestSystem)
        if (state.quest.activeQuests.contains("quest_north_mist_vision")) {
            val btn = Button(this).apply {
                text = "⚠ WYBRZEŻE [AKTYWNE]"
                styleToGrim()
                setOnClickListener {
                    startActivity(Intent(this@HubActivity, CoastlineActivity::class.java))
                }
            }
            container.addView(btn)
        }
    }

    private fun Button.styleToGrim() {
        this.setTextColor(androidx.core.content.ContextCompat.getColor(context, R.color.grimGold))
        this.setBackgroundColor(android.graphics.Color.parseColor("#80000000"))
        this.setPadding(8, 8, 8, 8)
        this.textSize = 10f
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 0, 4)
        this.layoutParams = params
    }

    private fun renderPartyStrip() {
        val party = GameRepository.state.party
        val slotIds = listOf(R.id.charSlot0, R.id.charSlot1, R.id.charSlot2, R.id.charSlot3)
        val nameIds = listOf(R.id.tvChar0Name, R.id.tvChar1Name, R.id.tvChar2Name, R.id.tvChar3Name)
        val hpIds = listOf(R.id.pbChar0HP, R.id.pbChar1HP, R.id.pbChar2HP, R.id.pbChar3HP)
        val portIds = listOf(R.id.ivChar0Portrait, R.id.ivChar1Portrait, R.id.ivChar2Portrait, R.id.ivChar3Portrait)

        slotIds.forEachIndexed { i, slotId ->
            val slotContainer = findViewById<View>(slotId) ?: return@forEachIndexed
            if (i < party.size) {
                slotContainer.visibility = View.VISIBLE
                val hero = party[i]
                findViewById<TextView>(nameIds[i])?.text = hero.name
                findViewById<ProgressBar>(hpIds[i])?.progress = if (hero.maxHp > 0) (hero.hp * 100 / hero.maxHp).coerceAtMost(100) else 0
                
                val portrait = findViewById<ImageView>(portIds[i])
                val resId = resources.getIdentifier(hero.portraitRes, "drawable", packageName)
                if (resId != 0) portrait?.setImageResource(resId)

                slotContainer.setOnClickListener {
                    val intent = Intent(this, CharacterActivity::class.java)
                    intent.putExtra("heroId", hero.id)
                    startActivity(intent)
                }
            } else {
                slotContainer.visibility = View.INVISIBLE
            }
        }
    }
}
