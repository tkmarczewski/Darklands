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
            "anomalia" -> "port_dragon"
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
            glitchText(node.text) + " ...GŁOSY... ABSOLUT... [NIE SŁUCHAJ ICH] ...CISZA..."
        } else {
            node.text + " (rzeczywistość wokół ciebie zaczyna tracić nasycenie)"
        }
        
        return node.copy(text = fracturedText)
    }

    private fun glitchText(text: String): String {
        return text.split(" ").map { word ->
            if (Random.nextFloat() < 0.25f) "[BŁĄD_ONTOLOGICZNY]" else word
        }.joinToString(" ")
    }
    
    fun seedBasicDialogues() {
        if (nodes.isNotEmpty()) return

        seedChapter1North()
        seedChapter2East()
        seedChapter3Central()
        seedChapter4South()
        seedChapter5FarSouth()
        seedChapter6West()
        seedChapter7Untamed()
        seedMetaLore()
        seedExtendedNpcDialogues()
        seedV21ExtraDialogues()
    }

    private fun seedV21ExtraDialogues() {
        // SEER
        registerNode(DialogueNode(
            id = "seer_start", npcId = "procedural",
            text = "Gwiazdy zgasły, ale ich poświata wciąż nas okłamuje. Widzę Twoją nić... jest zapętlona. Ile razy już tu byłeś, Kotwico?",
            choices = listOf(
                DialogueChoice("To mój pierwszy raz tutaj.", "seer_first"),
                DialogueChoice("Czuję, że znam tę ścieżkę.", "seer_familiar")
            )
        ))
        registerNode(DialogueNode(id = "seer_familiar", npcId = "procedural", text = "Dejà vu to tylko usterka w Sferze Fenomenów. Absolut zapomniał wymazać Twoje poprzednie kroki."))

        // SOLDIER
        registerNode(DialogueNode(
            id = "soldier_start", npcId = "procedural",
            text = "Trzymaj gardę wysoko. We mgle nie walczysz z ludźmi, tylko z tym, co po nich zostało. Masz dość odwagi, by przelać krew?",
            choices = listOf(
                DialogueChoice("Moja stal nie zna strachu.", "end"),
                DialogueChoice("Dla kogo walczysz?", "soldier_faction")
            )
        ))

        // HERETIC
        registerNode(DialogueNode(
            id = "heretic_start", npcId = "procedural",
            text = "Prorocy to tylko pasożyty na ranie świata! Prawdziwa wolność jest w Pęknięciu, tam gdzie nikt nie narzuca nam formy.",
            choices = listOf(
                DialogueChoice("Twoje słowa to szaleństwo.", "end"),
                DialogueChoice("Opowiedz mi o wolności Pęknięcia.", "heretic_freedom")
            )
        ))

        // BEGGAR
        registerNode(DialogueNode(
            id = "beggar_start", npcId = "procedural",
            text = "Miedziaka dla nędzarza, który stracił wszystko... nawet własne imię. Sprzedałem je mgle za bochenek chleba, który okazał się kamieniem.",
            choices = listOf(
                DialogueChoice("Daj 5 złota (Gold-5)", "beggar_give", onSelect = { it.gold -= 5 }),
                DialogueChoice("Idź precz.", "end")
            )
        ))
        registerNode(DialogueNode(id = "beggar_give", npcId = "procedural", text = "Dziękuję... Niech Absolut mrugnie, gdy będziesz przechodził przez bramę."))
    }

    private fun seedExtendedNpcDialogues() {
        // GRAVEDIGGER
        registerNode(DialogueNode(
            id = "gravedigger_start", npcId = "procedural",
            text = "Kopię płytkie doły, bo ziemia wkrótce i tak ich wypluje. Czy szukasz kogoś, kto już odszedł?",
            choices = listOf(
                DialogueChoice("Szukam prawdy o tym świecie.", "gravedigger_truth"),
                DialogueChoice("Po prostu wykonuj swoją pracę.", "end")
            )
        ))
        registerNode(DialogueNode(id = "gravedigger_truth", npcId = "procedural", text = "Prawda jest taka, że pod tą ziemią nie ma robaków. Jest tylko czyste światło Absolutu, które czeka na naszą śmierć."))

        // INQUISITOR
        registerNode(DialogueNode(
            id = "inquisitor_start", npcId = "procedural",
            text = "Twoja obecność tutaj jest anomalią. Czy Twoja wola jest zgodna z dogmatem Proroka, czy niesiesz w sobie zarazę Pęknięcia?",
            choices = listOf(
                DialogueChoice("Jestem wierny Absolutowi.", "inquisitor_loyal"),
                DialogueChoice("Nie uznaję Waszych dogmatów.", "inquisitor_heresy")
            )
        ))

        // ORPHAN
        registerNode(DialogueNode(
            id = "orphan_start", npcId = "procedural",
            text = "Panie... dlaczego słońce ma oczy? Moja mama powiedziała, że to Prorok Sereth, ale ja widzę tam coś innego...",
            choices = listOf(
                DialogueChoice("Nie patrz w niebo, mały.", "end"),
                DialogueChoice("Co widzisz?", "orphan_vision")
            )
        ))
        registerNode(DialogueNode(id = "orphan_vision", npcId = "procedural", text = "Widzę... pusty pokój. Bez ścian. I słyszę jak ktoś płacze, bo nikt go nie kocha. Nawet Absolut."))

        // BLACKSMITH
        registerNode(DialogueNode(
            id = "blacksmith_start", npcId = "procedural",
            text = "Młot uderza, ale metal się nie poddaje. W tym regionie stal staje się miękka jak masło, gdy tylko pomyślisz o Mgle. Czego potrzebujesz?",
            choices = listOf(
                DialogueChoice("Napraw moją broń.", "blacksmith_repair"),
                DialogueChoice("Opowiedz mi o tutejszym kruszcu.", "blacksmith_lore")
            )
        ))

        // AMNESIAC
        registerNode(DialogueNode(
            id = "amnesiac_start", npcId = "procedural",
            text = "Kim... kim jesteś? Kim JA jestem? Pamiętam tylko biały pokój i głos, który kazał mi mrugać...",
            choices = listOf(
                DialogueChoice("Jesteś w GrimReich. Pamiętasz to?", "amnesiac_name"),
                DialogueChoice("Odejdź, nieszczęśniku.", "end")
            )
        ))
    }

    private fun seedChapter1North() {
        registerNode(DialogueNode(
            id = "aelion_start", npcId = "aelion",
            text = "Mgła nie jest pogodą, wędrowcze. To skroplona niepamięć Absolutu. Czy czujesz, jak Twoje dzieciństwo wycieka Ci przez palce w tym białym oparze?",
            choices = listOf(
                DialogueChoice("Pamiętam imię mojej matki. To wystarczy.", "aelion_memory_strength"),
                DialogueChoice("Wszystko co mam, to ta broń. Mgła jej nie zabierze.", "aelion_weapon"),
                DialogueChoice("Kim są pozostali Prorocy?", "aelion_others")
            )
        ))
        registerNode(DialogueNode(id = "aelion_memory_strength", npcId = "aelion", text = "Imiona są kotwicami. Ale kotwice rdzewieją w słonej wodzie Wybrzeża. Wkrótce zostaniesz tylko Ty... i pustka, którą nazwiesz wolnością."))
        registerNode(DialogueNode(id = "aelion_others", npcId = "aelion", text = "Jesteśmy siedmioma pęknięciami w jednym lustrze. Xyrel kocha krew, Mira kocha prawdę, a ja... ja kocham spokój, który nastaje, gdy wszystko zostaje zapomniane."))
    }

    private fun seedChapter2East() {
        registerNode(DialogueNode(
            id = "xyrel_start", npcId = "xyrel",
            text = "Krew Równin jest gęstsza od wina. Każda wojna tutaj jest tylko próbą udowodnienia Absolutowi, że wciąż potrafimy czuć ból. Jesteś tu, by zabijać czy by umrzeć?",
            choices = listOf(
                DialogueChoice("Jestem tu, by położyć kres temu cierpieniu.", "xyrel_end"),
                DialogueChoice("Szukam Sztandaru Rozpaczy.", "xyrel_artifact"),
                DialogueChoice("Twoja religia jest szaleństwem.", "xyrel_heresy")
            )
        ))
        registerNode(DialogueNode(id = "xyrel_artifact", npcId = "xyrel", text = "Sztandar? On nie wisi na maszcie. On jest wyszyty z nerwów tych, którzy odmówili walki. Chcesz go nieść? Przygotuj się na wieczny krzyk w uszach."))
    }

    private fun seedChapter3Central() {
        registerNode(DialogueNode(
            id = "mira_start", npcId = "mira",
            text = "W Sercu Krainy nie ma prywatności. Jeziora widzą Twoje grzechy, a lustra pokazują to, co zrobisz za dziesięć lat. Czy boisz się swojego odbicia?",
            choices = listOf(
                DialogueChoice("Moje sumienie jest czyste.", "mira_clean"),
                DialogueChoice("Widzę w lustrze kogoś innego... potwora.", "mira_monster"),
                DialogueChoice("Czym jest Sfera Fenomenów?", "mira_sphere")
            )
        ))
        registerNode(DialogueNode(id = "mira_sphere", npcId = "mira", text = "To miejsce, gdzie idee mają wagę, a materia jest tylko sugestią. Nasz świat jest tylko jej nieudanym szkicem. Absolut próbuje go teraz wymazać."))
    }

    private fun seedChapter4South() {
        registerNode(DialogueNode(
            id = "sereth_start", npcId = "sereth",
            text = "Światło Pełni nie daje ciepła, ono daje świadomość. W tych ruinach każdy kamień wie, że jest tylko senną marą. Czy jesteś gotów obudzić się z tego koszmaru?",
            choices = listOf(
                DialogueChoice("Sen jest lepszy od nicości.", "sereth_dream"),
                DialogueChoice("Chcę zobaczyć Absolut twarzą w twarz.", "sereth_face")
            )
        ))
    }

    private fun seedChapter5FarSouth() {
        registerNode(DialogueNode(
            id = "ferrun_start", npcId = "ferrun",
            text = "Głębia to jedyne miejsce, które nie pęka, bo jest już dnem. Stal Ferruna nie rdzewieje, bo została wykuta z czystego cierpienia. Chcesz zostać przekuty?",
            choices = listOf(
                DialogueChoice("Uczyń mnie twardszym od rzeczywistości.", "ferrun_harden"),
                DialogueChoice("Szukam wyjścia z tych kopalni.", "ferrun_exit")
            )
        ))
    }

    private fun seedChapter6West() {
        registerNode(DialogueNode(
            id = "noctyros_start", npcId = "noctyros",
            text = "Pęknięcie na Zachodzie to brama. Cień, który z niego wycieka, to powrót do pierwotnej jedni. Dlaczego tak kurczowo trzymasz się swojego 'ja'?",
            choices = listOf(
                DialogueChoice("Moja wola jest moją jedyną własnością.", "noctyros_will"),
                DialogueChoice("Słyszę głosy z Pęknięcia... wołają mnie.", "noctyros_voices")
            )
        ))
        registerNode(DialogueNode(id = "noctyros_voices", npcId = "noctyros", text = "To nie głosy. To harmonia. Kiedyś cały świat był jednym dźwiękiem, zanim fenomeny go nie rozbiły na miliardy fałszywych nut."))
    }

    private fun seedChapter7Untamed() {
        registerNode(DialogueNode(
            id = "anomaly_start", npcId = "anomalia",
            text = "Tu nie ma Proroka, bo tu nie ma już kogo okłamywać. Trawa rośnie wewnątrz Twoich płuc, a czas płynie w poprzek. Czy wciąż wierzysz, że jesteś człowiekiem?",
            choices = listOf(
                DialogueChoice("Jestem tym, który przetrwa.", "anomaly_survive"),
                DialogueChoice("Ziemie Dzikie... to piękny koniec.", "anomaly_beauty")
            )
        ))
    }

    private fun seedMetaLore() {
        // THE ABSOLUTE CLUES
        registerNode(DialogueNode(
            id = "absolute_echo", npcId = "procedural",
            text = "Widziałem to w moich wizjach. Absolut to nie byt. To pusty pokój, w którym ktoś zostawił zapaloną świecę. My jesteśmy tylko tańczącymi cieniami na ścianie.",
            choices = listOf(DialogueChoice("Kto zapalił świecę?", "absolute_candle"))
        ))
        registerNode(DialogueNode(id = "absolute_candle", npcId = "procedural", text = "Ty. Ja. My wszyscy. Przestaliśmy mrugać i teraz rzeczywistość wypala nam oczy."))

        // CHRONICLER SECRETS
        registerNode(DialogueNode(
            id = "chronicler_deep", npcId = "procedural",
            text = "Znalazłem stronę w mojej kronice, która została zapisana Twoim charakterem pisma... ale tysiąc lat temu. Jak to wyjaśnisz, Kotwico?",
            choices = listOf(
                DialogueChoice("To pętla czasu.", "chronicler_loop"),
                DialogueChoice("To kłamstwo Mgły.", "chronicler_lie")
            )
        ))
    }
}
