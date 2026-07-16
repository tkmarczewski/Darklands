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
        
        val allNodes = mutableListOf<DialogueNode>()
        
        // Phase 1: Load all nodes from all files
        assetsDir.listFiles { _, name -> name.endsWith(".json") && name.contains("dialogue") }?.forEach { file ->
            try {
                val json = file.readText()
                val nodes: List<DialogueNode> = gson.fromJson(json, type)
                allNodes.addAll(nodes)
            } catch (e: Exception) {
                fail("Failed to parse ${file.name}: ${e.message}")
            }
        }

        val allNodeIds = allNodes.map { it.id }.toSet()
        
        // Phase 2: Validate each node and its links
        allNodes.forEach { node ->
            assertTrue("Node ID cannot be empty", node.id.isNotEmpty())
            assertTrue("NPC ID cannot be empty in node ${node.id}", node.npcId.isNotEmpty())
            assertTrue("Text cannot be empty in node ${node.id}", node.text.isNotEmpty())
            
            node.choices.forEach { choice ->
                if (choice.targetNodeId != "end") {
                    assertTrue(
                        "Dead link: node '${node.id}' points to non-existent '${choice.targetNodeId}'",
                        allNodeIds.contains(choice.targetNodeId)
                    )
                }
            }
        }
    }
}
