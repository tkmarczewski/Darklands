package com.grimreich.ui.main

import androidx.lifecycle.ViewModel
import com.grimreich.core.Career
import com.grimreich.core.HeroSkill
import com.grimreich.core.SkillGroup
import com.grimreich.grimreich.v1.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlin.random.Random

data class CharacterCreatorUiState(
    val selectedCareer: Career = Career.KNIGHT,
    val pointsRemaining: Int = 20,
    val specializationPointsRemaining: Int = 3,
    val attributes: Map<String, Int> = mapOf(
        "Str" to 10, "Agi" to 10, "Per" to 10, "Int" to 10, "End" to 10, "Cha" to 10, "Pie" to 10
    ),
    val specializedSkills: Set<HeroSkill> = emptySet(),
    val availableSkills: List<HeroSkill> = emptyList()
)

@HiltViewModel
class CharacterCreatorViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(CharacterCreatorUiState())
    val uiState: StateFlow<CharacterCreatorUiState> = _uiState.asStateFlow()

    init {
        selectCareer(Career.KNIGHT)
    }

    fun selectCareer(career: Career) {
        val newAttrs = _uiState.value.attributes.toMutableMap()
        newAttrs.keys.forEach { newAttrs[it] = 10 }
        
        when (career) {
            Career.KNIGHT -> { newAttrs["Str"] = 13; newAttrs["End"] = 12 }
            Career.ALCHEMIST -> { newAttrs["Int"] = 14; newAttrs["Cha"] = 12 }
            Career.GUARD -> { newAttrs["Per"] = 13; newAttrs["Agi"] = 12 }
            Career.SCHOLAR -> { newAttrs["Int"] = 15; newAttrs["Str"] = 8 }
            else -> {}
        }

        _uiState.update {
            it.copy(
                selectedCareer = career,
                attributes = newAttrs,
                pointsRemaining = 20,
                specializedSkills = emptySet(),
                specializationPointsRemaining = 3,
                availableSkills = availableSkillsForCareer(career)
            )
        }
    }

    private fun availableSkillsForCareer(career: Career): List<HeroSkill> {
        val allSkills = HeroSkill.entries
        return when (career) {
            Career.KNIGHT -> allSkills.filter { it.group == SkillGroup.WEAPON || it.group == SkillGroup.ARMOR }
            Career.ALCHEMIST -> allSkills.filter { it.group == SkillGroup.ACADEMIC || it.name == "ALCH" }
            Career.GUARD -> allSkills.filter { it.group == SkillGroup.WEAPON || it.group == SkillGroup.SURVIVAL }
            Career.SCHOLAR -> allSkills.filter { it.group == SkillGroup.ACADEMIC }
            else -> allSkills.toList()
        }
    }

    fun changeAttr(key: String, delta: Int) {
        val current = _uiState.value.attributes[key] ?: 10
        val remaining = _uiState.value.pointsRemaining
        
        if (delta > 0 && remaining > 0) {
            val newAttrs = _uiState.value.attributes.toMutableMap()
            newAttrs[key] = current + 1
            _uiState.update { it.copy(attributes = newAttrs, pointsRemaining = remaining - 1) }
        } else if (delta < 0 && current > 5) {
            val newAttrs = _uiState.value.attributes.toMutableMap()
            newAttrs[key] = current - 1
            _uiState.update { it.copy(attributes = newAttrs, pointsRemaining = remaining + 1) }
        }
    }

    fun toggleSkill(skill: HeroSkill) {
        val currentSet = _uiState.value.specializedSkills.toMutableSet()
        val remaining = _uiState.value.specializationPointsRemaining
        
        if (currentSet.contains(skill)) {
            currentSet.remove(skill)
            _uiState.update { it.copy(specializedSkills = currentSet, specializationPointsRemaining = remaining + 1) }
        } else if (remaining > 0) {
            currentSet.add(skill)
            _uiState.update { it.copy(specializedSkills = currentSet, specializationPointsRemaining = remaining - 1) }
        }
    }

    fun randomizeAll() {
        val career = listOf(Career.KNIGHT, Career.ALCHEMIST, Career.GUARD, Career.SCHOLAR).random()
        selectCareer(career)
        
        val preferred = when (career) {
            Career.KNIGHT -> listOf("Str", "End", "Agi")
            Career.ALCHEMIST -> listOf("Int", "Cha", "Per")
            Career.GUARD -> listOf("Per", "Agi", "End")
            Career.SCHOLAR -> listOf("Int", "Pie", "Cha")
            else -> _uiState.value.attributes.keys.toList()
        }

        repeat(20) {
            val key = if (Random.nextInt(100) < 70) preferred.random() else _uiState.value.attributes.keys.random()
            changeAttr(key, 1)
        }

        val skills = availableSkillsForCareer(career).shuffled().take(3)
        skills.forEach { toggleSkill(it) }
    }

    fun randomName(): String {
        val first = listOf("Klaus", "Hans", "Helga", "Greta", "Otto", "Bruno", "Marta", "Erich")
        val last = listOf("von Weber", "Schmidt", "Müller", "Wagner", "Becker", "Hoffmann")
        return "${first.random()} ${last.random()}"
    }
}
