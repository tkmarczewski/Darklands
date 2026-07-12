package com.grimreich.systems

import android.util.Log
import com.grimreich.world.CityCatalogue
import com.grimreich.world.ItemCatalogue
import javax.inject.Inject
import javax.inject.Singleton

sealed interface ContentError {
    val message: String
    val severity: ErrorSeverity

    data class QuestError(override val message: String, override val severity: ErrorSeverity = ErrorSeverity.CRITICAL) : ContentError
    data class DialogueError(override val message: String, override val severity: ErrorSeverity = ErrorSeverity.CRITICAL) : ContentError
    data class ItemError(override val message: String, override val severity: ErrorSeverity = ErrorSeverity.WARNING) : ContentError
    data class CityError(override val message: String, override val severity: ErrorSeverity = ErrorSeverity.CRITICAL) : ContentError
    data class BestiaryError(override val message: String, override val severity: ErrorSeverity = ErrorSeverity.CRITICAL) : ContentError
    data class MutationError(override val message: String, override val severity: ErrorSeverity = ErrorSeverity.CRITICAL) : ContentError
}

enum class ErrorSeverity {
    WARNING, CRITICAL
}

@Singleton
class ContentValidator @Inject constructor(
    private val questEngine: QuestEngine,
    private val itemCatalogue: ItemCatalogue,
    private val cityCatalogue: CityCatalogue,
    private val dialogueManager: DialogueManager
) {
    private val _errors = mutableListOf<ContentError>()
    val errors: List<ContentError> get() = _errors

    fun validateAll(): List<ContentError> {
        _errors.clear()
        Log.i("ContentValidator", "Starting full content validation...")

        validateQuests()
        validateDialogues()
        validateCities()
        validateItems()
        validateBestiary()
        validateMutations()

        if (questEngine.getAllDefinitions().isEmpty()) {
            _errors.add(ContentError.QuestError("Quest registry is EMPTY. Manifest seeding might have failed.", ErrorSeverity.CRITICAL))
        }

        if (_errors.isEmpty()) {
            Log.i("ContentValidator", "✅ Content validation passed! No issues found.")
        } else {
            val criticalCount = _errors.count { it.severity == ErrorSeverity.CRITICAL }
            Log.e("ContentValidator", "❌ Content validation failed with ${_errors.size} issues ($criticalCount critical).")
            _errors.forEach { error ->
                val prefix = if (error.severity == ErrorSeverity.CRITICAL) "[CRITICAL]" else "[WARNING]"
                Log.e("ContentValidator", "$prefix ${error.message}")
            }
        }

        return _errors
    }

    private fun validateQuests() {
        val allQuests = questEngine.getAllDefinitions()
        allQuests.forEach { quest ->
            // Validate City
            if (cityCatalogue.get(quest.cityId) == null) {
                _errors.add(ContentError.QuestError("Quest '${quest.id}' refers to non-existent cityId: '${quest.cityId}'"))
            }

            // Validate Prerequisite
            if (quest.prerequisiteQuestId != null && (questEngine.getDefinition(quest.prerequisiteQuestId) == null)) {
                _errors.add(ContentError.QuestError("Quest '${quest.id}' refers to non-existent prerequisiteQuestId: '${quest.prerequisiteQuestId}'"))
            }

            // Validate Steps
            quest.steps.forEachIndexed { index, step ->
                if (step.type == StepType.EXPEDITION && cityCatalogue.get(step.targetId) == null) {
                    _errors.add(ContentError.QuestError("Quest '${quest.id}' step $index (EXPEDITION) refers to non-existent targetId: '${step.targetId}'"))
                }
                
                if (step.type == StepType.DIALOGUE && (!dialogueManager.hasNode(step.targetId))) {
                    _errors.add(ContentError.DialogueError("Quest '${quest.id}' step $index (DIALOGUE) refers to non-existent nodeId: '${step.targetId}'", ErrorSeverity.WARNING))
                }

                if (step.type == StepType.COMBAT) {
                    try {
                        val enemyType = com.grimreich.core.EnemyType.valueOf(step.targetId)
                        if (com.grimreich.core.Bestiary.get(enemyType).name.contains("Błąd Rzeczywistości")) {
                            _errors.add(ContentError.QuestError("Quest '${quest.id}' step $index (COMBAT) refers to enemy '$enemyType' which uses fallback bestiary entry.", ErrorSeverity.WARNING))
                        }
                    } catch (e: Exception) {
                        _errors.add(ContentError.QuestError("Quest '${quest.id}' step $index (COMBAT) refers to non-existent enemy type: '${step.targetId}'"))
                    }
                }
            }
            
            // Detect Prerequisite Cycles
            if (quest.prerequisiteQuestId == quest.id) {
                _errors.add(ContentError.QuestError("Quest '${quest.id}' has a self-referencing prerequisite cycle."))
            }
        }
    }

    private fun validateDialogues() {
        val nodes = dialogueManager.getAllNodes()
        nodes.forEach { (nodeId, node) ->
            node.choices.forEach { choice ->
                // Target Node Validation
                if (choice.targetNodeId != "end" && !dialogueManager.hasNode(choice.targetNodeId)) {
                    _errors.add(ContentError.DialogueError("Dialogue node '$nodeId' choice leads to non-existent nodeId: '${choice.targetNodeId}'"))
                }

                // Quest Trigger Validation
                when (choice.triggerEvent) {
                    "ACTIVATE_QUEST", "COMPLETE_QUEST", "FAIL_QUEST", "ADVANCE_QUEST" -> {
                        if (choice.triggerValue != null && choice.triggerValue != "ACTIVE" && questEngine.getDefinition(choice.triggerValue) == null) {
                            _errors.add(ContentError.QuestError("Dialogue node '$nodeId' trigger '${choice.triggerEvent}' refers to non-existent questId: '${choice.triggerValue}'", ErrorSeverity.WARNING))
                        }
                    }
                }
            }
        }
    }

    private fun validateCities() {
        cityCatalogue.all().forEach { city ->
            city.marketStock.forEach { itemId ->
                if (itemCatalogue.findByTemplateId(itemId) == null) {
                    _errors.add(ContentError.CityError("City '${city.id}' market offers non-existent itemId: '$itemId'"))
                }
            }
        }
    }

    private fun validateItems() {
        itemCatalogue.allTemplates().forEach { item ->
            if (item.value < 0) {
                _errors.add(ContentError.ItemError("Item '${item.templateId}' has negative value: ${item.value}"))
            }
            if (item.weight < 0) {
                _errors.add(ContentError.ItemError("Item '${item.templateId}' has negative weight: ${item.weight}"))
            }
        }
    }

    private fun validateBestiary() {
        com.grimreich.core.EnemyType.entries.forEach { type ->
            val enemy = com.grimreich.core.Bestiary.get(type)
            enemy.lootTable.itemChances.keys.forEach { itemId ->
                if (itemCatalogue.findByTemplateId(itemId) == null) {
                    _errors.add(ContentError.BestiaryError("Enemy '$type' loot table refers to non-existent itemId: '$itemId'"))
                }
            }
        }
    }

    private fun validateMutations() {
        com.grimreich.core.mutations.MutationRegistry.allMutations.forEach { mutation ->
            mutation.attributeModifiers.keys.forEach { attr ->
                val validAttrs = listOf("strength", "agility", "perception", "intelligence", "endurance", "charisma", "piety")
                if (!validAttrs.contains(attr.lowercase())) {
                    _errors.add(ContentError.MutationError("Mutation '${mutation.id}' refers to invalid attribute: '$attr'"))
                }
            }
            if (mutation.stabilityImpact > 0) {
                _errors.add(ContentError.MutationError("Mutation '${mutation.id}' has positive stability impact (+${mutation.stabilityImpact}). Mutations should destabilize the world.", ErrorSeverity.WARNING))
            }
        }
    }
}
