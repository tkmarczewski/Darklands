package com.grimreich.ui

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.core.GameRepository
import com.grimreich.systems.DialogueManager
import com.grimreich.world.CityCatalogue

class DialogueActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialogue)

        val npcName = intent.getStringExtra("npcName") ?: "Nieznajomy"
        val npcRole = intent.getStringExtra("npcRole") ?: "Echo"
        val startNodeId = intent.getStringExtra("startNodeId") ?: "end"

        findViewById<TextView>(R.id.tvNpcName).text = npcName
        findViewById<TextView>(R.id.tvNpcRole).text = npcRole.uppercase()

        // Set regional background
        val currentCityId = GameRepository.state.grimCurrentRegion
        val city = CityCatalogue.get(currentCityId)
        if (city != null) {
            val bgRes = resources.getIdentifier(city.backgroundDrawable, "drawable", packageName)
            if (bgRes != 0) {
                findViewById<ImageView>(R.id.ivDialogueBg).setImageResource(bgRes)
            }
        }

        // Set NPC portrait
        val portraitName = DialogueManager.getPortrait(npcRole)
        val portResId = resources.getIdentifier(portraitName, "drawable", packageName)
        if (portResId != 0) {
            findViewById<ImageView>(R.id.ivNpcPortrait).setImageResource(portResId)
        }

        displayNode(startNodeId)
    }

    private fun displayNode(nodeId: String) {
        if (nodeId == "end") {
            finish()
            return
        }

        val node = DialogueManager.getNode(nodeId) ?: run {
            finish()
            return
        }

        val tvDialogue = findViewById<TextView>(R.id.tvDialogueText)
        val rawText = node.text
        
        if (rawText.contains("[WYMAZANO]") || rawText.contains("GŁOSY")) {
            val spannable = android.text.SpannableString(rawText)
            val color = android.graphics.Color.parseColor("#FF4444")
            
            // Simple highlighting for glitched words
            val words = listOf("[WYMAZANO]", "[NIE SŁUCHAJ ICH]", "GŁOSY", "ABSOLUT")
            words.forEach { word ->
                var start = rawText.indexOf(word)
                while (start != -1) {
                    spannable.setSpan(
                        android.text.style.ForegroundColorSpan(color),
                        start, start + word.length,
                        android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    start = rawText.indexOf(word, start + 1)
                }
            }
            tvDialogue.text = spannable
        } else {
            tvDialogue.text = rawText
        }
        
        val container = findViewById<LinearLayout>(R.id.choicesContainer)
        container.removeAllViews()

        node.choices.forEach { choice ->
            val btn = Button(androidx.appcompat.view.ContextThemeWrapper(this, R.style.GrimCombatButton), null, 0)
            btn.text = choice.text
            btn.setOnClickListener {
                choice.onSelect(GameRepository.state)
                displayNode(choice.targetNodeId)
            }
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 12)
            }
            btn.layoutParams = lp
            container.addView(btn)
        }
        
        if (node.choices.isEmpty()) {
            val btn = Button(androidx.appcompat.view.ContextThemeWrapper(this, R.style.GrimBackButton), null, 0)
            btn.text = "ODEJDŹ"
            btn.setOnClickListener { finish() }
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            btn.layoutParams = lp
            container.addView(btn)
        }
    }
}
