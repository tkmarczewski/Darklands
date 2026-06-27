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
    private val questEngine: QuestEngine
) : ViewModel() {

    private val _logEntries = MutableStateFlow<List<String>>(emptyList())
    val logEntries: StateFlow<List<String>> = _logEntries.asStateFlow()

    private val _currentQuestInfo = MutableStateFlow("")
    val currentQuestInfo: StateFlow<String> = _currentQuestInfo.asStateFlow()

    fun addLog(msg: String) {
        _logEntries.value = _logEntries.value + msg
    }

    fun startQuest(id: String) {
        questEngine.activateQuest(id)
        refreshInfo(id)
    }

    fun stepSuccess(id: String) {
        questEngine.advanceStep(id)
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
