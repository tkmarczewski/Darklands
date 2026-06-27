package com.grimreich.systems

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuestManifest @Inject constructor(
    private val engine: QuestEngine
) {
    fun seed() {
        // --- I. INTRIGUES ---
        registerQuest("q_blood_icon", "Szept Krwawej Ikony", "Wioska czci posąg, który zaczyna… odpowiadać.", "wybrzeze_polnocne", "mystic", 100, StepType.COMBAT, "Opętany Posąg")
        registerQuest("q_lost_apostle", "Zaginiony Apostoł", "Kapłan Absolutu uciekł z zakazanym manuskryptem.", "rowniny_koronne", "zealot", 120, StepType.COMBAT, "Upadły Kapłan")
        registerQuest("q_three_masks", "Trzy Maski Zdrajcy", "Ktoś podszywa się pod bohatera.", "twierdza_zakonu", "guard", 150, StepType.COMBAT, "Oszust w Masce")
        registerQuest("q_altar_silence", "Krew na Ołtarzu Ciszy", "Klasztor milczenia ukrywa ofiary.", "serce_krainy", "mystic", 130, StepType.COMBAT, "Strażnik Ciszy")
        registerQuest("q_raven_oath", "Złamana Przysięga Kruka", "Bractwo zabójców żąda spłaty długu.", "poludniowe_ruiny", "merchant", 140, StepType.COMBAT, "Kruk Zabójca")

        // --- II. ANOMALIES ---
        registerQuest("q_remembering_mists", "Mgły, Które Pamiętają", "Mgła pokazuje sceny z przeszłości.", "wybrzeze_polnocne", "mystic", 120, StepType.INVESTIGATION, "mist_path")
        registerQuest("q_doorless_tower", "Wieża Bez Drzwi", "Wejście pojawia się tylko przy śmierci.", "rowniny_koronne", "mystic", 200, StepType.COMBAT, "Ectoplasmik")
        registerQuest("q_breathing_forest", "Las, Który Oddycha", "Drzewa zmieniają położenie.", "ziemie_dzikie", "mystic", 150, StepType.INVESTIGATION, "forest_exit")
        registerQuest("q_stone_haven_dusk", "Kamienna Przystań", "O zmierzchu pojawiają się statki-widma.", "pogranicze_stepowe", "merchant", 130, StepType.INVESTIGATION, "ghost_ship")
        registerQuest("q_mirror_lake", "Jezioro Lustrzane", "Odbicia pokazują inne wersje Ciebie.", "serce_krainy", "mystic", 170, StepType.COMBAT, "Sobowtór")

        // --- III. BEASTS ---
        registerQuest("q_shadowless_wolves", "Wilki Bez Cieni", "Wataha nie zostawia śladów.", "ziemie_dzikie", "guard", 110, StepType.COMBAT, "Wilk Pustki")
        registerQuest("q_triple_hunter", "Trójwersyjny Łowca", "Potwór istnieje w trzech wersjach.", "gory_poludniowe", "guard", 200, StepType.COMBAT, "Łowca-Paradoks")
        registerQuest("q_two_heart_ghoul", "Ghul z Dwoma Sercami", "Zabicie go raz nie wystarcza.", "poludniowe_ruiny", "guard", 130, StepType.COMBAT, "Ghul-Mutant")
        registerQuest("q_swamp_witch", "Szeptucha z Bagien", "Wiedźma żąda „czegoś żywego”.", "ziemie_dzikie", "mystic", 150, StepType.COMBAT, "Szeptucha")
        registerQuest("q_monastery_bloodsucker", "Krwiopijca z Klasztoru", "Mnisi ukrywają potwora.", "serce_krainy", "zealot", 140, StepType.COMBAT, "Wampirzy Mnich")

        // --- IV. VERDICT CHAIN ---
        engine.register(QuestDefinition(
            id = "q_verdict_1", title = "Gabinet bez śladów",
            description = "Zbadaj miejsce zbrodni wysokiego urzędnika.",
            rewardGold = 150, cityId = "twierdza_zakonu", originNpcId = "guard",
            steps = listOf(QuestStep("Przeszukaj gabinet urzędnika.", StepType.INVESTIGATION, "office_id"))
        ))
        
        // --- V. DRAMAS ---
        registerQuest("q_undying_mother", "Matka, Która Nie Umiera", "Kobieta żyje 200 lat i błaga o śmierć.", "wybrzeze_polnocne", "zealot", 100, StepType.DIALOGUE, "citizen")
        registerQuest("q_lost_squad", "Zaginiony Oddział", "Żołnierze wrócili, ale się nie postarzali.", "rowniny_koronne", "guard", 150, StepType.INVESTIGATION, "squad_secret")
        registerQuest("q_demon_merchant", "Kupiec Widzący Demony", "Kupiec twierdzi, że klienci mają demonicze cienie.", "twierdza_zakonu", "merchant", 130, StepType.DIALOGUE, "merchant")
        
        // --- VI. CIPHER FINALE ---
        registerQuest("q_scribe_encounter", "Konfrontacja ze Skrybą", "Fundamenty świata drżą. Skryba czeka w Sercu Krainy.", "serce_krainy", "mira", 500, StepType.COMBAT, "scribe_boss")
    }

    private fun registerQuest(id: String, title: String, desc: String, city: String, npc: String, gold: Int, type: StepType, target: String) {
        engine.register(QuestDefinition(
            id = id, title = title, description = desc, rewardGold = gold,
            cityId = city, originNpcId = npc,
            steps = listOf(QuestStep(desc, type, target))
        ))
    }
}
