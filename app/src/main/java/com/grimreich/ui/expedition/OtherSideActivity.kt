package com.grimreich.ui.expedition

import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.grimreich.v1.OtherSideLoyalty

class OtherSideActivity : AppCompatActivity() {
    private val vm: OtherSideViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_side)

        val tvName = findViewById<TextView>(R.id.tvExpeditionName)
        val tvLayers = findViewById<TextView>(R.id.tvLayers)
        val tvEnemies = findViewById<TextView>(R.id.tvEnemies)
        val tvRewards = findViewById<TextView>(R.id.tvRewards)
        val tvImpact = findViewById<TextView>(R.id.tvRegionImpact)
        val tvEnding = findViewById<TextView>(R.id.tvEndingImpact)
        val tvNpc = findViewById<TextView>(R.id.tvNpcStates)

        vm.state.observe(this) { s ->
            if (s == null) return@observe
            tvName.text = s.name
            tvLayers.text = "Warstwy: L=${s.logicalLayer}, S=${s.symmetricLayer}, 0=${s.zeroLayer}"
            tvEnemies.text = "Wrogowie: ${s.enemies.joinToString(", ")}"
            tvRewards.text = "Lupy: ${s.rewards.joinToString(", ")}"
            tvImpact.text = "Wplyw na region: ${s.regionImpact}"
            tvEnding.text = "Wplyw na zakonczenie: ${s.endingImpact}"
            tvNpc.text = if (s.npcStates.isEmpty()) {
                "Brak towarzyszy w wyprawie."
            } else {
                buildString {
                    s.npcStates.forEach { st ->
                        val loyalty = when (st.loyalty) {
                            OtherSideLoyalty.LOYAL -> "Lojalny"
                            OtherSideLoyalty.TORN -> "Rozdarty"
                            OtherSideLoyalty.BETRAYER -> "Zdrajca"
                        }
                        append(st.npcName)
                        append(" - ")
                        append(loyalty)
                        append(" | Ryzyko: ")
                        append(st.deathRisk)
                        append(" | Nagrody: ")
                        append(st.rewardModifier)
                        append('\n')
                    }
                    append(s.rewardSummary.riskNote)
                }.trim()
            }
        }
        vm.startForCurrentRegion()
    }
}
