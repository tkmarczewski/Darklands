package com.grimreich.systems

object QuestDefinitionRegistry {

    val allDefinitions: List<QuestDefinition> = listOf(
        // TEST CHAIN
        QuestDefinition("q_blood_icon", "Szept Krwawej Ikony", "Wioska czci posąg, który zaczyna odpowiadać.", "Intrigue", 100, listOf(
            QuestStep("blood_step_1", QuestStepType.COMBAT, "q_blood_icon", "Oczyść posąg z mroku.")
        )),

        // VERDICT CHAIN (Wyrok)
        QuestDefinition("q_verdict_1", "Gabinet bez śladów", "Zbadaj miejsce zbrodni wysokiego urzędnika.", "Verdict", 150, listOf(
            QuestStep("v1_s1", QuestStepType.INVESTIGATION, "office", "Przeszukaj gabinet urzędnika.")
        )),
        QuestDefinition("q_verdict_2", "Mieszkanie, które nie pamięta", "Archiwistka Imperium zniknęła. Jej dom jest pusty.", "Verdict", 150, listOf(
            QuestStep("v2_s1", QuestStepType.INVESTIGATION, "house", "Odszukaj ślady Liry Voss.")
        )),
        QuestDefinition("q_verdict_3", "Fabryka, która zabiła sama siebie", "WINNI. Tak brzmi wyrok na ścianie fabryki.", "Verdict", 200, listOf(
            QuestStep("v3_s1", QuestStepType.COMBAT, "q_verdict_3", "Przetrwaj w opuszczonej fabryce.")
        )),

        // BLOOD CHAIN
        QuestDefinition("q_blood_1", "Krzyk z Piwnicy", "Wioska prosi o pomoc.", "Chain", 100, listOf(
            QuestStep("b1_s1", QuestStepType.COMBAT, "q_blood_1", "Zbadaj piwnicę starego domu.")
        )),
        QuestDefinition("q_blood_2", "Dziewczyna, która widziała Twoją twarz", "Świadek twierdzi, że Cię widziała.", "Chain", 110, listOf(
            QuestStep("b2_s1", QuestStepType.DIALOGUE, "citizen", "Porozmawiaj z dziewczyną.")
        ))
    )

    fun getById(id: String): QuestDefinition? = allDefinitions.find { it.id == id }
}
