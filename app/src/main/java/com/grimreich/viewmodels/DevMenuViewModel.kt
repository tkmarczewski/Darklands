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

    fun triggerEnding(root: com.grimreich.ui.main.GameRootViewModel, type: String) {
        gameRepository.updateState { s ->
            when (type) {
                "TOTAL_COLLAPSE" -> {
                    s.world.collapseProgress = 1.0f
                    s.world.echoIntensity = 0.0f
                }
                "ECHO_DOMINION" -> {
                    s.world.collapseProgress = 0.0f
                    s.world.echoIntensity = 5.0f
                }
                "ABANDONED" -> {
                    s.world.collapseProgress = 0.0f
                    s.world.echoIntensity = 0.0f
                    s.party.forEach { it.hp = 0; it.isDead = true }
                }
                "RESTORATION" -> {
                    s.world.collapseProgress = 0.0f
                    s.world.echoIntensity = 0.0f
                    s.world.globalStability = 100
                    s.quest.completedQuestIds.add("q_1")
                }
                "OBSERVED" -> {
                    s.world.collapseProgress = 0.0f
                    s.world.echoIntensity = 0.0f
                    s.metaAwarenessLevel = 5
                    s.quest.completedQuestIds.add("q_meta_7")
                }
                "GOOD" -> {
                    s.world.collapseProgress = 0.0f
                    s.world.echoIntensity = 0.0f
                    s.prayer.faith = 100
                    s.prayer.virtue = 100
                    s.world.globalStability = 100
                    s.prayer.sins = 0
                }
                "PRAGMATIC" -> {
                    s.world.collapseProgress = 0.0f
                    s.world.echoIntensity = 0.0f
                    s.prayer.faith = 40
                    s.world.globalStability = 50
                    s.prayer.virtue = 10
                    s.prayer.sins = 1
                }
                "CORRUPTED" -> {
                    s.world.collapseProgress = 0.0f
                    s.world.echoIntensity = 0.0f
                    s.world.globalStability = 10
                    s.prayer.sins = 20
                }
                "REDEMPTION" -> {
                    s.world.collapseProgress = 0.0f
                    s.world.echoIntensity = 0.0f
                    s.prayer.faith = 10
                    s.world.globalStability = 35
                    s.prayer.sins = 5
                }
            }
        }
        root.setMode(com.grimreich.ui.main.GameScreenMode.ending)
    }

    private fun refreshInfo(id: String) {
        val def = questEngine.getDefinition(id)
        val status = questEngine.getStatus(id)
        _currentQuestInfo.value = "ID: $id | Title: ${def?.title} | Status: $status"
    }
}
