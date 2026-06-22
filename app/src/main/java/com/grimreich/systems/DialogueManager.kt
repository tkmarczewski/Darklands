package com.grimreich.systems

import com.grimreich.core.GameRepository
import com.grimreich.core.GameConstants
import com.grimreich.grimreich.v1.DialogueChoice
import com.grimreich.grimreich.v1.DialogueNode
import dagger.Lazy
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class DialogueManager @Inject constructor(
    private val gameRepositoryProvider: Lazy<GameRepository>
) {
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
            "merchant", "kupiec" -> "port_rogue"
            "zealot", "pielgrzym" -> "port_priest"
            "mystic", "mistyk" -> "port_mage"
            "guard", "straznik" -> "port_warrior"
            "xyrel" -> "port_knight"
            "mira" -> "port_mage"
            "sereth" -> "port_wraith"
            "ferrun" -> "port_barbarian"
            "noctyros" -> "port_demon"
            "incident" -> "port_skeleton"
            else -> "port_rogue"
        }
    }

    private fun applyWorldEffects(node: DialogueNode): DialogueNode {
        val state = gameRepositoryProvider.get().currentState()
        val stability = state.world.globalStability
        
        if (stability >= GameConstants.STABILITY_THRESHOLD_HIGH) return node
        
        val fracturedText = if (stability < GameConstants.STABILITY_THRESHOLD_LOW) {
            glitchText(node.text) + " ...GŁOSY... ABSOLUT... [NIE SŁUCHAJ ICH] ...CISZA..."
        } else {
            node.text + " (rzeczywistość wokół ciebie zaczyna tracić nasycenie)"
        }
        
        return node.copy(text = fracturedText)
    }

    private fun glitchText(text: String): String {
        return text.split(" ").map { word ->
            if (Random.nextFloat() < 0.1f) "[BŁĄD_ONTOLOGICZNY]" else word
        }.joinToString(" ")
    }
    
    fun seedBasicDialogues() {
        if (nodes.isNotEmpty()) return

        // 1. GUARD
        registerNode(DialogueNode(
            id = "guard_start", npcId = "guard",
            text = "Stój! Mgła gęstnieje, a prawo musi być przestrzegane. Czego szukasz w cieniu murów?",
            choices = listOf(
                DialogueChoice("Szukam pracy.", "guard_work"),
                DialogueChoice("Tylko przechodzę.", "end")
            )
        ))
        registerNode(DialogueNode(id = "guard_work", npcId = "guard", text = "Zawsze potrzebujemy rąk do pracy przy murach. Sprawdź tablicę zadań w HUBie.", choices = listOf(DialogueChoice("Dziękuję.", "end"))))

        // 2. MERCHANT
        registerNode(DialogueNode(
            id = "merchant_start", npcId = "merchant",
            text = "Mam towary z Drugiej Strony. Złoto jest tu jedyną prawdą. Chcesz handlować?",
            choices = listOf(
                DialogueChoice("Pokaż co masz (OTWÓRZ TARG).", "end"),
                DialogueChoice("Może innym razem.", "end")
            )
        ))

        // 3. PILGRIM / ZEALOT
        registerNode(DialogueNode(
            id = "zealot_start", npcId = "zealot",
            text = "Prorocy patrzą! Czy Twoja dusza jest czysta, wędrowcze? Pielgrzymujemy do Serca Krainy, by obmyć się w jeziorach prawdy.",
            choices = listOf(
                DialogueChoice("Jestem wierny.", "end"),
                DialogueChoice("Ofiaruj krew (HP-${GameConstants.ZEALOT_SACRIFICE_HP_LOSS})", "zealot_sacrifice", onSelect = { 
                    it.party.forEach { h -> h.hp -= GameConstants.ZEALOT_SACRIFICE_HP_LOSS }
                }),
                DialogueChoice("Dokąd dokładnie zmierzacie?", "zealot_destination")
            )
        ))
        registerNode(DialogueNode(id = "zealot_destination", npcId = "zealot", text = "Do Opactwa Ciszy. Tam, gdzie słowa tracą znaczenie, a Absolut staje się słyszalny.", choices = listOf(DialogueChoice("Powodzenia.", "end"))))
        registerNode(DialogueNode(id = "zealot_sacrifice", npcId = "zealot", text = "Twoja ofiara została przyjęta. Czuć mrowienie w kościach.", choices = listOf(DialogueChoice("Idź w pokoju.", "end"))))

        // 4. MYSTIC
        registerNode(DialogueNode(
            id = "mystic_start", npcId = "mystic",
            text = "Cień w Tobie rośnie. Absolut Cię woła, Kotwico. Widzę wieżę bez drzwi w Twoich snach... czy ona już tu jest?",
            choices = listOf(
                DialogueChoice("Powiedz mi o wieży (ZADANIE).", "mystic_tower_info"),
                DialogueChoice("Kim jesteś?", "mystic_who"),
                DialogueChoice("Nie interesują mnie sny.", "end")
            )
        ))
        registerNode(DialogueNode(
            id = "mystic_tower_info", npcId = "mystic",
            text = "Ona pojawia się tylko tam, gdzie śmierć jest świeża. Szukaj jej na obrzeżach miasta, pośród mgły. By wejść, musisz przestać istnieć na chwilę.",
            choices = listOf(
                DialogueChoice("Jak mogę 'przestać istnieć'?", "mystic_tower_exist"),
                DialogueChoice("Rozumiem.", "end")
            )
        ))
        registerNode(DialogueNode(
            id = "mystic_tower_exist", npcId = "mystic",
            text = "To stan między uderzeniami serca. Medytuj w ciszy ruin. Jeśli uda Ci się odnaleźć wejście, zadanie zostanie ukończone.",
            choices = listOf(
                DialogueChoice("Spróbuję tego (UKOŃCZ ZADANIE: Wieża Bez Drzwi).", "end", onSelect = {
                    it.pendingQuestId = "COMPLETE:q_doorless_tower"
                }),
                DialogueChoice("To brzmi jak szaleństwo.", "end")
            )
        ))
        registerNode(DialogueNode(id = "mystic_who", npcId = "mystic", text = "Jestem echem kogoś, kto za długo patrzył w Pęknięcie. Widzę węzły czasu, które próbujesz rozplątać.", choices = listOf(DialogueChoice("To niepokojące.", "end"))))

        // 5. INCIDENT HOOK (VERDICT CHAIN)
        registerNode(DialogueNode(
            id = "verdict_hook_start", npcId = "incident",
            text = "Przed Tobą leżą zwłoki strażnika. Na ścianie obok ktoś nabazgrał krwią: 'WINNI'. To już trzeci taki przypadek w tym tygodniu.",
            choices = listOf(
                DialogueChoice("Zbadaj ciało (ZADANIE).", "verdict_hook_investigate"),
                DialogueChoice("Zawiadom straże.", "end")
            )
        ))
        registerNode(DialogueNode(
            id = "verdict_hook_investigate", npcId = "incident",
            text = "W zaciśniętej pięści denata znajdujesz symbol wysokiego urzędnika. Musisz sprawdzić jego gabinet w Twierdzy Zakonu.",
            choices = listOf(
                DialogueChoice("Podejmij śledztwo (START: Wyrok).", "end", onSelect = {
                    it.pendingQuestId = "q_verdict_1"
                })
            )
        ))

        // AELION
        registerNode(DialogueNode(
            id = "aelion_start", npcId = "aelion",
            text = "Mgła nie jest pogodą, wędrowcze. To skroplona niepamięć Absolutu.",
            choices = listOf(
                DialogueChoice("Pamiętam imię mojej matki.", "end"),
                DialogueChoice("Szukam wizji.", "end")
            )
        ))
    }
}
