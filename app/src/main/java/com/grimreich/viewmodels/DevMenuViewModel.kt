package com.grimreich.viewmodels

import androidx.lifecycle.ViewModel
import com.grimreich.systems.ExpeditionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class DevMenuViewModel @Inject constructor(
    private val expeditionManager: ExpeditionManager
) : ViewModel() {

    private val _logEntries = MutableStateFlow<List<String>>(emptyList())
    val logEntries: StateFlow<List<String>> = _logEntries

    private val _currentQuestInfo = MutableStateFlow("—")
    val currentQuestInfo: StateFlow<String> = _currentQuestInfo

        private fun addLog(entry: String) {
        val updated = (_logEntries.value + entry).takeLast(100)
        _logEntries.value = updated
    }

    fun startQuest(questId: String) {
        val result = expeditionManager.startQuest(questId)
                    addLog("START $questId → $result")
        refreshInfo(questId)
    }

    fun stepSuccess(questId: String) {
        val result = expeditionManager.onStepFinished(questId, success = true)
                    addLog("STEP ✓ $questId → $result")
        refreshInfo(questId)
    }

    fun stepFail(questId: String) {
        val result = expeditionManager.onStepFinished(questId, success = false)
                    addLog("STEP ✗ $questId → $result")
        refreshInfo(questId)
    }

    fun resetQuest(questId: String) {
        expeditionManager.resetProgress(questId)
                    addLog("RESET $questId")
        _currentQuestInfo.value = "—"
    }

    private fun refreshInfo(questId: String) {
        _currentQuestInfo.value = expeditionManager.getStepInfo(questId)
    }
}
