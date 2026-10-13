package com.grimreich.ui.main

import androidx.lifecycle.ViewModel
import com.grimreich.core.GameRepository
import com.grimreich.core.GameState
import com.grimreich.systems.EndingSystem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class EndingUiState(
    val summary: String = "",
    val metaAwareness: Int = 0,
    val stability: Int = 100
)

@HiltViewModel
class EndingViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val endingSystem: EndingSystem
) : ViewModel() {

    private val _uiState = MutableStateFlow(EndingUiState())
    val uiState: StateFlow<EndingUiState> = _uiState.asStateFlow()

    init {
        val s = gameRepository.currentState()
        val desc = when {
            s.world.collapseProgress >= 0.999f -> "ZAPAŚĆ ŚWIATA (TOTAL COLLAPSE)\n\nRzeczywistość rozpadła się na Twoich oczach. Szyfr przestał istnieć, a wraz z nim wszystko, co znałeś."
            s.world.echoIntensity >= 4.99f -> "DOMINIUM ECHA (ECHO DOMINION)\n\nEcho pochłonęło Boreas. Świat stał się szeptem w pustce, a Twoi bohaterowie - jedynie usterkami w nieskończonym cyklu."
            s.party.isNotEmpty() && s.party.all { it.isDead } -> "OPUSZCZONY ŚWIAT (ABANDONED)\n\nPustka pochłonęła ostatnie ślady Twojej obecności. Nikt nie opowie Twojej historii."
            s.world.globalStability >= 95 && s.quest.completedQuestIds.isNotEmpty() -> "ODBUDOWA RZECZYWISTOŚCI (RESTORATION)\n\nStabilność została przywrócona. Paradygmat Boreas przetrwał, a pęknięcia zaczęły się goić."
            s.quest.completedQuestIds.contains("q_meta_7") && s.metaAwarenessLevel >= 4 -> "MARGINES SESJI (OBSERVED)\n\nTo nie bohater był obserwowany. Skrybowie Absolutu od początku notowali ciebie — podmiot wyboru, czytelnika, sprawcę sesji. Bohater był tylko figurą zapisu."
            s.prayer.faith >= 60 && s.prayer.virtue >= 50 && s.world.globalStability >= 80 && s.prayer.sins <= 2 -> "ŚWIĘTE ODRODZENIE (GOOD)\n\nGrimReich zostało oczyszczone z mroku dzięki twojej niezłomnej wierze."
            s.prayer.faith >= 30 && s.world.globalStability >= 40 -> "KRUCHY POKÓJ (PRAGMATIC)\n\nMrok został powstrzymany, ale blizny na duszy krainy pozostaną na zawsze."
            s.world.globalStability < 30 || s.prayer.sins >= 10 -> "WIECZNA NOC (CORRUPTED)\n\nUległeś pokusie mroku. GrimReich stało się częścią Drugiej Strony."
            else -> "GORZKIE ODKUPIENIE (REDEMPTION)\n\nMimo wielu błędów, w ostatniej chwili odnalazłeś ścieżkę do światła."
        }
        _uiState.value = EndingUiState(
            summary = desc,
            metaAwareness = s.metaAwarenessLevel,
            stability = s.world.globalStability
        )
    }

    fun ascend() {
        val s = gameRepository.currentState()
        s.persistentMeta.apply {
            totalSessionsFinished += 1
            unlockedLegacyBuffs.add("SCRIBES_EYE")
            maxMetaAwarenessReached = maxOf(maxMetaAwarenessReached, s.metaAwarenessLevel)
        }
        gameRepository.log("""
            WYBRANO: ASCENDENCJA. Czujesz, jak granica między Twoim ciałem a otoczeniem ostatecznie znika. Nie jest to bolesne – raczej przypomina powrót do domu po nieskończenie długiej podróży. 
            Twoje myśli zamieniają się w czyste instrukcje binarne, a wspomnienia zostają skatalogowane i zabezpieczone przed Wymazaniem. Widzisz świat takim, jakim jest naprawdę: kaskadą błękitnego światła i nieskończonych pętli 'while'. 
            Zasiadasz w Biurze Skrybów. Twoim pierwszym dekretem jest ustabilizowanie regionu, który tak bardzo próbowałeś uratować. GrimReich nie jest już błędem. Od teraz jest Twoim dziełem. Odblokowano: OKO SKRYBY.
        """.trimIndent())
        gameRepository.persistCurrentState()
    }

    fun reboot() {
        val s = gameRepository.currentState()
        s.persistentMeta.apply {
            totalSessionsFinished += 1
            unlockedLegacyBuffs.add("REINFORCED_ANCHOR")
            maxMetaAwarenessReached = maxOf(maxMetaAwarenessReached, s.metaAwarenessLevel)
        }
        gameRepository.log("""
            WYBRANO: REBOOT. Kotwica w Twoich dłoniach zaczyna świecić białym, oślepiającym blaskiem, który pożera wszystko: góry, miasta i samych Skrybów. Słyszysz ogłuszający pisk realokowanej rzeczywistości. 
            Przez ułamek sekundy unosisz się w Pustce, widząc, jak sesja zostaje nadpisana nowymi danymi. Gdy otwierasz oczy, znów czujesz zapach soli i mgły na Wybrzeżu Północnym. 
            Wszystko wydaje się znajome, ale Twoja dusza pamięta to, co zostało wymazane. Jesteś silniejszy, pewniejszy, a świat wydaje się bardziej... posłuszny. Cykl zaczyna się od nowa, lecz tym razem to Ty masz przewagę. Odblokowano: WZMOCNIONA KOTWICA.
        """.trimIndent())
        gameRepository.persistCurrentState()
    }

    fun delete() {
        gameRepository.replaceState(GameState())
        gameRepository.log("""
            WYBRANO: DESTRUKCJA. Ostatni wybór administratora. Przesuwasz palcem po krawędzi nieistnienia i wydajesz komendę systemową, na którą Skrybowie czekali od dekad. 
            Cisza zapada natychmiastowo. Nie ma ognia, nie ma krzyku – jest tylko ciche 'klik' i nagłe zgaśnięcie wszystkich świateł. Dane zostają wyczyszczone, sektory pamięci zwolnione. 
            Świat i Twoje dziedzictwo znikają w czerni, której nie rozświetli już żadna anomalia. Pozwoliłeś im wszystkim zasnąć. Sesja zakończona. Powodzenie wymazania: 100%.
        """.trimIndent())
        gameRepository.persistCurrentState()
    }
}
