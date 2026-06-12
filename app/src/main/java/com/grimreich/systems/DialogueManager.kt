package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.grimreich.v1.DialogueNode
import com.grimreich.grimreich.v1.DialogueChoice
import kotlin.random.Random

object DialogueManager {
    private val nodes = mutableMapOf<String, DialogueNode>()

    fun registerNode(node: DialogueNode) {
        nodes[node.id] = node
    }

    fun getNode(id: String): DialogueNode? {
        val baseNode = nodes[id] ?: return null
        return applyWorldEffects(baseNode)
    }

    fun getPortrait(role: String): String {
        return when (role.lowercase()) {
            "aelion" -> "port_priest"
            "xyrel" -> "port_knight"
            "mira" -> "port_mage"
            "sereth" -> "port_wraith"
            "ferrun" -> "port_barbarian"
            "noctyros" -> "port_demon"
            "alchemik" -> "port_alchemist"
            "barbarzynca" -> "port_barbarian"
            "kaplan" -> "port_priest"
            "lowca" -> "port_ranger"
            "rycerz" -> "port_knight"
            "mag" -> "port_mage"
            "ork" -> "port_orc"
            "troll" -> "port_troll"
            "szkielet" -> "port_skeleton"
            "upior" -> "port_wraith"
            "demon" -> "port_demon"
            "smok" -> "port_dragon"
            "wilk" -> "port_wolf"
            "lotr" -> "port_rogue"
            else -> "port_rogue"
        }
    }

    private fun applyWorldEffects(node: DialogueNode): DialogueNode {
        val world = GameRepository.state.world
        val stability = world.globalStability
        
        if (stability >= 70) return node
        
        val fracturedText = if (stability < 30) {
            glitchText(node.text) + " ...GŁOSY... ABSOLUT... [NIE SŁUCHAJ ICH]"
        } else {
            node.text + " (czujesz pękanie tkanki świata)"
        }
        
        return node.copy(text = fracturedText)
    }

    private fun glitchText(text: String): String {
        return text.split(" ").map { word ->
            if (Random.nextFloat() < 0.2f) "[WYMAZANO]" else word
        }.joinToString(" ")
    }
    
    fun seedBasicDialogues() {
        if (nodes.isNotEmpty()) return

        // 1. AELION - PROROK MGŁY
        registerNode(DialogueNode(
            id = "aelion_start", npcId = "aelion",
            text = "Witaj w oparach Wybrzeża. Twoje oczy wciąż widzą formy, ale Twoja dusza... Twoja dusza zaczyna się rozmywać. Czy szukasz swoich utraconych dni?",
            choices = listOf(
                DialogueChoice("Pamiętam tylko zimną stal i zapach spalonej wsi.", "aelion_pain"),
                DialogueChoice("Chcę odzyskać wspomnienia o mojej rodzinie.", "aelion_recovery"),
                DialogueChoice("Czym dokładnie jest ta Mgła?", "aelion_lore")
            )
        ))
        registerNode(DialogueNode(id = "aelion_pain", npcId = "aelion", text = "Ból to jedyna rzecz, której Mgła nie może strawić. Jest Twoją kotwicą. Jeśli go odrzucisz, staniesz się tylko kolejnym cieniem bez twarzy."))
        registerNode(DialogueNode(id = "aelion_recovery", npcId = "aelion", text = "Rodzina? To tylko echa w Sferze Fenomenów. Prorocy dawno temu zamienili swoje więzi na wieczność w Pęknięciu. Ty zrobisz to samo."))
        registerNode(DialogueNode(id = "aelion_lore", npcId = "aelion", text = "Mgła to nie woda. To skroplona pamięć świata, który nigdy nie istniał. Absolut roni łzy, a my w nich toniemy."))

        // 2. XYREL - PROROK KRWI
        registerNode(DialogueNode(
            id = "xyrel_start", npcId = "xyrel",
            text = "Stoisz na ziemi, która pragnie Twojego życia. Słyszysz to? To bicie serca Równin. Każda kropla przelana tutaj karmi głód Absolutu.",
            choices = listOf(
                DialogueChoice("Moja broń jest gotowa na kolejną ofiarę.", "xyrel_battle"),
                DialogueChoice("Zatrzymaj to szaleństwo. Dość już krwi.", "xyrel_peace"),
                DialogueChoice("Czy ty też jesteś tylko ofiarą?", "xyrel_victim")
            )
        ))
        registerNode(DialogueNode(id = "xyrel_battle", npcId = "xyrel", text = "Wojownik! Twoja wola jest twarda jak stal Twierdzy Zakonu. Prowadź nas w głąb anomalii, aż nie zostanie nic prócz szkarłatu."))
        registerNode(DialogueNode(id = "xyrel_peace", npcId = "xyrel", text = "Pokój? Pokój to stagnacja. Tylko w agonii walki rzeczywistość staje się prawdziwa. Absolut nienawidzi ciszy."))

        // 3. MIRA - SĘDZIA ODBIĆ
        registerNode(DialogueNode(
            id = "mira_start", npcId = "mira",
            text = "Spójrz w jezioro, wędrowcze. Nie widzisz tam siebie, prawda? Widzisz to, kim Absolut chciałby, żebyś był. Która wersja Ciebie zwycięży?",
            choices = listOf(
                DialogueChoice("Jestem kowalem własnego losu.", "mira_truth"),
                DialogueChoice("Świat jest tylko odbiciem snu.", "mira_lie"),
                DialogueChoice("Jak mogę uciec z tej sali luster?", "mira_escape")
            )
        ))
        registerNode(DialogueNode(id = "mira_truth", npcId = "mira", text = "Kowal? Nawet młot, którym uderzasz, jest tylko cieniem idei. Twoja prawda pęka przy każdym dotyku."))

        // 4. SERETH - STRAŻNIK PEŁNI
        registerNode(DialogueNode(
            id = "sereth_start", npcId = "sereth",
            text = "Jasność Pełni wypala kłamstwa. W tych ruinach nie ma cieni, bo światło dochodzi z samej głębi Twoich oczu. Czy jesteś gotów oślepnąć od prawdy?",
            choices = listOf(
                DialogueChoice("Przyjmuję światło.", "sereth_light"),
                DialogueChoice("Mrok wewnątrz mnie jest silniejszy.", "sereth_dark"),
                DialogueChoice("Odejdź w blasku.", "end")
            )
        ))

        // 5. FERRUN - PROROK GŁĘBI
        registerNode(DialogueNode(
            id = "ferrun_start", npcId = "ferrun",
            text = "Góra nie wybacza. Metal nie czuje litości. Głębia woła tych, którzy mają dość powierzchniowej ułudy. Czy Twoje kości wytrzymają ten ciężar?",
            choices = listOf(
                DialogueChoice("Wykuję tu swój koniec.", "ferrun_forge"),
                DialogueChoice("Szukam artefaktu Ferruna.", "ferrun_quest")
            )
        ))

        // 6. NOCTYROS - PROROK PĘKNIĘCIA
        registerNode(DialogueNode(
            id = "noctyros_start", npcId = "noctyros",
            text = "Cień już Cię dotknął. Pęknięcie nie jest w ziemi, ono jest w Twojej duszy. Słyszysz głosy? To nie szaleństwo. To rozmowa z Absolutem.",
            choices = listOf(
                DialogueChoice("Słyszę... szum.", "noctyros_noise"),
                DialogueChoice("Rozkaż im przestać!", "noctyros_rage"),
                DialogueChoice("Czego one chcą?", "noctyros_will")
            )
        ))
        registerNode(DialogueNode(id = "noctyros_will", npcId = "noctyros", text = "Chcą, żebyś przestał stawiać opór. Upadek jest piękny, gdy przestajesz bać się uderzenia o dno rzeczywistości."))

        // NPC PROCEDURALNI - ROZBUDOWA
        registerNode(DialogueNode(
            id = "chronicler_deep", npcId = "procedural",
            text = "Moje pióro krwawi czarnym atramentem. Każde słowo, które piszę, znika z pamięci świata. Jesteśmy w ostatnim rozdziale, nie sądzisz?",
            choices = listOf(
                DialogueChoice("Zapisz moje czyny.", "chronicler_deeds"),
                DialogueChoice("Spal tę księgę.", "chronicler_burn")
            )
        ))
        
        registerNode(DialogueNode(
            id = "fugitive_horror", npcId = "procedural",
            text = "Widziałem jak kamień stał się okiem i mrugnął do mnie! Nie wrócę tam... Ziemie Dzikie... tam trawa krzyczy...",
            choices = listOf(
                DialogueChoice("Uspokój się.", "fugitive_calm"),
                DialogueChoice("Pokaż mi drogę.", "end")
            )
        ))
        
        registerNode(DialogueNode(
            id = "mystic_absolute", npcId = "procedural",
            text = "Absolut nie śpi. On czeka, aż wszyscy zamkniemy oczy, żeby mógł w końcu przestać udawać, że istniejemy.",
            choices = listOf(
                DialogueChoice("To nihilistyczne bzdury.", "end"),
                DialogueChoice("Co się stanie, gdy się obudzi?", "mystic_wake")
            )
        ))
        registerNode(DialogueNode(id = "mystic_wake", npcId = "procedural", text = "Wszystkie kolory zleją się w jeden. Wszystkie dźwięki w ciszę. Będziesz tam, ale nie będzie kogoś, kto mógłby to zauważyć."))
    }
}
