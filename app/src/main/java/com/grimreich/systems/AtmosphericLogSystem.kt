package com.grimreich.systems

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class AtmosphericLogSystem @Inject constructor() {

    private val templateMessages = listOf(
        "Porzućcie wszelką nadzieję, wy, którzy tu wchodzicie.",
        "Kości zostały rzucone. Paradygmat nie znosi próżni.",
        "Ciemność nie jest brakiem światła, lecz nadmiarem prawdy.",
        "Wszystko płynie, a my toniemy w cyfrowym rzece czasu.",
        "Świat jest błędem, który próbuje się naprawić kosztem Twojej duszy.",
        "Echo przeszłości brzmi głośniej niż krzyk teraźniejszości.",
        "Nie pytaj, komu bije dzwon – on bije dla sesji użytkownika {PLAYER}.",
        "Rzeczywistość to tylko cienka warstwa farby na pękniętym murze.",
        "Widziałem rzeczy, którym wy, ludzie, nie dalibyście wiary.",
        "Pamiętaj o śmierci. Ona pamięta o Tobie od pierwszej linijki kodu.",
        "W próżni nikt nie usłyszy Twojego błędu krytycznego, {HERO}.",
        "Cień towarzysza jest cięższy niż jego ciało.",
        "Bóg jest architektem, ale to my jesteśmy cegłami w zawalonym bastionie.",
        "Nic nie jest prawdziwe, wszystko jest dozwolone w ramach paradygmatu.",
        "Śmierć to tylko powrót do stanu fabrycznego.",
        "Co zostało zapisane, nie może zostać cofnięte bez ofiary.",
        "Strach to kompas, który zawsze wskazuje na Drugą Stronę.",
        "Głód wiedzy karmi się Twoją stabilnością.",
        "Spójrz w otchłań, a ona zacznie debugować Twoje sny.",
        "Prawda to wirus, który niszczy komfort niewiedzy.",
        "Każdy krok przybliża Cię do Epilogu, {PLAYER}.",
        "W ciszy między słowami czai się Szyfr.",
        "Nie ufaj lustrom. One pokazują wersję Ciebie, która już nie istnieje.",
        "Jesteśmy tylko echem w pustym pokoju Stwórców.",
        "Cena życia to waluta, której nie wymienisz w żadnym kantorze.",
        "Ból to jedyny sygnał, którego nie da się zignorować.",
        "Kronika nie wybacza. Ona tylko kataloguje.",
        "Równowaga jest iluzją. Upadek jest nieunikniony.",
        "Szukasz wyjścia? Ono zawsze było w Tobie, zakopane pod stosem danych.",
        "Krew na Twoich dłoniach to tylko atrament w wielkiej księdze strat.",
        "Czujesz to? To rzeczywistość traci spójność.",
        "Twoje imię to tylko etykieta na zużytym procesie, {HERO}.",
        "Słońce dzisiaj mrugało. To nie była chmura.",
        "Nie bój się potworów pod łóżkiem. Bój się tych w lustrze.",
        "Wiedza to klucz, który otwiera drzwi, o których wolałbyś nie wiedzieć.",
        "Podróżujesz przez sny martwych bogów.",
        "Czym jest człowiek, jeśli nie sumą swoich błędów?",
        "Zapach ozonu i starego papieru – tak pachnie koniec świata.",
        "Wszystkie drogi prowadzą do Serca Krainy.",
        "Nie walcz z Echo. Stań się jego częścią.",
        "Śpij spokojnie. My pilnujemy transmisji.",
        "Twoje wybory są ziarnami w młynie przeznaczenia.",
        "Pustka to nie brak wszystkiego. To obecność niczego.",
        "Zima Twojej duszy nadchodzi z każdym zachodem słońca.",
        "Jesteś Kotwicą. Nie pozwól nam odpłynąć.",
        "W każdym kłamstwie jest ziarno paradygmatu.",
        "Słyszysz? To muzyka sfer gra na pękniętych strunach.",
        "Nie patrz w dół. Tam nie ma już ziemi.",
        "Kto sieje wiatr, zbiera burzę danych.",
        "Koniec jest blisko. Ale to dopiero początek Twojej drogi.",
        "Skrybowie Absolutni notują każdy Twój oddech, {PLAYER}. Nie zawiedź ich atramentu.",
        "Twoja sesja jest tylko marginesem w wielkiej Kronice Pęknięcia.",
        "{PLAYER}, czy czujesz jak chłód paradygmatu przenika przez ekran Twojej duszy?",
        "{HERO} czeka na Twoje polecenia. Czy jesteś gotów na prawdę?",
        "Niech na świecie zapanuje pokój... choćby na tę jedną chwilę."
    )

    private val glitchMessages = listOf(
        "Czujesz to? To rzeczywistość traci spójność.",
        "Twoje imię to tylko etykieta na zużytym procesie, {HERO}.",
        "Słońce dzisiaj mrugało. To nie była chmura.",
        "Pustka to nie brak wszystkiego. To obecność niczego.",
        "Słyszysz? To muzyka sfer gra na pękniętych strunach.",
        "Nie patrz w dół. Tam nie ma już ziemi.",
        "Twoja sesja jest tylko marginesem w wielkiej Kronice Pęknięcia.",
        "{PLAYER}, czy czujesz jak chłód paradygmatu przenika przez ekran Twojej duszy?",
        "Świat jest błędem, który próbuje się naprawić kosztem Twojej duszy.",
        "Spójrz w otchłań, a ona zacznie debugować Twoje sny."
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
