package com.grimreich.systems

/**
 * Data definitions for all manually crafted quests and chains in GrimReich.
 */
object QuestRegistry {

    data class QuestTemplate(
        val id: String,
        val title: String,
        val description: String,
        val category: String,
        val baseReward: Int,
        val objective: String,
        val preferredCityId: String? = null,
        val enemyStats: EnemyStats? = null
    )

    data class EnemyStats(
        val name: String,
        val hp: Int,
        val atk: Int,
        val def: Int
    )

    data class QuestChain(
        val id: String,
        val title: String,
        val stages: List<QuestTemplate>
    )

    val allTemplates = listOf(
        // I. Intrigues, Treasons, Sects
        QuestTemplate("q_blood_icon", "Szept Krwawej Ikony", "Wioska czci posąg, który zaczyna… odpowiadać.", "Intrigue", 100, "Zbadaj źródło głosu w wiosce.", enemyStats = EnemyStats("Opętany Posąg", 60, 12, 15)),
        QuestTemplate("q_lost_apostle", "Zaginiony Apostoł", "Jeden z kapłanów Absolutu uciekł z zakazanym manuskryptem.", "Intrigue", 120, "Odnajdź kapłana i manuskrypt.", enemyStats = EnemyStats("Upadły Kapłan", 45, 14, 5)),
        QuestTemplate("q_three_masks", "Trzy Maski Zdrajcy", "Ktoś podszywa się pod bohatera, popełniając zbrodnie w jego imieniu.", "Intrigue", 150, "Oczyść swoje imię i powstrzymaj oszusta.", enemyStats = EnemyStats("Oszust w Masce", 55, 16, 8)),
        QuestTemplate("q_altar_silence", "Krew na Ołtarzu Ciszy", "Klasztor milczenia ukrywa ofiary w podziemiach.", "Intrigue", 130, "Zbadaj podziemia klasztoru.", enemyStats = EnemyStats("Strażnik Ciszy", 50, 10, 10)),
        QuestTemplate("q_raven_oath", "Złamana Przysięga Kruka", "Bractwo zabójców żąda spłaty dawnego długu.", "Intrigue", 140, "Rozwiąż sprawę długu z bractwem.", enemyStats = EnemyStats("Kruk Zabójca", 40, 20, 4)),
        QuestTemplate("q_ritualist_seal", "Ostatnia Pieczęć Rytualisty", "Rytualista twierdzi, że bohater jest „ostatnim elementem”.", "Intrigue", 160, "Przetrwaj rytuał lub go powstrzymaj.", enemyStats = EnemyStats("Arcy-Rytualista", 70, 15, 12)),
        QuestTemplate("q_house_shadows", "Cienie w Domu Burgrabiego", "Burgrabia nie śpi od tygodni, bo „ktoś chodzi po suficie”.", "Intrigue", 110, "Spędź noc w domu burgrabiego.", enemyStats = EnemyStats("Pełzacz Sufitowy", 40, 12, 6)),
        QuestTemplate("q_golden_ruins_betrayal", "Zdrada w Złotych Ruinach", "Ekspedycja archeologiczna zaczyna znikać po kolei.", "Intrigue", 150, "Znajdź ocalałych w ruinach.", enemyStats = EnemyStats("Upiorny Strażnik", 65, 14, 18)),
        QuestTemplate("q_not_your_voice", "Głos, Który Nie Jest Twój", "Bohater słyszy własny głos, który daje mu rozkazy.", "Intrigue", 170, "Odkryj źródło mentalnej manipulacji.", enemyStats = EnemyStats("Odbicie Umysłu", 50, 18, 5)),
        QuestTemplate("q_book_writes_itself", "Księga, Która Pisze Się Sama", "Księga zapisuje przyszłe zbrodnie, zanim się wydarzą.", "Intrigue", 180, "Zapobiegnij zbrodni opisanej w księdze.", enemyStats = EnemyStats("Atramentowa Zmora", 55, 12, 10)),

        // II. Cursed Locations, Anomalies
        QuestTemplate("q_remembering_mists", "Mgły, Które Pamiętają", "Mgła pokazuje sceny z przeszłości, ale nie wszystkie są prawdziwe.", "Anomaly", 120, "Przejdź przez mgłę bez popadnięcia w obłęd.", preferredCityId = "wybrzeze_polnocne"),
        QuestTemplate("q_doorless_tower", "Wieża Bez Drzwi", "Wejście pojawia się tylko wtedy, gdy ktoś umrze w pobliżu.", "Anomaly", 200, "Odkryj tajemnicę wieży.", enemyStats = EnemyStats("Ectoplasmik", 45, 15, 0)),
        QuestTemplate("q_breathing_forest", "Las, Który Oddycha", "Drzewa zmieniają położenie, zamykając bohatera w labiryncie.", "Anomaly", 150, "Znajdź drogę wyjścia z lasu.", preferredCityId = "ziemie_dzikie"),
        QuestTemplate("q_stone_haven_dusk", "Kamienna Przystań o Zmierzchu", "O zmierzchu pojawiają się statki, których nie ma za dnia.", "Anomaly", 130, "Wejdź na pokład widmowego okrętu.", preferredCityId = "pogranicze_stepowe"),
        QuestTemplate("q_backwards_clock", "Zegar, Który Odmierza Wstecz", "Czas w wiosce płynie odwrotnie.", "Anomaly", 140, "Powstrzymaj regresję czasu."),
        QuestTemplate("q_black_maw_wakes", "Czarna Paszcza Budzi Się", "Ruiny zaczynają „oddychać”, jakby coś rosło pod ziemią.", "Anomaly", 180, "Zbadaj serce ruin.", preferredCityId = "pogranicze_stepowe"),
        QuestTemplate("q_bridge_over_nothing", "Most Nad Niczym", "Most prowadzi do miejsca, które nie istnieje na mapach.", "Anomaly", 160, "Przekrocz most do Innej Strony."),
        QuestTemplate("q_vanishing_temple", "Świątynia, Która Znika", "Świątynia pojawia się tylko podczas burzy.", "Anomaly", 190, "Odszukaj świątynię w oku cyklonu."),
        QuestTemplate("q_mirror_lake", "Jezioro Lustrzane", "Odbicia w wodzie pokazują alternatywne wersje bohatera.", "Anomaly", 170, "Pokonaj swoje alternatywne ja.", enemyStats = EnemyStats("Sobowtór", 80, 15, 15)),
        QuestTemplate("q_whispering_mine", "Kopalnia, Która Szepcze", "Górnicy słyszą głosy, które każą im kopać głębiej.", "Anomaly", 150, "Wycisz głosy w głębi kopalni.", preferredCityId = "gory_poludniowe"),

        // III. Beasts, Monsters, Mutations
        QuestTemplate("q_shadowless_wolves", "Wilki Bez Cieni", "Wataha wilków nie rzuca cienia i nie zostawia śladów.", "Beast", 110, "Upoluj widmową watahę.", enemyStats = EnemyStats("Wilk Pustki", 40, 18, 5)),
        QuestTemplate("q_triple_hunter", "Trójwersyjny Łowca", "Potwór istnieje w trzech wersjach naraz.", "Beast", 200, "Zabij łowcę w trzech wymiarach.", enemyStats = EnemyStats("Łowca-Paradoks", 90, 16, 12)),
        QuestTemplate("q_two_heart_ghoul", "Ghul z Dwoma Sercami", "Zabicie go raz nie wystarcza.", "Beast", 130, "Zniszcz oba serca ghula.", enemyStats = EnemyStats("Ghul-Mutant", 80, 12, 8)),
        QuestTemplate("q_swamp_witch", "Szeptucha z Bagien", "Wiedźma oferuje pomoc, ale żąda „czegoś żywego”.", "Beast", 150, "Złóż ofiarę lub pokonaj wiedźmę.", enemyStats = EnemyStats("Szeptucha", 55, 16, 10)),
        QuestTemplate("q_monastery_bloodsucker", "Krwiopijca z Klasztoru", "Mnisi ukrywają potwora, który był kiedyś jednym z nich.", "Beast", 140, "Rozwiąż problem krwiopijcy.", enemyStats = EnemyStats("Wampirzy Mnich", 75, 18, 15)),
        QuestTemplate("q_golden_colossus", "Złoty Kolos", "Gigantyczna statua zaczyna się poruszać.", "Beast", 250, "Powstrzymaj kolosa przed zniszczeniem miasta.", enemyStats = EnemyStats("Złoty Kolos", 150, 20, 30)),
        QuestTemplate("q_mist_child", "Dziecko Mgły", "Dziecko pojawia się w wioskach, a potem wszyscy chorują.", "Beast", 160, "Znajdź źródło zarazy mgły."),
        QuestTemplate("q_winged_hulk", "Skrzydlaty Kadłub", "Latająca abominacja złożona z ciał kilku ofiar.", "Beast", 180, "Zestrzel abominację.", enemyStats = EnemyStats("Skrzydlaty Kadłub", 70, 22, 5)),
        QuestTemplate("q_talking_dog", "Pies, Który Mówi", "Pies twierdzi, że zna przyszłość bohatera.", "Beast", 100, "Podążaj za psem do prawdy."),
        QuestTemplate("q_four_eyed_rider", "Czterooki Jeździec", "Jeździec zwiastuje katastrofy, ale można go przekupić.", "Beast", 140, "Zatrzymaj zwiastuna lub zapłać mu.", enemyStats = EnemyStats("Mroczny Jeździec", 100, 25, 20)),

        // IV. People, Dramas, Moral Choices
        QuestTemplate("q_undying_mother", "Matka, Która Nie Umiera", "Kobieta żyje już 200 lat i błaga o śmierć.", "Drama", 100, "Znajdź sposób na przerwanie wiecznego życia."),
        QuestTemplate("q_two_soul_child", "Dziecko z Dwoma Duszami", "Dwie osobowości walczą o kontrolę.", "Drama", 120, "Ustabilizuj duszę dziecka."),
        QuestTemplate("q_lost_squad", "Zaginiony Oddział", "Oddział żołnierzy wraca po latach, ale… nie postarzał się.", "Drama", 150, "Odkryj tajemnicę wiecznej młodości oddziału."),
        QuestTemplate("q_demon_merchant", "Kupiec, Który Widzi Demony", "Kupiec twierdzi, że każdy klient ma „demoniczny cień”.", "Drama", 130, "Zbadaj wizje kupca."),
        QuestTemplate("q_returned_brother", "Brat, Który Powrócił", "Brat bohatera wraca z martwych, ale nie pamięta rodziny.", "Drama", 140, "Pomóż bratu odzyskać pamięć lub spokój."),
        QuestTemplate("q_village_judgment", "Sąd Ostateczny Wioski", "Mieszkańcy chcą wykonać egzekucję na niewinnej osobie.", "Drama", 110, "Wydaj sprawiedliwy wyrok."),
        QuestTemplate("q_dream_thief", "Złodziej Snów", "Ktoś kradnie sny mieszkańców, doprowadzając ich do obłędu.", "Drama", 160, "Odzyskaj sny ze świata snów."),
        QuestTemplate("q_faceless_wanderer", "Wędrowiec Bez Twarzy", "Człowiek bez twarzy prosi o pomoc w odzyskaniu „swojego ja”.", "Drama", 130, "Odnajdź twarz wędrowca."),
        QuestTemplate("q_blood_dowry", "Dziewczyna z Krwawym Wianem", "Panna młoda uciekła sprzed ołtarza, bo her narzeczony „nie jest człowiekiem”.", "Drama", 140, "Zdemaskuj narzeczonego."),
        QuestTemplate("q_jester_laugh", "Ostatni Śmiech Błazna", "Błazen zna sekrety całego dworu i chce je sprzedać.", "Drama", 120, "Zdobądź sekrety błazna.")
    )

    val bloodChain = QuestChain(
        id = "chain_blood",
        title = "Krew, Która Nie Chce Zaschnąć",
        stages = listOf(
            QuestTemplate("q_blood_1", "Krzyk z Piwnicy", "Wioska prosi o pomoc - z pustego domu słychać krzyki.", "Chain", 100, "Zbadaj piwnicę starego domu.", enemyStats = EnemyStats("Krwawa Mara", 40, 10, 5)),
            QuestTemplate("q_blood_2", "Dziewczyna, Która Widziała Twoją Twarz", "Jedyna świadek twierdzi, że widziała Cię w piwnicy.", "Chain", 110, "Porozmawiaj z dziewczyną o zniekształconych wspomnieniach."),
            QuestTemplate("q_blood_3", "Krew na Progu Świątyni", "Krew z piwnicy należy do ofiary sprzed 50 lat.", "Chain", 120, "Skonsultuj się z Kapłanami Absolutu."),
            QuestTemplate("q_blood_4", "Rytualista Bez Imienia", "Spotykasz człowieka, który twierdzi: Twoja krew pamięta więcej.", "Chain", 130, "Złóż ofiarę ze wspomnienia w zamian za prawdę."),
            QuestTemplate("q_blood_5", "Wspomnienie, Którego Nie Było", "Zaczynasz widzieć siebie w rytuale sprzed dekad.", "Chain", 140, "Rozszyfruj wizje swojej przeszłości.", enemyStats = EnemyStats("Cień Przeszłości", 60, 15, 10)),
            QuestTemplate("q_blood_6", "Trzej, Którzy Przeżyli", "Znajdź ostatnich świadków rytuału sprzed lat.", "Chain", 150, "Wysłuchaj sprzecznych wersji świadków."),
            QuestTemplate("q_blood_7", "Krew, Która Wraca", "Twoje rany nie chcą się goić. Klątwa działa.", "Chain", 160, "Odnajdź Miejsce Pierwszego Rozcięcia."),
            QuestTemplate("q_blood_8", "Ołtarz, Który Pamięta", "Finał w podziemiach. Ołtarz czeka na uczestnika.", "Chain", 300, "Dokonaj ostatecznego wyboru.", enemyStats = EnemyStats("Klątwa Krwi", 120, 25, 20))
        )
    )
}
