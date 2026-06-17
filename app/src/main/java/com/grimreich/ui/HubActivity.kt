package com.grimreich.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.core.GameRepository
import com.grimreich.ui.main.HubScreen
import com.grimreich.ui.main.HubViewModel

class HubActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = HubViewModel()

        setContent {
            HubScreen(
                viewModel = viewModel,
                onCity = { startActivity(Intent(this, CityActivity::class.java)) },
                onMap = { startActivity(Intent(this, MapActivity::class.java)) },
                onInventory = { startActivity(Intent(this, InventoryActivity::class.java)) },
                onQuests = { startActivity(Intent(this, QuestJournalActivity::class.java)) },
                onWorldLog = { startActivity(Intent(this, WorldLogActivity::class.java)) },
                onCharacter = { heroId ->
                    val intent = Intent(this, CharacterActivity::class.java)
                    intent.putExtra("heroId", heroId)
                    startActivity(intent)
                }
            )
        }
    }
}
