package com.grimreich.systems

import com.grimreich.core.*

enum class EndingType {
    GOOD, PRAGMATIC, REDEMPTION, CORRUPTED
}

data class Ending(
    val type: EndingType,
    val title: String,
    val description: String
)

object EndingSystem {
    fun resolveEnding(gameState: GameState): Ending {
        val faith = gameState.prayer.faith
        val virtue = gameState.prayer.virtue
        val cityRep = gameState.reputation.city.values.sum()
        val sins = gameState.prayer.sins
        val stability = gameState.world.globalStability
        
        return when {
            faith >= 60 && virtue >= 50 && stability >= 80 && sins <= 2 -> 
                Ending(EndingType.GOOD, "Święte Odrodzenie", "GrimReich zostało oczyszczone z mroku dzięki twojej niezłomnej wierze.")
            faith >= 30 && stability >= 40 -> 
                Ending(EndingType.PRAGMATIC, "Kruchy Pokój", "Mrok został powstrzymany, ale blizny na duszy krainy pozostaną na zawsze.")
            stability < 30 || sins >= 10 -> 
                Ending(EndingType.CORRUPTED, "Wieczna Noc", "Uległeś pokusie mroku. GrimReich stało się częścią Drugiej Strony.")
            else -> 
                Ending(EndingType.REDEMPTION, "Gorzkie Odkupienie", "Mimo wielu błędów, w ostatniej chwili odnalazłeś ścieżkę do światła.")
        }
    }

    fun finaleStatus(): String {
        val s = GameRepository.state
        val faith = s.prayer.faith
        val sins = s.prayer.sins
        val stability = s.world.globalStability
        val avgSanity = if (s.party.isNotEmpty()) s.party.map { it.sanity }.average().toInt() else 100
        
        val potentialEnding = resolveEnding(s)
        
        return """
            === FINAŁ GRIMREICH ===
            Aktualne Przewidywanie: ${potentialEnding.title}
            
            Stabilność Świata: $stability%
            Wiara Drużyny: $faith
            Grzechy: $sins
            Średnia Poczytalność: $avgSanity%
            
            Kronika zawiera ${ChronicleSystem.getAll().size} kluczowych wpisów.
        """.trimIndent()
    }
}
