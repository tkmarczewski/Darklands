package com.grimreich.viewmodels

import androidx.lifecycle.ViewModel
import com.grimreich.systems.QuestEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class DevMenuViewModel @Inject constructor(
    private val questEngine: QuestEngine,
    val gameRepository: com.grimreich.core.GameRepository
) : ViewModel() {

    val logEntries: StateFlow<List<String>> = gameRepository.gameLogs

    private val _currentQuestInfo = MutableStateFlow("")
    val currentQuestInfo: StateFlow<String> = _currentQuestInfo.asStateFlow()

    fun addLog(msg: String) {
        gameRepository.log(msg)
    }

    fun addGold(amount: Int) {
        gameRepository.updateState { it.gold += amount }
        addLog("DEBUG: Dodano $amount gp")
    }

    fun healParty() {
        gameRepository.updateState { state ->
            state.party.forEach { hero ->
                hero.hp = hero.maxHp
                hero.isDead = false
                hero.activeStatusEffects.clear()
            }
        }
        addLog("DEBUG: Drużyna uleczona")
    }

    fun addTestHero() {
        gameRepository.updateState { state ->
            val hero = com.grimreich.core.Hero(
                id = "hero_main",
                name = "Debug Hero",
                age = 18,
                hp = 50,
                maxHp = 50
            )
            state.party.clear()
            state.party.add(hero)
            state.activeHeroId = hero.id
        }
        addLog("DEBUG: Dodano Debug Hero")
    }

    fun addXp(amount: Int) {
        gameRepository.updateState { state ->
            state.party.forEach { it.xp += amount }
        }
        addLog("DEBUG: Dodano $amount XP")
    }

    fun levelUp() {
        gameRepository.updateState { state ->
            state.party.forEach { it.level++ }
        }
        addLog("DEBUG: Level UP!")
    }

    fun addDays(amount: Int) {
        gameRepository.updateState { state ->
            val oldDay = state.world.day
            state.world.day += amount
            val fullYearsPassed = (state.world.day / 365) - (oldDay / 365)
            if (fullYearsPassed > 0) {
                state.party.forEach { hero ->
                    hero.age += fullYearsPassed
                }
                addLog("DEBUG: Upłynęło $fullYearsPassed lat.")
            }
        }
        addLog("DEBUG: Przesunięto czas o $amount dni.")
    }

    fun dumpState() {
        val state = gameRepository.currentState()
        android.util.Log.d("TRIBUNAL_DEBUG", "Full State: $state")
        addLog("DEBUG: State dumped to logcat")
    }

    fun startQuest(id: String) {
        gameRepository.updateState { state ->
            questEngine.activateQuestDirect(state, id)
        }
        refreshInfo(id)
    }

    fun stepSuccess(id: String) {
        gameRepository.updateState { state ->
            questEngine.advanceStepDirect(state, id)
        }
        refreshInfo(id)
    }

    fun stepFail(id: String) {
        addLog("Manual step fail triggered for $id")
    }

    fun resetQuest(id: String) {
        addLog("Reset requested for $id - Engine 2.0 does not support raw reset yet.")
    }

    private fun refreshInfo(id: String) {
        val def = questEngine.getDefinition(id)
        val status = questEngine.getStatus(id)
        _currentQuestInfo.value = "ID: $id | Title: ${def?.title} | Status: $status"
    }
}
