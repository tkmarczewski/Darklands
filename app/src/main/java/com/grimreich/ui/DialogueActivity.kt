package com.grimreich.ui

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.core.GameRepository
import com.grimreich.grimreich.v1.DialogueNode
import com.grimreich.systems.DialogueManager

class DialogueActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialogue)

        val npcName = intent.getStringExtra("npcName") ?: "Nieznajomy"
        val npcRole = intent.getStringExtra("npcRole") ?: "Echo"
        val startNodeId = intent.getStringExtra("startNodeId") ?: "end"

        findViewById<TextView>(R.id.tvNpcName).text = npcName
        findViewById<TextView>(R.id.tvNpcRole).text = npcRole

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

        findViewById<TextView>(R.id.tvDialogueText).text = node.text
        
        val container = findViewById<LinearLayout>(R.id.choicesContainer)
        container.removeAllViews()

        node.choices.forEach { choice ->
            val btn = Button(this)
            btn.text = choice.text
            btn.setOnClickListener {
                choice.onSelect(GameRepository.state)
                displayNode(choice.targetNodeId)
            }
            container.addView(btn)
        }
        
        // Always add an 'Exit' option if no choices
        if (node.choices.isEmpty()) {
            val btn = Button(this)
            btn.text = "Odejdź"
            btn.setOnClickListener { finish() }
            container.addView(btn)
        }
    }
}
