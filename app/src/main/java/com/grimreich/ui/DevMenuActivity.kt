package com.grimreich.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.core.AbilityRegistry
import com.grimreich.core.GameRepository
import com.grimreich.core.Hero
import com.grimreich.core.Trait
import com.grimreich.systems.GameLoopController
import java.util.UUID

class DevMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dev_menu)

        findViewById<Button>(R.id.btnDevBootstrap).setOnClickListener {
            devBootstrap()
            Toast.makeText(this, "Ralwing dołączył do drużyny!", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnInstantEndgame).apply {
            text = "INSTANT ENDGAME"
            setOnClickListener {
                instantEndgame()
                Toast.makeText(context, "Warunki finału spełnione!", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.jumpHub).setOnClickListener { jumpTo(HubActivity::class.java) }
        findViewById<Button>(R.id.jumpCreator).setOnClickListener { jumpTo(CharacterCreatorActivity::class.java) }
        findViewById<Button>(R.id.jumpCity).setOnClickListener { jumpTo(CityActivity::class.java) }
        findViewById<Button>(R.id.jumpCombat).setOnClickListener { jumpTo(CombatActivity::class.java) }
        findViewById<Button>(R.id.jumpMap).setOnClickListener { jumpTo(MapActivity::class.java) }
        findViewById<Button>(R.id.jumpInv).setOnClickListener { jumpTo(InventoryActivity::class.java) }
        findViewById<Button>(R.id.jumpChar).setOnClickListener { jumpTo(CharacterActivity::class.java) }
        findViewById<Button>(R.id.jumpTrade).setOnClickListener { jumpTo(TradeActivity::class.java) }
        findViewById<Button>(R.id.jumpSaints).setOnClickListener { jumpTo(SaintsActivity::class.java) }
        findViewById<Button>(R.id.jumpAlchemy).setOnClickListener { jumpTo(AlchemyActivity::class.java) }

        findViewById<Button>(R.id.btnBackFromDev).setOnClickListener {
            finish()
        }
    }

    private fun devBootstrap() {
        GameLoopController.bootstrap(seed = 1)
        
        val ralwing = Hero(
            id = "hero_ralwing",
            name = "Ralwing",
            age = 33,
            strength = 18,
            agility = 15,
            piety = 12,
            endurance = 14,
            intelligence = 13,
            perception = 16,
            charisma = 11,
            trait = Trait.SHADOW_BORN
        )
        ralwing.abilities.add(AbilityRegistry.SOLARIAN_STRIKE)
        ralwing.abilities.add(AbilityRegistry.SHADOW_VEIL)

        GameRepository.state.party.clear()
        GameRepository.state.party.add(ralwing)
        GameRepository.state.activeHeroId = ralwing.id
        GameRepository.state.gold = 5000
    }

    private fun instantEndgame() {
        if (GameRepository.state.party.isEmpty()) devBootstrap()
        val s = GameRepository.state
        s.world.globalStability = 95
        s.prayer.faith = 80
        s.prayer.virtue = 70
        s.prayer.sins = 0
        s.gold = 9999
        com.grimreich.systems.QuestSystem.seedIntegratedContent()
        com.grimreich.systems.QuestSystem.complete("eq1_signs")
        com.grimreich.systems.QuestSystem.complete("eq2_alliances")
        com.grimreich.systems.QuestSystem.complete("eq3_pilgrimage")
        com.grimreich.systems.ChronicleSystem.record("Kotwica odnalazła prawdę w Sercu Krainy.", 5)
    }

    private fun jumpTo(activityClass: Class<*>) {
        if (GameRepository.state.party.isEmpty()) {
            devBootstrap()
        }
        startActivity(Intent(this, activityClass))
    }
}
