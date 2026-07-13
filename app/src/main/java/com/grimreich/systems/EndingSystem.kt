package com.grimreich.systems

import com.grimreich.core.*
import javax.inject.Inject
import javax.inject.Singleton

enum class EndingType {
    GOOD, PRAGMATIC, REDEMPTION, CORRUPTED, OBSERVED
}

data class Ending(
    val type: EndingType,
    val title: String,
    val description: String
)

@Singleton
class EndingSystem @Inject constructor(
    private val gameRepository: GameRepository,
    private val chronicleSystem: ChronicleSystem
) {
    enum class GameEnding {
        NONE, TOTAL_COLLAPSE, ECHO_DOMINION, FAITH_ASCENSION, RESTORATION, ABANDONED
    }

    fun checkEndingConditions(state: GameState): GameEnding {
        // Project Anchor: Endings can only trigger after the first day
        if (state.world.day <= 1 && state.world.globalStability == 100) return GameEnding.NONE

        return when {
            // FIX (PRECISION): Use epsilon for float comparisons
            state.world.collapseProgress >= 0.999f -> GameEnding.TOTAL_COLLAPSE
            state.world.echoIntensity >= 0.999f -> GameEnding.ECHO_DOMINION
            state.party.all { it.isDead } -> GameEnding.ABANDONED
            state.world.globalStability >= 90 && state.prayer.faith >= 80 -> GameEnding.FAITH_ASCENSION
            state.world.globalStability >= 95 -> GameEnding.RESTORATION
            else -> GameEnding.NONE
        }
    }

    fun getEndingDescription(ending: GameEnding): String {
        return when (ending) {
            GameEnding.TOTAL_COLLAPSE -> "Rzeczywistość rozpadła się na Twoich oczach. Szyfr przestał istnieć, a wraz z nim wszystko, co znałeś."
            GameEnding.ECHO_DOMINION -> "Echo pochłonęło Boreas. Świat stał się szeptem w pustce, a Twoi bohaterowie - jedynie usterkami w nieskończonym cyklu."
            GameEnding.FAITH_ASCENSION -> "Wiara Twojej drużyny stała się nową Kotwicą. Bogowie uśmiechnęli się, a mrok został wyparty poza krawędź snu."
            GameEnding.RESTORATION -> "Stabilność została przywrócona. Paradygmat Boreas przetrwał, a pęknięcia zaczęły się goić."
            GameEnding.ABANDONED -> "Pustka pochłonęła ostatnie ślady Twojej obecności. Nikt nie opowie Twojej historii."
            else -> ""
        }
    }

    fun shouldTriggerMetaEnding(): Boolean {
        val s = gameRepository.currentState()
        val heroEndings = listOf("lore_aelion_ascension", "lore_mira_ascension", "lore_ferrun_iron_wall", "lore_noctyros_update")
        val resolvedCount = heroEndings.count { chronicleSystem.isUnlocked(it) }

        val observedReady = s.metaAwarenessLevel >= 4 && s.quest.completedQuestIds.contains("q_meta_7")
        
        return observedReady || s.world.globalStability < 10 || resolvedCount >= 4
    }

    fun resolveEnding(gameState: GameState): Ending {
        if (gameState.quest.completedQuestIds.contains("q_meta_7") && gameState.metaAwarenessLevel >= 4) {
            return Ending(
                EndingType.OBSERVED,
                "Margines Sesji",
                "To nie bohater był obserwowany. Skrybowie Absolutu od początku notowali ciebie — podmiot wyboru, czytelnika, sprawcę sesji. Bohater był tylko figurą zapisu."
            )
        }
        
        val faith = gameState.prayer.faith
        val virtue = gameState.prayer.virtue
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

    fun getAllHeroes(): List<Hero> = gameRepository.currentState().party.toList()

    fun getHeroEpilogue(hero: Hero): String {
        return when {
            hero.corruption >= 80 -> "${hero.name} stał się naczyniem dla mroku, błąkając się wiecznie po Drugiej Stronie."
            hero.sanity <= 20 -> "${hero.name} popadł w obłęd, widząc rzeczy, których śmiertelnik nie powinien znać."
            hero.virtue >= 40 -> "${hero.name} został zapamiętany jako święty obrońca krainy."
            else -> "${hero.name} wrócił do normalnego życia, lecz cienie przeszłości nigdy go nie opuściły."
        }
    }

    fun finaleStatus(): String {
        val s = gameRepository.currentState()
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
            
            Kronika zawiera ${chronicleSystem.getAll().size} kluczowych wpisów.
        """.trimIndent()
    }
}
