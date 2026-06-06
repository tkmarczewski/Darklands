package com.grimreich.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.core.GameRepository
import com.grimreich.databinding.ActivityPrayerBinding
import com.grimreich.systems.ReligionSystem

class PrayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        render()

        binding.btnPray.setOnClickListener {
            val hero = GameRepository.state.party.firstOrNull()
            if (hero != null) {
                val result = ReligionSystem.pray(hero)
                binding.tvResult.text = result
                render()
            } else {
                binding.tvResult.text = "Brak bohatera w drużynie."
            }
        }

        binding.btnDonate.setOnClickListener {
            val amount = binding.etDonation.text.toString().toIntOrNull() ?: 0
            val result = donate(amount)
            binding.tvResult.text = result
            render()
        }
    }

    private fun donate(amount: Int): String {
        val s = GameRepository.state
        if (amount <= 0) return "Podaj dodatnią kwotę."
        if (s.gold < amount) return "Za mało złota (masz ${s.gold})."
        s.gold -= amount
        s.prayer.faith = (s.prayer.faith + amount / 5).coerceAtMost(150)
        return "Ofiarowano $amount złota. Wiara wzrosła."
    }

    private fun render() {
        val g = GameRepository.state
        val hero = g.party.firstOrNull()
        val sb = StringBuilder()
        sb.appendLine("=== MROCZNA MODLITWA ===")
        sb.appendLine()
        sb.appendLine("Uznanie Proroków: ${g.prayer.faith}")
        sb.appendLine("Stabilność Duszy: ${g.prayer.virtue}")
        sb.appendLine("Skażenie: ${g.prayer.sins}")
        sb.appendLine("Otrzymane Wizje: ${g.prayer.blessings.joinToString(", ")}")
        if (hero != null) sb.appendLine("Aktywny bohater: ${hero.name} (pobozność ${hero.piety})")
        sb.appendLine("Złoto drużyny: ${g.gold}")
        binding.tvPrayer.text = sb.toString()
    }
}
