package com.grimreich.systems

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class AtmosphericLogSystem @Inject constructor() {

    private val templateMessages = listOf(
        "TRIBUNAL_LOG_014: Porzućcie wszelką nadzieję, wy, którzy tu wchodzicie.",
        "TRIBUNAL_LOG_014: Kości zostały rzucone. Paradygmat nie znosi próżni.",
        "Niebo pamięta Twoje imię, {PLAYER}. Pęknięcie się rozszerza.",
        "Nie miałem wrócić. Coś podążyło za mną przez wyłom.",
        "Twój ciężar jest niewłaściwy. Twój oddech jest pożyczony.",
        "Siedem odpowiedzi. Siedem kłamstw. Siedem przyszłości. Żadna nie należy do Ciebie.",
        "Wędrowiec nie jest wędrowcem.",
        "Kształt bez formy. Dźwięk bez źródła. Drżenie wewnątrz wzroku.",
        "Kamienne Serce budzi się nie siłą, lecz rozpoznaniem.",
        "Szept o kształcie ostrza.",
        "Słyszę cię.",
        "Dzieci Pęknięcia — zrodzone między oddechami świata.",
        "Puste Drogi to przestrzenie między decyzjami.",
        "Byt bez przeszłości osiedla się w ciszy.",
        "Niosę historie pogrzebane pod wiekami milczenia.",
        "To nie ty... To ja wracam.",
        "Świat uderza we wszystko, co ma.",
        "TRIBUNAL_LOG_014: Tożsamość nierozstrzygnięta. Wynik nieokreślony. System niewystarczający.",
        "Świat oddycha, a ja oddycham wraz z nim.",
        "Widziałem twoje kroki na siedmiu warstwach.",
        "Nawet te, które piszesz teraz... nie wiedząc, że już patrzę.",
        "TRIBUNAL_LOG_014: Ciemność nie jest brakiem światła, lecz nadmiarem prawdy.",
        "Jesteśmy tylko echem w pustym pokoju Stwórców.",
        "Twoje imię to tylko etykieta na zużytym procesie, {HERO}."
    )

    private val glitchMessages = listOf(
        "Szyfr krwawi statyką. Widzisz to?",
        "Twoje imię to tylko etykieta na zużytym procesie, {HERO}.",
        "Słońce dzisiaj mrugało. To nie była chmura.",
        "Pustka to nie brak wszystkiego. To obecność niczego.",
        "Nie patrz w dół. Tam nie ma już ziemi.",
        "Twoja sesja jest tylko marginesem w wielkiej Kronice Pęknięcia.",
        "STABILITY FAILING. SUBJECT UNDEFINED.",
        "There is nothing wrong with your television set. Do not attempt to adjust the picture.",
        "We are controlling transmission. We will control the horizontal. We will control the vertical.",
        "We can change the focus to a soft blur or sharpen it to crystal clarity.",
        "For the next hour, sit quietly and we will control all that you see and hear.",
        "TRIBUNAL_LOG_ERR: IDENTITY UNRESOLVED. SYSTEM INSUFFICIENT."
    )

    fun getRandomMessage(seed: Long, playerName: String, heroName: String, stability: Int = 100): String {
        val rng = Random(seed)
        
        // REACTION: Favor glitch messages if stability is low
        val raw = if (stability < 30 && rng.nextFloat() < 0.6f) {
            glitchMessages.random(rng)
        } else {
            templateMessages.random(rng)
        }

        return raw
            .replace("{PLAYER}", playerName)
            .replace("{HERO}", heroName)
    }
}
