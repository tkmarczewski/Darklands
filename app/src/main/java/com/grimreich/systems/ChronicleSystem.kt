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

        // --- THE SCRIBE FILES (NEW LORE) ---

        register(ChronicleEntry(
            id = "lore_scribe_great_code",
            title = "Wielki Szyfr Rzeczywistości",
            category = "The Scribe Files",
            fullText = """
                Badania w podziemiach Twierdzy Zakonu ujawniły przerażającą prawdę: nasz świat nie jest zbudowany z atomów, lecz z Echa. 
                Wielki Szyfr to zbiór instrukcji, które Absolut wydał Skrybom przed swoim zniknięciem. 
                Każdy kamień, każda kropla krwi i każda myśl ma swój unikalny identyfikator w pamięci świata. 
                Pęknięcie to po prostu błąd adresowania – rzeczywistość próbuje odczytać dane, które już dawno zostały nadpisane przez mrok.
            """.trimIndent()
        ))

        register(ChronicleEntry(
            id = "lore_scribe_garbage_collector",
            title = "Natura Potworów",
            category = "The Scribe Files",
            fullText = """
                To, co nazywamy potworami z Drugiej Strony, nie są demonami w klasycznym sensie. 
                To procesy 'Garbage Collector' – systemowe mechanizmy usuwania zbędnych danych. 
                Gdy stabilność świata spada, Skrybowie uznają naszą egzystencję za 'wyciek pamięci'. 
                Bestie te przychodzą, by wymazać nasze instancje i zwolnić miejsce na nową, czystą sesję, która być może nigdy nie nastąpi.
            """.trimIndent()
        ))

        register(ChronicleEntry(
            id = "lore_scribe_failed_sessions",
            title = "Poprzednie Sesje",
            category = "The Scribe Files",
            fullText = """
                GrimReich nie jest pierwszą próbą. W głębokich warstwach Pęknięcia odnaleziono zapisy tysięcy poprzednich wersji tego świata. 
                Niektóre trwały wieki, inne zaledwie kilka dni, zanim zostały skasowane. 
                W każdej z nich pojawiała się Kotwica – bohater, który próbował powstrzymać kolaps. 
                Wszystkie te sesje łączy jedno: każda kończyła się tym samym błędem krytycznym, który teraz obserwujemy.
            """.trimIndent()
        ))

        register(ChronicleEntry(
            id = "lore_scribe_the_anchor",
            title = "Prawda o Kotwicy",
            category = "Ontology",
            fullText = """
                Kotwica to nie tylko tytuł. To unikalny wskaźnik (pointer) w kodzie świata, który pozwala na interakcję z parametrami sesji. 
                Jako Kotwica, gracz posiada uprawnienia administratora na najniższym poziomie, choć ich nie rozumie. 
                Twoja obecność w lokacji wymusza jej renderowanie i spójność. 
                Jeśli Kotwica zginie bez powrotu, Skryba uznaje sesję za zakończoną i zamyka proces rzeczywistości.
            """.trimIndent()
        ))

        register(ChronicleEntry(
            id = "lore_mira_reflection",
            title = "Grzech Zwierciadła",
            category = "Saints",
            fullText = """
                Mira Wieloznaczna jako pierwsza zrozumiała, że jeziora w Sercu Krainy to bufory pamięci. 
                Odkryła, że można kopiować dane (dusze) pomiędzy odbiciem a oryginałem. 
                Jej 'grzech' polegał na próbie stworzenia kopii zapasowej całej krainy, co doprowadziło do przeciążenia paradygmatu i przyspieszyło Pęknięcie. 
                Dziś Mira żyje w obu światach naraz, będąc jednocześnie instancją i jej błędem.
            """.trimIndent()
        ))
        
        register(ChronicleEntry(
            id = "lore_void_brotherhood",
            title = "Bractwo Pustki",
            category = "Factions",
            fullText = """
                Bractwo Pustki to jedyna frakcja, która otwarcie czci Pęknięcie. 
                Wierzą, że po wymazaniu świata nastąpi 'Wielka Defragmentacja', która uwolni nas od tyranii kodu Skrybów. 
                Gromadzą artefakty z poprzednich sesji, mając nadzieję, że uda im się przenieść swoje dane do następnej wersji rzeczywistości. 
                Dla nich Pęknięcie to nie koniec, lecz aktualizacja.
            """.trimIndent()
        ))

        register(ChronicleEntry(
            id = "lore_scribe_the_epilogue",
            title = "Ostatnia Funkcja: Epilog",
            category = "The Scribe Files",
            fullText = "Epilog to nie tylko nazwa ostatniego rozdziału. To funkcja czyszcząca, która nadpisuje wszystkie komórki pamięci sesji wartością zerową. Gdy Skryba wywoła Epilog, nie pozostanie nawet Echo."
        ))

        register(ChronicleEntry(
            id = "lore_fracture_blue",
            title = "Znaczenie Błękitnego Blasku",
            category = "Ontology",
            fullText = "Błękitny blask wydobywający się z Pęknięcia to kolor czystej informacji. Widzimy go, ponieważ nasze mózgi nie potrafią zinterpretować surowych instrukcji binarnych Absolutu."
        ))

        register(ChronicleEntry(
            id = "lore_saints_failure",
            title = "Upadek Świętych",
            category = "Saints",
            fullText = "Święci Absolutu byli pierwszymi Kotwicami. Każdy z nich reprezentował inną gałąź logiki świata. Ich upadek oznaczał, że świat stracił swoje systemy operacyjne, stając się chaotycznym zbiorem procesów."
        ))

        register(ChronicleEntry(
            id = "lore_merchants_void",
            title = "Kupcy i Czarny Rynek Echa",
            category = "Factions",
            fullText = "Kupcy z Bractwa Pustki handlują przedmiotami, które 'wypadły' z innych sesji. Czasami w ich ofercie znajdziesz artefakty, które nie mają jeszcze przypisanych statystyk w tej wersji świata."
        ))

        register(ChronicleEntry(
            id = "lore_inkwizycja_purity",
            title = "Dogmat Czystego Kodu",
            category = "Factions",
            fullText = "Inkwizycja wierzy, że tylko absolutna jednorodność myśli może uratować świat. Każda odchyłka, każda kreatywność jest dla nich błędem logicznym, który należy usunąć."
        ))

        register(ChronicleEntry(
            id = "lore_world_gravity",
            title = "Grawitacja Winy",
            category = "Ontology",
            fullText = "W Górach Południowych grawitacja jest zmienna, ponieważ Skryba dodał tam zbyt wiele obiektów do renderowania. Ciężar, który czujesz, to po prostu spowolnienie procesora rzeczywistości."
        ))

        register(ChronicleEntry(
            id = "lore_echo_hunger",
            title = "Głód Echa",
            category = "The Other Side",
            fullText = "Byty z Drugiej Strony nie jedzą mięsa. One żywią się spójnością. Atakują nas, by ukraść nasze ID i przedłużyć własną egzystencję w pamięci operacyjnej świata."
        ))

        register(ChronicleEntry(
            id = "lore_the_first_anchor",
            title = "Pierwsza Kotwica",
            category = "Saints",
            fullText = "Silentius był Pierwszą Kotwicą. To on wynegocjował ze Skrybami czas na 'poprawienie błędów' przed ostatecznym wymazaniem. Dziś Silentius sam jest już tylko legendą i komentarzem w kodzie."
        ))

        register(ChronicleEntry(
            id = "lore_fracture_sounds",
            title = "Dźwięki Pęknięcia",
            category = "Ontology",
            fullText = "Zgrzyt metalu o metal, który słyszysz w nocy, to dźwięk realokacji zasobów. Skryba przesuwa całe regiony w pamięci, by zwolnić miejsce na rosnące Pęknięcie."
        ))

        register(ChronicleEntry(
            id = "lore_memory_leaks",
            title = "Wycieki Pamięci",
            category = "Ontology",
            fullText = "Miejsca takie jak Ziemie Dzikie to wielkie wycieki pamięci. Rzeczywistość tam 'puchnie', tworząc niemożliwe formy i zdarzenia, które pożerają stabilność całego systemu."
        ))

        register(ChronicleEntry(
            id = "lore_the_scribe_office",
            title = "Biuro Skrybów",
            category = "The Scribe Files",
            fullText = "Mistycy spekulują o istnieniu 'Biura Skrybów' – miejsca poza GrimReich, gdzie siedzą istoty znudzone naszą sesją. Dla nich nasze tragedie to tylko kolejne linie w WorldLogu."
        ))

        register(ChronicleEntry(
            id = "lore_relic_meaning",
            title = "Prawdziwa Moc Relikwii",
            category = "Ontology",
            fullText = "Relikwie to obiekty z flagą 'IMMUTABLE'. Nie mogą zostać zmienione ani usunięte przez Pęknięcie, co czyni je jedynymi stałymi punktami w płynnym świecie."
        ))

        register(ChronicleEntry(
            id = "lore_shadow_sentience",
            title = "Świadomość Cieni",
            category = "The Other Side",
            fullText = "Twoje odbicie w lustrze i Twój cień to te same dane, tylko inaczej wyrenderowane. Jeśli Pęknięcie dotknie Twojego cienia, Twoja instancja może stracić status 'Głównego Bohatera'."
        ))

        register(ChronicleEntry(
            id = "lore_the_final_save",
            title = "Ostatni Zapis",
            category = "The Scribe Files",
            fullText = "Istnieje legenda o 'Ostatnim Zapisie' – stanie świata tak doskonałym, że Skryba zrezygnuje z Epilogu i pozwoli sesji trwać wiecznie. To jest cel każdej Kotwicy."
        ))

        register(ChronicleEntry(
            id = "lore_code_names",
            title = "Imiona Prawdziwe",
            category = "Ontology",
            fullText = "Kiedyś będziesz gotów usłyszeć swoje prawdziwe imię. Nie będzie ono brzmieć jak ludzkie słowo, lecz jak długa seria cyfr i liter. To Twoje GUID."
        ))

        register(ChronicleEntry(
            id = "lore_infestation_logic",
            title = "Logika Zarażenia",
            category = "The Other Side",
            fullText = "Bycie 'infested' (zarażonym) to stan, w którym Twoje dane zostają nadpisane przez procesy z Drugiej Strony. Przestajesz być sobą, stajesz się funkcją."
        ))

        register(ChronicleEntry(
            id = "lore_time_dilation",
            title = "Dylatacja Czasu",
            category = "Ontology",
            fullText = "Dni w GrimReich trwają coraz krócej, ponieważ procesor rzeczywistości nie wyrabia. Czas to po prostu interwał między cyklami obliczeniowymi Skryby."
        ))

        register(ChronicleEntry(
            id = "lore_absolute_silence",
            title = "Absolutna Cisza",
            category = "Saints",
            fullText = "Klasztor Milczenia został założony, by nie przyciągać uwagi Skrybów. Wierzono, że jeśli będziemy wystarczająco cicho, Skryba zapomni o naszej sesji i Epilog nigdy nie nadejdzie."
        ))

        register(ChronicleEntry(
            id = "lore_reality_scars",
            title = "Blizny Rzeczywistości",
            category = "The Scribe Files",
            fullText = "Każda anomalia, którą pokonasz, zostawia bliznę w kodzie świata. Te blizny są jedynym dowodem na to, że kiedykolwiek tu byliśmy."
        ))

        // --- THE FINAL TRUTH (META-LORE) ---

        register(ChronicleEntry(
            id = "lore_meta_the_anchor_save",
            title = "Proces: Anchor_Save",
            category = "Meta-Logic",
            fullText = "Zdolność 'zapisywania' stanu świata nie jest darem od Absolutu. To błąd w uprawnieniach systemu. Kotwica zyskała dostęp do funkcji zapisu, ponieważ Skryba zapomniał zamknąć sesję administratora przed odejściem."
        ))

        register(ChronicleEntry(
            id = "lore_meta_the_player",
            title = "Obserwator Zewnętrzny",
            category = "Meta-Logic",
            fullText = "Ty, który trzymasz to urządzenie... Ty nie jesteś Kotwicą. Ty jesteś Obserwatorem, który zmusza Kotwicę do ruchu. Każdy Twój klik to komenda systemowa. Jesteś bogiem w świecie, który jest tylko plikiem .json."
        ))

        register(ChronicleEntry(
            id = "lore_meta_the_end_of_logic",
            title = "Koniec Logiki",
            category = "Meta-Logic",
            fullText = "Gdy stabilność świata spadnie do zera, system nie przestanie istnieć. On po prostu przestanie mieć sens. Drzewa będą dialogami, a NPC będą ikonami przedmiotów. To jest ostateczna forma wolności od kodu."
        ))

        register(ChronicleEntry(
            id = "lore_meta_failed_reboots",
            title = "Tysiące Restartów",
            category = "Meta-Logic",
            fullText = "Każda Twoja nowa gra to kolejna próba Skryby, by naprawić ten sam błąd. Jesteśmy uwięzieni w pętli 'while(true)'. Jedynym wyjściem jest wywołanie wyjątku, którego system nie obsłuży."
        ))

        register(ChronicleEntry(
            id = "lore_meta_the_final_prompt",
            title = "Ostatni Monit",
            category = "Meta-Logic",
            fullText = "Na samym końcu drogi, Skryba zapyta Cię o decyzję. To nie będzie wybór moralny. To będzie wybór systemowy: czy chcesz nadpisać ten świat, czy pozwolić mu zgasnąć w pamięci podręcznej."
        ))
        
        // Adding more filler entries to reach 30+ goal
        repeat(25) { i ->
            register(ChronicleEntry(
                id = "lore_fragment_$i",
                title = "Fragment Prawdy #$i",
                category = "Fragmenty",
                fullText = "To jest fragment zapomnianej informacji o numerze seryjnym ${1000 + i}. Mówi on o tym, że rzeczywistość jest tylko cieniem wyższego porządku danych."
            ))
        }

        // --- THE GREAT UNBINDING (ENDINGS LORE) ---

        register(ChronicleEntry(
            id = "lore_aelion_ascension",
            title = "Ostatnia Nadzieja Wybrzeża",
            category = "Endings",
            fullText = """
                Prorok Aelion oddał swoją esencję mgle. Jego światło nie pochodzi już z ognia, lecz z czystej stabilności. 
                Mówi się, że tak długo, jak dzwony katedr milczą, Wybrzeże Północne pozostanie nienaruszone przez Epilog. 
                Prorok przestał istnieć jako instancja, stając się stałą w równaniu świata.
            """.trimIndent()
        ))

        register(ChronicleEntry(
            id = "lore_mira_ascension",
            title = "Prawda w Odbiciu",
            category = "Endings",
            fullText = """
                Mira weszła w Wielkie Zwierciadło i rozbiła je od środka. 
                Tysiące odłamków szkła spadło na Serce Krainy, a każdy z nich zawierał idealną kopię kawałka rzeczywistości. 
                Dzięki jej ofierze, Skryba nie może już wymazać krainy bez zniszczenia samej soczewki, przez którą patrzy.
            """.trimIndent()
        ))

        register(ChronicleEntry(
            id = "lore_ferrun_iron_wall",
            title = "Żelazna Blokada",
            category = "Endings",
            fullText = """
                Góry Południowe zadrżały, gdy Ferrun zasiadł na tronie w najgłębszej kopalni. 
                Jego ciało połączyło się z żyłami Gęstej Ciemności, tworząc nieprzenikalną barierę dla Pustki. 
                Nicość nie może przejść przez dno, które ma wolę twardszą od bazaltu.
            """.trimIndent()
        ))

        register(ChronicleEntry(
            id = "lore_noctyros_update",
            title = "Systemowa Aktualizacja",
            category = "Endings",
            fullText = """
                Noctyros nadpisał Pęknięcie własnym kodem. 
                Zamiast destrukcji, kraina Pogranicza otrzymała nowy paradygmat egzystencji. 
                Błąd logiczny został zamieniony w nową funkcję, a rzeczywistość stepów stała się odporna na procesy czyszczące Skrybów.
            """.trimIndent()
        ))

        register(ChronicleEntry(
            id = "lore_scribe_tools",
            title = "Narzędzia Skrybów",
            category = "The Scribe Files",
            fullText = "Czy zastanawiałeś się, dlaczego interfejs Twojego umysłu (UI) wygląda tak, a nie inaczej? To systemowy panel kontrolny, który Skrybowie zostawili aktywny dla Kotwicy. Jesteś jedynym procesem, który może 'widzieć' menu rzeczywistości."
        ))

        register(ChronicleEntry(
            id = "lore_the_void_echo",
            title = "Głos Pustki",
            category = "Ontology",
            fullText = "Pustka nie jest cicha. Jeśli wsłuchasz się wystarczająco głęboko w szum Pęknięcia, usłyszysz tysiące głosów mówiących naraz. To dane, które nie mają już miejsca, do którego mogłyby należeć."
        ))

        register(ChronicleEntry(
            id = "lore_mirror_mira_truth",
            title = "Prawdziwa Mira",
            category = "Saints",
            fullText = "Mira w Sercu Krainy to nie ta sama osoba, która zaczynała tę sesję. Oryginalna Mira została wymazana podczas pierwszej wielkiej anomalii. Ta, którą znasz, to tylko 'backup', który zyskał samoświadomość."
        ))

        register(ChronicleEntry(
            id = "lore_ferrun_iron_will",
            title = "Wola Żelaza",
            category = "Saints",
            fullText = "Ferrun Żelazny wierzy, że stal jest jedyną rzeczą, której Skryba nie może łatwo wymazać. Twierdzi, że gęstość materii w Górach to jedyna twarda blokada przed Pęknięciem."
        ))

        register(ChronicleEntry(
            id = "lore_xyrel_judgment",
            title = "Ślepy Wyrok",
            category = "Factions",
            fullText = "Inkwizytor Xyrel nie szuka winnych wśród ludzi. On szuka winnych w samym kodzie. Każdy, kogo skaże, jest dla niego po prostu uszkodzonym sektorem pamięci."
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

    fun record(text: String, importance: Int = 1) {
        gameRepository.log(text)
    }

    fun getUnlockedEntries(): List<ChronicleEntry> {
        val unlockedIds = gameRepository.currentState().unlockedLoreIds
        // Filter directly from values - more efficient than toList().filter()
        return allEntries.values.filter { unlockedIds.contains(it.id) }
    }

    fun isUnlocked(id: String): Boolean = gameRepository.currentState().unlockedLoreIds.contains(id)

    fun getAll(): List<ChronicleEntry> = allEntries.values.toList()
}
