package com.grimreich.systems

import androidx.compose.ui.graphics.Color
import com.grimreich.core.GameRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VisualContentSystem @Inject constructor(
    private val gameRepository: GameRepository
) {
    fun getHubTintColor(stability: Int): Color {
        return when {
            stability > 80 -> Color.Transparent
            stability > 50 -> Color(0x30000000)
            stability > 30 -> Color(0x40300000) // Slight red tint
            else -> Color(0x60600000) // Heavy dark red tint for Era of Fracture
        }
    }

    fun getHubBackground(regionId: String, stability: Int): String {
        // Basic implementation: can be expanded with region-specific assets
        return if (stability < 30) "bg_finale" else "bg_party_castle"
    }

    fun getAtmosphericMessage(stability: Int): String {
        return when {
            stability > 90 -> "Czystość eteru jest niemal oślepiająca."
            stability > 70 -> "Świat wydaje się solidny i przewidywalny."
            stability > 50 -> "Cienie wydają się nieco dłuższe niż zazwyczaj."
            stability > 30 -> "Powietrze smakuje metalem. Granice drżą."
            else -> "Rzeczywistość pęka na Twoich oczach. Słyszysz Echa."
        }
    }
}
