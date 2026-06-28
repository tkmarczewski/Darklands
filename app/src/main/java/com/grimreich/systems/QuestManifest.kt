package com.grimreich.systems

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuestManifest @Inject constructor(
    private val engine: QuestEngine
) {
    fun seed() {
        Log.d("QuestManifest", "Seeding quests...")
        
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
        
        // --- QUEST CHAIN: Shadows of the Scribes ---
        
        registerQuest(
            id = "q_scribes_1",
            title = "Cienie Archiwistów I",
            desc = "Mira twierdzi, że ktoś podmienia wpisy w Kronice Świata.",
            cityId = "serce_krainy",
            npcId = "mira",
            gold = 150,
            stepType = StepType.INVESTIGATION,
            target = "Pęknięta Kronika"
        )

        registerQuest(
            id = "q_scribes_2",
            title = "Cienie Archiwistów II",
            desc = "Ślady prowadzą do zapomnianego skryptorium. Musisz wyeliminować Echa Archiwistów.",
            cityId = "serce_krainy",
            npcId = "mira",
            gold = 250,
            stepType = StepType.COMBAT,
            target = "Echo Archiwisty",
            prerequisiteId = "q_scribes_1"
        )

        registerQuest(
            id = "q_scribes_3",
            title = "Cienie Archiwistów III",
            desc = "Finałowa konfrontacja z Cieniem Pierwszego Sędziego.",
            cityId = "serce_krainy",
            npcId = "mira",
            gold = 500,
            stepType = StepType.COMBAT,
            target = "Pierwszy Sędzia (Cień)",
            prerequisiteId = "q_scribes_2"
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
            target = "Boss: Echo Absolutu",
            prerequisiteId = "q_scribes_3"
        )
        
        Log.d("QuestManifest", "Quests seeded successfully.")
    }

    private fun registerQuest(
        id: String, title: String, desc: String, 
        cityId: String, npcId: String, gold: Int, 
        stepType: StepType, target: String,
        prerequisiteId: String? = null
    ) {
        engine.register(QuestDefinition(
            id = id,
            title = title,
            description = desc,
            rewardGold = gold,
            cityId = cityId,
            originNpcId = npcId,
            steps = listOf(
                QuestStep("Cel: $target", stepType, target)
            ),
            prerequisiteQuestId = prerequisiteId
        ))
    }
}
