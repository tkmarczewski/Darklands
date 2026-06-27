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
    private val questEngine: QuestEngine
) {
    fun view(playerState: PlayerState): GrimholdSliceViewData {
        return GrimholdSliceViewData(
            title = "Grimhold",
            description = "Status systemu: Stabilny.",
            availableQuests = questEngine.getActiveQuestsForCity("grimhold").map { it.title }
        )
    }
}
