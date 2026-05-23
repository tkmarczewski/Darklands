package com.darklandsmobile.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.darklandsmobile.core.GameRepository
import com.darklandsmobile.databinding.ActivityPrayerBinding
import com.darklandsmobile.systems.PrayerSystem

class PrayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        render()

        binding.btnPray.setOnClickListener {
            val result = PrayerSystem.pray()
            binding.tvResult.text = result
            render()
        }

        binding.btnDonate.setOnClickListener {
            val amount = binding.etDonation.text.toString().toIntOrNull() ?: 0
            val result = PrayerSystem.donate(amount)
            binding.tvResult.text = result
            render()
        }
    }

    private fun render() {
        val g = GameRepository.state
        val hero = g.party.members.firstOrNull() ?: return
        val sb = StringBuilder()
        sb.appendLine("=== MODLITWA ===")
        sb.appendLine()
        sb.appendLine("Wiara: ${hero.faith}")
        sb.appendLine("Bozek: ${hero.deity}")
        sb.appendLine("Zloto druzyny: ${g.party.gold}")
        binding.tvPrayer.text = sb.toString()
    }
}
