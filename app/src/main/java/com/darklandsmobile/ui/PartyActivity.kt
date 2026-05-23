package com.darklandsmobile.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.darklandsmobile.R
import com.darklandsmobile.core.GameRepository
import com.darklandsmobile.databinding.ActivityPartyBinding

class PartyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPartyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPartyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        render()
    }

    private fun render() {
        val party = GameRepository.state.party
        val sb = StringBuilder()
        sb.appendLine("=== DRUZYNA ===")
        sb.appendLine()
        party.members.forEachIndexed { i, hero ->
            sb.appendLine("${i + 1}. ${hero.name} (${hero.heroClass})")
            sb.appendLine("   HP: ${hero.hp}/${hero.maxHp}  Sila: ${hero.strength}  Zrecznosc: ${hero.dexterity}")
            sb.appendLine("   Wiara: ${hero.faith}  Reputacja: ${hero.reputation}")
            sb.appendLine()
        }
        binding.tvParty.text = sb.toString()
    }
}
