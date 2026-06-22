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

    fun startQuest(questId: String) {
        val result = expeditionManager.startQuest(questId)
        _logEntries.value = _logEntries.value + "START $questId → $result"
        refreshInfo(questId)
    }

    fun stepSuccess(questId: String) {
        val result = expeditionManager.onStepFinished(questId, success = true)
        _logEntries.value = _logEntries.value + "STEP ✓ $questId → $result"
        refreshInfo(questId)
    }

    fun stepFail(questId: String) {
        val result = expeditionManager.onStepFinished(questId, success = false)
        _logEntries.value = _logEntries.value + "STEP ✗ $questId → $result"
        refreshInfo(questId)
    }

    fun resetQuest(questId: String) {
        expeditionManager.resetProgress(questId)
        _logEntries.value = _logEntries.value + "RESET $questId"
        _currentQuestInfo.value = "—"
    }

    private fun refreshInfo(questId: String) {
        _currentQuestInfo.value = expeditionManager.getStepInfo(questId)
    }
}
