package com.darklandsmobile.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.darklandsmobile.core.GameRepository
import com.darklandsmobile.core.SaintCatalogue
import com.darklandsmobile.core.ShrineType
import com.darklandsmobile.databinding.ActivityPrayerBinding
import com.darklandsmobile.systems.ReligionSystem

// Ekran modlitwy - aktywne czyny: modlitwa do losowego swietego (typ kapliczki CHAPEL),
// oraz "ofiarowanie" zlota (proste dodanie wiary i zmniejszenie zlota druzyny).
class PrayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        render()

        binding.btnPray.setOnClickListener {
            val saint = SaintCatalogue.all().random()
            val result = ReligionSystem.pray(saint.id, ShrineType.CHAPEL)
            binding.tvResult.text = result
            render()
        }

        binding.btnDonate.setOnClickListener {
            val amount = binding.etDonation.text.toString().toIntOrNull() ?: 0
            val result = donate(amount)
            binding.tvResult.text = result
            render()
        }
    }

    // Lokalna ofiara: zlota -> wiara (1zl = 1 punkt wiary, max 100).
    private fun donate(amount: Int): String {
        val s = GameRepository.state
        if (amount <= 0) return "Podaj dodatnia kwote."
        if (s.gold < amount) return "Za malo zlota (masz ${s.gold})."
        s.gold -= amount
        s.prayer.faith = (s.prayer.faith + amount).coerceAtMost(100)
        GameRepository.log("Ofiarowano $amount zlota. Wiara: ${s.prayer.faith}")
        return "Ofiarowano $amount zlota. Wiara: ${s.prayer.faith}"
    }

    private fun render() {
        val g = GameRepository.state
        val hero = g.party.firstOrNull()
        val sb = StringBuilder()
        sb.appendLine("=== MODLITWA ===")
        sb.appendLine()
        sb.appendLine("Wiara: ${g.prayer.faith}")
        sb.appendLine("Cnota: ${g.prayer.virtue}")
        sb.appendLine("Grzechy: ${g.prayer.sins}")
        sb.appendLine("Blogoslawienstwa: ${g.prayer.blessings}")
        if (hero != null) sb.appendLine("Aktywny bohater: ${hero.name} (poboznosc ${hero.piety})")
        sb.appendLine("Zloto druzyny: ${g.gold}")
        binding.tvPrayer.text = sb.toString()
    }
}
