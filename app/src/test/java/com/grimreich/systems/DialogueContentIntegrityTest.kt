package com.grimreich.systems

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.grimreich.grimreich.v1.DialogueNode
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.io.File

class DialogueContentIntegrityTest {

    @Test
    fun `validate all dialogue files in assets`() {
        val assetsDir = File("src/main/assets/grimreich")
        if (!assetsDir.exists()) {
            fail("Assets directory not found at ${assetsDir.absolutePath}")
        }

        val gson = Gson()
        val type = object : TypeToken<List<DialogueNode>>() {}.type
        
        assetsDir.listFiles { _, name -> name.endsWith(".json") }?.forEach { file ->
            try {
                val json = file.readText()
                if (file.name.contains("dialogue")) {
                    val nodes: List<DialogueNode> = gson.fromJson(json, type)
                    nodes.forEach { node ->
                        assertTrue("Node ID cannot be empty in ${file.name}", node.id.isNotEmpty())
                        assertTrue("NPC ID cannot be empty in node ${node.id}", node.npcId.isNotEmpty())
                        assertTrue("Text cannot be empty in node ${node.id}", node.text.isNotEmpty())
                    }
                }
            } catch (e: Exception) {
                fail("Failed to parse ${file.name}: ${e.message}")
            }
        }
    }
}
