package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.grimreich.v1.ChronicleEntry
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChronicleSystem @Inject constructor(
    private val gameRepository: GameRepository
) {
    private val allEntries = mutableMapOf<String, ChronicleEntry>()

    init {
        seed()
    }

    private fun seed() {
        register(ChronicleEntry(
            id = "lore_fracture_origin",
            title = "Początek Pęknięcia",
            category = "Era of Fracture",
            fullText = """
                Pęknięcie nie było wydarzeniem nagłym. Zaczęło się od drobnych anomalii – cienie poruszały się o ułamek sekundy za późno, a lustra pokazywały obrazy z przyszłości, która nigdy nie nadeszła. 
                W roku 1244 Era Materii dobiegła końca, gdy niebo nad Sercem Krainy dosłownie rozdarło się, ukazując błękitny blask czystego kodu rzeczywistości. 
                Świat, który znaliśmy, okazał się być tylko jedną z warstw niedokończonego projektu.
            """.trimIndent()
        ))

        register(ChronicleEntry(
            id = "lore_scribes",
            title = "O Skrybach Absolutu",
            category = "Ontology",
            fullText = """
                Kim są Skrybowie? Niektórzy mistycy wierzą, że to byty z zewnętrznego wymiaru, które traktują naszą rzeczywistość jako brudnopis. 
                Każdy nasz wybór, każdy krok, jest zapisywany w 'Zwoju Stanu' (SessionState). 
                Anomalie powstają tam, gdzie Skryba popełni błąd lub próbuje poprawić fragment tekstu, nie dbając o logiczną spójność całości. 
                Pęknięcie to po prostu miejsce, gdzie atrament jeszcze nie wysechł.
            """.trimIndent()
        ))

        register(ChronicleEntry(
            id = "lore_verdict_origin",
            title = "Geneza Wyroku",
            category = "Factions",
            fullText = """
                Inkwizycja nie zawsze była tak surowa. Powstała z resztek dawnej armii cesarskiej, by chronić ludzi przed mutantami z Drugiej Strony. 
                Jednak z czasem ich liderzy zaczęli wierzyć, że jedynym sposobem na powstrzymanie Pęknięcia jest eliminacja wszystkiego, co 'nielogiczne'. 
                Wyrok stał się narzędziem segregacji – jeśli nie pasujesz do paradygmatu, zostajesz usunięty z pamięci świata.
            """.trimIndent()
        ))

        register(ChronicleEntry(
            id = "lore_aelion_secret",
            title = "Tajemnica Proroka Aeliona",
            category = "Saints",
            fullText = """
                Prorok Aelion nie jest człowiekiem. Ci, którzy widzieli go bez szat, twierdzą, że jego ciało składa się z tysięcy drobnych, lśniących znaków. 
                Jest on żywym Fragmentem Prawdy, który uciekł ze Zwoju i przybrał formę śmiertelnika, by ostrzec nas przed Epilogiem. 
                Jego obecność stabilizuje Wybrzeże, ale jednocześnie przyciąga uwagę Skrybów, którzy chcą go 'wyczyścić'.
            """.trimIndent()
        ))
    }

    fun register(entry: ChronicleEntry) {
        allEntries[entry.id] = entry
    }

    fun unlock(id: String) {
        val state = gameRepository.currentState()
        if (!state.unlockedLoreIds.contains(id)) {
            state.unlockedLoreIds.add(id)
            gameRepository.log("Odblokowano nowy wpis w Kronice: ${allEntries[id]?.title}")
            gameRepository.persistCurrentState()
        }
    }

    fun getUnlockedEntries(): List<ChronicleEntry> {
        val unlockedIds = gameRepository.currentState().unlockedLoreIds
        return allEntries.values.filter { unlockedIds.contains(it.id) }
    }

    fun isUnlocked(id: String): Boolean = gameRepository.currentState().unlockedLoreIds.contains(id)
}
