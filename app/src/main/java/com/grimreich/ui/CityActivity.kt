package com.grimreich.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.core.GameRepository
import com.grimreich.ui.city.CityScreen
import com.grimreich.ui.city.CityViewModel
import com.grimreich.world.CityCatalogue

class CityActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ensure canonical data is available
        CityCatalogue.seedCanonical()
        com.grimreich.systems.QuestSystem.seedIntegratedContent()

        val viewModel = CityViewModel()

        setContent {
            CityScreen(
                viewModel = viewModel,
                onMarket = { startActivity(Intent(this, TradeActivity::class.java)) },
                onTavern = { startActivity(Intent(this, TavernActivity::class.java)) },
                onTemple = { startActivity(Intent(this, SaintsActivity::class.java)) },
                onRecruit = { startActivity(Intent(this, RecruitmentActivity::class.java)) },
                onNpcClick = { name, role, startNode ->
                    val intent = Intent(this, DialogueActivity::class.java).apply {
                        putExtra("npcName", name)
                        putExtra("npcRole", role)
                        putExtra("startNodeId", startNode)
                    }
                    startActivity(intent)
                },
                onExit = { finish() }
            )
        }

        // Zdarzenia losowe w mieście
        com.grimreich.systems.RandomEventManager.triggerCityEvent(this)

        // VERDICT CAMPAIGN BREADCRUMBS
        handleVerdictBreadcrumbs()
    }

    private fun handleVerdictBreadcrumbs() {
        val state = GameRepository.state
        state.world.cityEntryCount++
        
        when (state.world.cityEntryCount) {
            1 -> UiUtils.showNarrativePopup(this, "WIEŚCI Z MIASTA", "Przy bramie strażnik rzuca mimochodem: \"Ostatnio u nas niespokojnie… jednego urzędnika znaleźli martwego w gabinecie. Bez śladów. Na ścianie wypalono: WYROK WYKONANY.\"")
            3 -> UiUtils.showNarrativePopup(this, "KRONIKA ZAGINIĘĆ", "Na tablicy ogłoszeń widzisz świeży pergamin: ZAGINĘŁA ARCHIWISTKA IMPERIUM. Mieszkanie puste, na drzwiach runa: WYMAZANA.")
            5 -> UiUtils.showNarrativePopup(this, "TRAGEDIA W FABRYCE", "Ludzie szepczą o eksplozji w fabryce zbrojeniowej. Dziewiętnaście trupów bez ran. Na ścianie wypalono jedno słowo: WINNI.")
        }
    }
}
