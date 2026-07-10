package com.grimreich.systems

import com.grimreich.core.GameRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AtmosphericDescriptionSystem @Inject constructor(
    private val gameRepository: GameRepository
) {
    fun getCityDescription(cityId: String): String {
        val state = gameRepository.currentState()
        val stability = state.world.globalStability
        val meta = state.metaAwarenessLevel

        return when (cityId) {
            "wybrzeze_polnocne" -> getCoastDescription(stability, meta)
            "twierdza_zelazna" -> getFortressDescription(stability, meta)
            "opactwo_ciszy" -> getAbbeyDescription(stability, meta)
            "serce_krainy" -> getCoreDescription(stability, meta)
            else -> "Czujesz chłód pustki otulający to miejsce."
        }
    }

    private fun getCoastDescription(s: Int, m: Int): String {
        return when {
            s < 30 -> "Mgła jest gęsta jak smoła, szepcząca imiona tych, którzy nigdy nie wrócili z morza."
            m > 2 -> "Widzisz, że krawędzie rzeczywistości w mgle drżą, jakbyś patrzył przez niedopracowany render."
            else -> "Słone bryzy niosą ze sobą zapach wilgoci i zapomnienia."
        }
    }

    private fun getFortressDescription(s: Int, m: Int): String {
        return when {
            s < 30 -> "Żelazne ściany zdają się zwężać, a każdy krok echem przypomina o Twojej śmiertelności."
            m > 2 -> "Zamiast żelaza, w niektórych miejscach ściany przypominają nieczytelny ciąg znaków."
            else -> "Potęga Twierdzy Żelaznej przytłacza, chroniąc przed echem, które czai się za horyzontem."
        }
    }

    private fun getAbbeyDescription(s: Int, m: Int): String {
        return when {
            s < 30 -> "Cisza tutaj jest tak ciężka, że niemal słyszysz własny puls odliczający czas do końca sesji."
            m > 2 -> "Mnisi zdają się nie ruszać – są jak statyczne obiekty czekające na kolejne instrukcje."
            else -> "Miejsce skupienia, gdzie każdy oddech jest rejestrowany przez wszechobecny Trybunał."
        }
    }

    private fun getCoreDescription(s: Int, m: Int): String {
        return when {
            s < 30 -> "Tutaj świat pęka. Widzisz pod sobą nie fundamenty, lecz absolutną, przerażającą nicość."
            m > 2 -> "Stoisz w samym centrum zapisu. Czujesz, jak Skrybowie Absolutni patrzą na Ciebie z każdej strony."
            else -> "Epicentrum. Tu zaczyna się i kończy każda Twoja decyzja."
        }
    }
}
