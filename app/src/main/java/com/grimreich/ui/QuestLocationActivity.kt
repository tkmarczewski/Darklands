package com.grimreich.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.core.GameRepository
import com.grimreich.systems.QuestSystem
import com.grimreich.systems.CombatSystem
import com.grimreich.ui.UiUtils.styleToGrim

class QuestLocationActivity : AppCompatActivity() {
    private lateinit var questId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quest_location)

        questId = intent.getStringExtra("questId") ?: ""
        if (questId.isEmpty()) {
            finish()
            return
        }

        renderQuestLocation()

        findViewById<Button>(R.id.btnExitQuestLocation).setOnClickListener {
            finish()
        }
    }

    private fun renderQuestLocation() {
        val tvTitle = findViewById<TextView>(R.id.tvQuestLocationTitle)
        val tvDesc = findViewById<TextView>(R.id.tvQuestLocationDescription)
        val container = findViewById<LinearLayout>(R.id.questLocationActionsContainer)

        when (questId) {
            "quest_crown_blood_toll" -> renderBloodToll(tvTitle, tvDesc, container)
            "quest_crown_iron_forge" -> renderIronForge(tvTitle, tvDesc, container)
            "quest_heart_mirror_truth" -> renderMirrorTruth(tvTitle, tvDesc, container)
            "quest_eq1_signs" -> renderSigns(tvTitle, tvDesc, container)
            "quest_eq2_alliances" -> renderAlliances(tvTitle, tvDesc, container)
            "quest_eq3_pilgrimage" -> renderPilgrimage(tvTitle, tvDesc, container)
            else -> {
                tvTitle.text = "NIEZNANA LOKACJA"
                tvDesc.text = "Nie można odnaleźć tej lokacji."
            }
        }
    }

    // CROWN: Blood Toll - Równiny, pokonaj wrogów i złóż ofiarę
    private fun renderBloodToll(title: TextView, desc: TextView, container: LinearLayout) {
        title.text = "RÓWNINY - OŁTARZ KRWI"
        desc.text = "Przed tobą starożytny ołtarz. Czujesz obecność wrogów."

        val btnFight = Button(this).apply {
            text = "WALCZ Z WROGAMI"
            styleToGrim()
            setOnClickListener {
                // Start combat encounter
                GameRepository.state.pendingQuestId = questId
                CombatSystem.startEncounterForQuest(questId)
                startActivity(Intent(this@QuestLocationActivity, CombatActivity::class.java))
            }
        }
        container.addView(btnFight)
    }

    // CROWN: Iron Forge - dostawa rudy do Ferruna
    private fun renderIronForge(title: TextView, desc: TextView, container: LinearLayout) {
        title.text = "RÓWNINY - KUŹNIA FERRUNA"
        desc.text = "Ferrun, potworny kowal, czeka na rzadką rudę."

        val btnDeliver = Button(this).apply {
            text = "DOSTARCZ RUDĘ"
            styleToGrim()
            setOnClickListener {
                AlertDialog.Builder(this@QuestLocationActivity)
                    .setTitle("Ferrun")
                    .setMessage("Podajesz rudę Ferrunowi. Jego oczy płoną.\n\n\"Dobra robota, ludziku. To rzadki metal z samego serca ziemi. Jutro wykuję ci broń, która przebije każdą zbroję.\"\n\nOtrzymujesz: +50 złota, +2 reputacji")
                    .setPositiveButton("Ukończ") { _, _ ->
                        completeQuest(50, 2)
                    }
                    .show()
            }
        }
        container.addView(btnDeliver)
    }

    // HEART: Mirror Truth - lustro i walka z odbiciem
    private fun renderMirrorTruth(title: TextView, desc: TextView, container: LinearLayout) {
        title.text = "SERCE KRAINY - LUSTRO PRAWDY"
        desc.text = "Starożytne lustro odbija twój obraz... ale coś jest nie tak."

        val btnLook = Button(this).apply {
            text = "SPÓJRZ W LUSTRO"
            styleToGrim()
            setOnClickListener {
                AlertDialog.Builder(this@QuestLocationActivity)
                    .setTitle("Lustro Prawdy")
                    .setMessage("Patrzysz w lustro. Twoje odbicie uśmiecha się złowrogo i wychodzi z powierzchni lustra!\n\nMusisz walczyć ze sobą!")
                    .setPositiveButton("WALCZ!") { _, _ ->
                        GameRepository.state.pendingQuestId = questId
                        CombatSystem.startEncounterForQuest(questId)
                        startActivity(Intent(this@QuestLocationActivity, CombatActivity::class.java))
                    }
                    .show()
            }
        }
        container.addView(btnLook)
    }

    // EQ1: Signs - znajdź 3 kapliczki
    private fun renderSigns(title: TextView, desc: TextView, container: LinearLayout) {
        title.text = "SERCE KRAINY - KAPLICZKI"
        desc.text = "Musisz znaleźć 3 kapliczki korupcji i oczyścić je."

        val state = GameRepository.state
        val progress = state.quest.questProgress[questId] ?: 0

        val tvProgress = TextView(this).apply {
            text = "Oczyszczono: $progress/3"
            textSize = 14f
            setTextColor(android.graphics.Color.parseColor("#c8a96e"))
        }
        container.addView(tvProgress)

        if (progress < 3) {
            val btnCleanse = Button(this).apply {
                text = "OCZYŚĆ NASTĘPNĄ KAPLICZKĘ"
                styleToGrim()
                setOnClickListener {
                    val newProgress = progress + 1
                    state.quest.questProgress[questId] = newProgress
                    if (newProgress >= 3) {
                        completeQuest(com.grimreich.core.GrimConstants.Economy.QUEST_REWARD_GOLD_ENDGAME_MID, 5)
                    } else {
                        state.logEntries.add(0, "Oczyszczono kapliczkę ($newProgress/3)")
                        finish()
                    }
                }
            }
            container.addView(btnCleanse)
        } else {
            val btnComplete = Button(this).apply {
                text = "UKOŃCZ QUEST"
                styleToGrim()
                setOnClickListener {
                    completeQuest(com.grimreich.core.GrimConstants.Economy.QUEST_REWARD_GOLD_ENDGAME_MID, 5)
                }
            }
            container.addView(btnComplete)
        }
    }

    // EQ2: Alliances - przekonaj Rycerzy
    private fun renderAlliances(title: TextView, desc: TextView, container: LinearLayout) {
        title.text = "TWIERDZA RYCERZY"
        desc.text = "Dowódca Rycerzy słucha twojej prośby o wsparcie."

        val btnPersuade = Button(this).apply {
            text = "PRZEKONAJ RYCERZY"
            styleToGrim()
            setOnClickListener {
                AlertDialog.Builder(this@QuestLocationActivity)
                    .setTitle("Dowódca Rycerzy")
                    .setMessage("\"Twoje słowa brzmią szczerze, wędrowcze. Widzieliśmy znaki korupcji na naszych ziemiach. Jeśli naprawdę walczysz przeciw ciemności, Rycerze staną u twojego boku.\"\n\nOtrzymujesz wsparcie Rycerzy!")
                    .setPositiveButton("Dziękuję") { _, _ ->
                        completeQuest(com.grimreich.core.GrimConstants.Economy.QUEST_REWARD_GOLD_ENDGAME_LIGHT, 5)
                    }
                    .show()
            }
        }
        container.addView(btnPersuade)
    }

    // EQ3: Pilgrimage - Brama Absolutu
    private fun renderPilgrimage(title: TextView, desc: TextView, container: LinearLayout) {
        title.text = "BRAMA ABSOLUTU"
        desc.text = "Przed tobą legendarny portal. Czujesz moc płynącą z otchłani."

        val btnEnter = Button(this).apply {
            text = "WEJDŹ DO BRAMY"
            styleToGrim()
            setOnClickListener {
                AlertDialog.Builder(this@QuestLocationActivity)
                    .setTitle("Brama Absolutu")
                    .setMessage("Przekraczasz próg Bramy. Światłość ogłusza cię, a czas zdaje się zatrzymywać.\n\nGłos rozlega się w twojej głowie: \"Dokonaj wyboru, śmiertelniku. Przyjąć dar czy odrzucić światło?\"\n\nTo jest koniec twojej podróży.")
                    .setPositiveButton("PRZYJMIJ DAR") { _, _ ->
                        completeQuest(com.grimreich.core.GrimConstants.Economy.QUEST_REWARD_GOLD_ENDGAME_HEAVY, 10)
                    }
                    .setNegativeButton("ODRZUĆ") { _, _ ->
                        completeQuest(com.grimreich.core.GrimConstants.Economy.QUEST_REWARD_GOLD_CROWN, 5)
                    }
                    .show()
            }
        }
        container.addView(btnEnter)
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

    private fun completeQuest(gold: Int, reputation: Int) {
        val state = GameRepository.state
        QuestSystem.complete(questId)
        state.gold += gold
        val currentCity = state.grimCurrentRegion
        val currentRep = state.reputation.city[currentCity] ?: 0
        state.reputation.city[currentCity] = currentRep + reputation
        state.logEntries.add(0, "Ukończono quest - otrzymano ${gold}z i +${reputation} reputacji")
        finish()
    }
}
