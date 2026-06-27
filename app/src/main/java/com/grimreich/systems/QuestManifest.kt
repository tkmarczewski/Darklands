package com.grimreich.systems

import com.grimreich.grimreich.v1.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuestManifest @Inject constructor(
    private val engine: QuestEngine
) {
    fun seed() {
        // Main Verdict Quest
        registerQuest(
            id = "q_verdict_1",
            title = "Początek Wyroku",
            desc = "Inkwizycja podejrzewa, że stabilność regionu jest sabotowana przez kultystów Echa.",
            cityId = "wybrzeze_polnocne",
            npcId = "guard",
            gold = 100,
            stepType = StepType.COMBAT,
            target = "Kultysta Echa"
        )

        // North Coast Secondary
        registerQuest(
            id = "q_coast_harvest",
            title = "Żniwa Mgły",
            desc = "Archiwiści potrzebują rzadkich ziół, które rosną tylko w gęstych oparach.",
            cityId = "wybrzeze_polnocne",
            npcId = "merchant",
            gold = 50,
            stepType = StepType.INVESTIGATION,
            target = "Gęsta Mgła"
        )
        
        // Late-game Quest (Climax)
        registerQuest(
            id = "q_collapse_core",
            title = "Serce Kolapsu",
            desc = "Dotrzyj do źródła anomalii w Sercu Krainy i spróbuj scalić paradygmat.",
            cityId = "serce_krainy",
            npcId = "mira",
            gold = 500,
            stepType = StepType.COMBAT,
            target = "Boss: Echo Absolutu"
        )
    }

    private fun registerQuest(
        id: String, title: String, desc: String, 
        cityId: String, npcId: String, gold: Int, 
        stepType: StepType, target: String
    ) {
        engine.register(QuestDefinition(
            id = id,
            title = title,
            description = desc,
            rewardGold = gold,
            cityId = cityId,
            originNpcId = npcId,
            steps = listOf(
                QuestStep("Zbadaj: $target", stepType, target)
            )
        ))
    }
}
