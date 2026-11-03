package com.grimreich.systems

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.grimreich.core.Bestiary
import com.grimreich.core.Enemy
import com.grimreich.grimreich.v1.DialogueNode
import com.grimreich.world.CityCatalogue
import com.grimreich.world.ItemCatalogue
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class FullContentValidatorTest {

    @Test
    fun testValidateRealContent() {
        val assetsDir = File("src/main/assets/grimreich")
        val gson = Gson()

        // Load Bestiary
        val bestiaryFile = File(assetsDir, "bestiary_pilot.json")
        if (bestiaryFile.exists()) {
            val enemyType = object : TypeToken<List<Enemy>>() {}.type
            val enemies: List<Enemy> = gson.fromJson(bestiaryFile.readText(), enemyType)
            Bestiary.loadFromList(enemies)
        }

        val mockRepo = mock<com.grimreich.core.GameRepository>()
        whenever(mockRepo.currentState()).thenReturn(com.grimreich.core.GameState())

        val mockExp = mock<ExperienceSystem>()

        val questEngine = QuestEngine(
            context = mock(),
            gameRepositoryProvider = { mockRepo },
            experienceSystemProvider = { mockExp }
        )

        val itemCatalogue = ItemCatalogue().apply { seed() }
        val cityCatalogue = CityCatalogue().apply { seedCanonical() }

        whenever(mockRepo.itemCatalogue).thenReturn(itemCatalogue)

        // Load Quests
        val questType = object : TypeToken<List<QuestDefinition>>() {}.type
        assetsDir.listFiles { _, name -> name.startsWith("quests_") && name.endsWith(".json") }?.forEach { file ->
            val json = file.readText()
            val quests: List<QuestDefinition> = gson.fromJson(json, questType)
            quests.forEach { questEngine.register(it) }
        }

        val dialogueManager = DialogueManager(
            context = mock(),
            gameRepositoryProvider = { mockRepo },
            questEngine = { questEngine }
        )

        // Load Dialogues
        val dialogueType = object : TypeToken<List<DialogueNode>>() {}.type
        assetsDir.listFiles { _, name -> name.startsWith("dialogues_") && name.endsWith(".json") }?.forEach { file ->
            val json = file.readText()
            val nodes: List<DialogueNode> = gson.fromJson(json, dialogueType)
            nodes.forEach { dialogueManager.registerNode(it) }
        }

        val validator = ContentValidator(
            questEngine = questEngine,
            itemCatalogue = itemCatalogue,
            cityCatalogue = cityCatalogue,
            dialogueManager = dialogueManager
        )

        val errors = validator.validateAll()

        val errorDetails = errors.joinToString("\n") { " - [${it.severity}] ${it.message}" }
        assertEquals("Validation issues found:\n$errorDetails", 0, errors.size)
    }

    @Test
    fun testQuestChainsIntegrity() {
        val assetsDir = File("src/main/assets/grimreich")
        val gson = Gson()

        val mockRepo = mock<com.grimreich.core.GameRepository>()
        whenever(mockRepo.currentState()).thenReturn(com.grimreich.core.GameState())
        val mockExp = mock<ExperienceSystem>()

        val questEngine = QuestEngine(
            context = mock(),
            gameRepositoryProvider = { mockRepo },
            experienceSystemProvider = { mockExp }
        )

        val questType = object : TypeToken<List<QuestDefinition>>() {}.type
        assetsDir.listFiles { _, name -> name.startsWith("quests_") && name.endsWith(".json") }?.forEach { file ->
            val quests: List<QuestDefinition> = gson.fromJson(file.readText(), questType)
            quests.forEach { questEngine.register(it) }
        }

        val dialogueManager = DialogueManager(
            context = mock(),
            gameRepositoryProvider = { mockRepo },
            questEngine = { questEngine }
        )

        val dialogueType = object : TypeToken<List<DialogueNode>>() {}.type
        assetsDir.listFiles { _, name -> name.startsWith("dialogues_") && name.endsWith(".json") }?.forEach { file ->
            val nodes: List<DialogueNode> = gson.fromJson(file.readText(), dialogueType)
            nodes.forEach { dialogueManager.registerNode(it) }
        }

        val chains = questEngine.getAllDefinitions().filter { it.chainId != null }.groupBy { it.chainId!! }
        
        chains.forEach { (chainId, questsInChain) ->
            val sortedChain = questsInChain.sortedBy { it.chainOrder }
            sortedChain.forEachIndexed { index, quest ->
                if (index > 0) {
                    val prevQuest = sortedChain[index - 1]
                    assertEquals(
                        "Quest ${quest.id} in chain $chainId must have prerequisite ${prevQuest.id}",
                        prevQuest.id,
                        quest.prerequisiteQuestId
                    )
                }
                
                // Ensure every dialogue step target exists in dialogueManager
                quest.steps.forEach { step ->
                    if (step.type == com.grimreich.core.StepType.dialogue || step.type == com.grimreich.core.StepType.investigation) {
                        val hasNode = dialogueManager.hasNode(step.targetId) || step.targetId == "end"
                        org.junit.Assert.assertTrue(
                            "Chain $chainId quest ${quest.id} step target '${step.targetId}' must exist in dialogues",
                            hasNode
                        )
                    }
                }
            }
        }
    }
}
