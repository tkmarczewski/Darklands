package com.grimreich.systems

import com.grimreich.grimreich.v1.NPC
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NpcFlavorSystem @Inject constructor() {

    fun getFlavorDescription(npc: NPC): String {
        return when (npc.role.uppercase()) {
            "GUARD" -> "Weteran o twarzy pociętej bliznami. Jego historia to ciągła walka o porządek v chaosie."
            "SCHOLAR" -> "Otoczony zapachem starego pergaminu. Jego spojrzenie sugeruje umysł, który potrafi dostrzec anomalie v zapisie rzeczywistości."
            "PRIEST" -> "W jego oczach widać blask, którego nie daje słońce. Przetrwa tam, gdzie inni utracą wiarę."
            "MERCENARY" -> "Człowiek, który przeżył więcej sesji, niż powinien. Jego postawa to gwarancja, że nie padnie przy pierwszym błędzie."
            else -> "Niepozorny podróżnik. Wygląda, jakby widział zbyt wiele, by nadal czuć się bezpiecznie."
        }
    }

    fun getRecruitmentReason(npc: NPC): String {
        val str = npc.stats["Str"] ?: 10
        val intel = npc.stats["Int"] ?: 10
        val pie = npc.stats["Pie"] ?: 10

        return when {
            str > 15 -> "Jego siła to jedyna rzecz, która powstrzyma cienie, gdy świat zacznie pękać."
            intel > 15 -> "Jego wiedza to jedyny kompas v labiryncie prawd, które próbują nas wymazać."
            pie > 15 -> "Jego wiara jest kotwicą, która utrzymuje spójność drużyny v obliczu nicości."
            else -> "To dusza, która jeszcze nie straciła nadziei. Potrzebujemy kogoś takiego, by utrzymać nas przy zmysłach."
        }
    }

    fun getPersonalityDescription(npc: NPC): String {
        return when (npc.personality.lowercase()) {
            "normal" -> "Jest dość zrównoważony, jak na kogoś żyjącego v świecie, który powoli przestaje istnieć."
            "cynical" -> "Każde jego słowo jest przesiąknięte goryczą. Widział za dużo końców świata."
            "zealous" -> "Płonie ogniem swoich przekonań. Czasem boisz się, czy nie spali przy tym całej drużyny."
            "melancholic" -> "Zdaje się być pogrążony v wiecznym smutku, jakby żałował czegoś, co dopiero się wydarzy."
            else -> "Jego osobowość jest trudna do odczytania, jakby coś w nim celowo zacierało ślady."
        }
    }
}
