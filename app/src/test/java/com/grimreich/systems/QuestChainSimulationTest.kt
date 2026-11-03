package com.grimreich.systems

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.grimreich.core.GameState
import com.grimreich.core.QuestStatus
import com.grimreich.core.StepType
import com.grimreich.grimreich.v1.DialogueNode
import com.grimreich.world.CityCatalogue
import com.grimreich.world.ItemCatalogue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class QuestChainSimulationTest {

    private lateinit var questEngine: QuestEngine
    private lateinit var dialogueManager: DialogueManager
    private val state = GameState()
    private val allQuests = mutableListOf<QuestDefinition>()

    @Before
    fun setup() {
        val assetsDir = File("src/main/assets/grimreich")
        val gson = Gson()

        val mockRepo = mock<com.grimreich.core.GameRepository>()
        whenever(mockRepo.currentState()).thenReturn(state)

        val expSystem = mock<ExperienceSystem>()
        whenever(expSystem.addPartyXpDirect(any(), any())).thenReturn(emptyList())

        questEngine = QuestEngine(
            context = mock(),
            gameRepositoryProvider = { mockRepo },
            experienceSystemProvider = { expSystem }
        )

        // Load Quests
        val questType = object : TypeToken<List<QuestDefinition>>() {}.type
        assetsDir.listFiles { _, name -> name.startsWith("quests_") && name.endsWith(".json") }?.forEach { file ->
            val quests: List<QuestDefinition> = gson.fromJson(file.readText(), questType)
            quests.forEach {
                questEngine.register(it)
                allQuests.add(it)
            }
        }

        dialogueManager = DialogueManager(
            context = mock(),
            gameRepositoryProvider = { mockRepo },
            questEngine = { questEngine }
        )

        // Load Dialogues
        val dialogueType = object : TypeToken<List<DialogueNode>>() {}.type
        assetsDir.listFiles { _, name -> name.startsWith("dialogues_") && name.endsWith(".json") }?.forEach { file ->
            val nodes: List<DialogueNode> = gson.fromJson(file.readText(), dialogueType)
            nodes.forEach { dialogueManager.registerNode(it) }
        }
    }

    @Test
    fun testQuestAndChainCounts() {
        println("==================================================")
        println("      RAPORT STATYSTYK QUESTÓW I ŁAŃCUCHÓW        ")
        println("==================================================")
        println("Łączna liczba wszystkich questów: ${allQuests.size}")

        val chainGroup = allQuests.filter { it.chainId != null }.groupBy { it.chainId!! }
        println("Liczba zdefiniowanych łańcuchów (chains): ${chainGroup.size}")

        chainGroup.forEach { (chainId, quests) ->
            val sorted = quests.sortedBy { it.chainOrder }
            println(" - Łańcuch '$chainId': ${quests.size} questów (${sorted.joinToString(" -> ") { it.id }})")
        }

        val standaloneQuests = allQuests.filter { it.chainId == null }
        println("Liczba questów niezależnych (standalone): ${standaloneQuests.size}")
        println("==================================================")

        assertTrue("Projekt musi posiadać co najmniej 66 questów", allQuests.size >= 66)
        assertTrue("Projekt musi posiadać co najmniej 5 łańcuchy questów", chainGroup.size >= 5)
    }

    @Test
    fun testSimulateAllQuestsTransitions() {
        var completedCount = 0

        // Give player meta awareness to unlock meta quests
        state.metaAwarenessLevel = 10

        allQuests.forEach { quest ->
            // Set state location to quest's city
            state.world.locationId = quest.cityId

            // Activate quest
            questEngine.activateQuestDirect(state, quest.id)
            val activeStatus = questEngine.getStatus(quest.id, state)
            assertEquals("Quest ${quest.id} should be active or available", QuestStatus.active, activeStatus)

            // Step through all steps
            quest.steps.forEachIndexed { _, step ->
                when (step.type) {
                    StepType.dialogue, StepType.investigation, StepType.social, StepType.meta -> {
                        val node = dialogueManager.getNode(step.targetId)
                        if (node != null) {
                            // Find choice that advances or completes quest
                            val choice = node.choices.find { 
                                it.triggerEvent == "advance_quest" || it.triggerEvent == "complete_quest" 
                            }
                            if (choice != null) {
                                dialogueManager.handleTrigger(state, choice.triggerEvent, choice.triggerValue ?: quest.id)
                            } else {
                                questEngine.advanceStepDirect(state, quest.id)
                            }
                        } else {
                            questEngine.advanceStepDirect(state, quest.id)
                        }
                    }
                    StepType.combat, StepType.expedition -> {
                        questEngine.advanceStepDirect(state, quest.id)
                    }
                    else -> {
                        questEngine.advanceStepDirect(state, quest.id)
                    }
                }
            }

            // Complete quest
            questEngine.completeQuestDirect(state, quest.id)

            val finalStatus = questEngine.getStatus(quest.id, state)
            assertEquals("Quest ${quest.id} must reach status 'completed'", QuestStatus.completed, finalStatus)
            completedCount++
        }

        assertEquals("All ${allQuests.size} quests must be completable without errors", allQuests.size, completedCount)
    }
}
