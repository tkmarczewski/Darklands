package com.grimreich.ui.region

import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R

class RegionActivity : AppCompatActivity() {
    private val vm: GrimRegionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_region)

        val tvRegionName = findViewById<TextView>(R.id.tvRegionName)
        val tvEmotionalState = findViewById<TextView>(R.id.tvEmotionalState)
        val tvMistMind = findViewById<TextView>(R.id.tvMistMind)
        val tvBloodBody = findViewById<TextView>(R.id.tvBloodBody)
        val tvReflectionSoul = findViewById<TextView>(R.id.tvReflectionSoul)
        val tvChaosLevel = findViewById<TextView>(R.id.tvChaosLevel)
        val tvTimeEffects = findViewById<TextView>(R.id.tvTimeEffects)
        val tvMemory = findViewById<TextView>(R.id.tvMemory)
        val tvReactions = findViewById<TextView>(R.id.tvReactions)
        val tvEndingImpact = findViewById<TextView>(R.id.tvEndingImpact)

        vm.snapshot.observe(this) { s ->
            if (s == null) return@observe
            tvRegionName.text = s.regionName
            tvEmotionalState.text = "Nastroj: ${s.emotionalState}"
            tvMistMind.text = "Mgla: ${s.mistMind}"
            tvBloodBody.text = "Krew: ${s.bloodBody}"
            tvReflectionSoul.text = "Odbicie: ${s.reflectionSoul}"
            tvChaosLevel.text = "Chaos: ${s.chaosLevel} | Mist: ${s.mistTimeLevel}"
            tvTimeEffects.text = "Efekty czasu: ${s.timeEffects.joinToString(", ")}"
            tvMemory.text = "Pamiec: ${s.memory.joinToString(", ")}"
            tvReactions.text = "Reakcje: ${s.reactions.joinToString(", ")}"
            tvEndingImpact.text = "Zakonczenie: ${s.endingImpact}"
        }

        vm.openRegion(GrimRegionNavigation.extractRegion(intent))
    }
}
