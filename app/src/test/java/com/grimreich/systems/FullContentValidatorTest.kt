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
}
