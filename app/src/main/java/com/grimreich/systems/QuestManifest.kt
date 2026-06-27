package com.grimreich.systems

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuestManifest @Inject constructor(
    private val engine: QuestEngine
) {
    fun seed() {
        engine.register(QuestDefinition(
            id = "q_blood_icon",
            title = "Szept Krwawej Ikony",
            description = "Wioska czci posąg, który zaczyna odpowiadać.",
            rewardGold = 100,
            cityId = "wybrzeze_polnocne",
            originNpcId = "mystic",
            steps = listOf(
                QuestStep("Oczyść posąg z mroku.", StepType.COMBAT, "boss_statue")
            )
        ))

        engine.register(QuestDefinition(
            id = "q_verdict_1",
            title = "Gabinet bez śladów",
            description = "Zbadaj miejsce zbrodni wysokiego urzędnika.",
            rewardGold = 150,
            cityId = "twierdza_zakonu",
            originNpcId = "guard",
            steps = listOf(
                QuestStep("Przeszukaj gabinet urzędnika.", StepType.INVESTIGATION, "office_id")
            )
        ))
    }
}
