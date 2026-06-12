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
