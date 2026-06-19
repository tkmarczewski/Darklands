package com.grimreich.systems

import com.grimreich.core.PlayerState
import javax.inject.Inject
import javax.inject.Singleton

data class GrimholdSliceViewData(
    val title: String,
    val description: String,
    val availableQuests: List<String>
)

@Singleton
class GrimholdSliceSystem @Inject constructor(
    private val questSystem: QuestSystem
) {
    fun view(playerState: PlayerState): GrimholdSliceViewData {
        val quests = questSystem.availableForCity("grimhold")
        
        return GrimholdSliceViewData(
            title = "Grimhold - Sektor 4",
            description = "Dzielnica spowita gęstą mgłą.",
            availableQuests = quests.map { it.title }
        )
    }
}
