package com.grimreich.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.core.GameRepository
import com.grimreich.databinding.ActivityPartyBinding

// Ekran druzyny - prosty dump skladu z atrybutami pierwszej linii statystyk.
class PartyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPartyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPartyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        render()
    }

    private fun render() {
        val state = GameRepository.state
        val party = state.party
        val sb = StringBuilder()
        sb.appendLine("=== DRUZYNA ===")
        sb.appendLine()
        party.forEachIndexed { i, hero ->
            val careerName = hero.currentCareer?.displayName ?: "Bez kariery"
            sb.appendLine("${i + 1}. ${hero.name} ($careerName)")
            sb.appendLine("   HP: ${hero.hp}/${hero.maxHp}  Sila: ${hero.strength}  Zwinnosc: ${hero.agility}")
            sb.appendLine("   Wiara: ${state.prayer.faith}  Cnota: ${hero.virtue}")
            sb.appendLine()
        }
        sb.appendLine("Zloto druzyny: ${state.gold}")
        binding.tvParty.text = sb.toString()
    }
}
