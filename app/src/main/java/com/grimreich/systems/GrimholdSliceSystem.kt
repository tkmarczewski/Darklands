package com.grimreich.systems

import com.grimreich.core.GrimholdSliceViewData
import com.grimreich.core.PlayerState
import com.grimreich.core.SliceArtwork
import com.grimreich.core.SliceQuestDetail

object GrimholdSliceSystem {
    private val artwork = SliceArtwork(
        cityId = "grimhold",
        backgroundUrl = "https://pplx-res.cloudinary.com/image/upload/pplx_search_images/446eeb623b65ddc779cf24de3e294c2948bc349d.jpg",
        referenceTitle = "Medieval Town at Night",
        sourceLabel = "Reference image for internal playtests"
    )

    fun seed() {
        ExpandedContentSeeder.seed(seed = 34)

        QuestSystem.register(
            QuestEntry(
                id = "quest_grimhold_dock_watch",
                title = "Watch the River Docks",
                description = "Keep eyes on suspicious cargo moving after curfew.",
                cityId = "grimhold",
                originType = QuestOriginType.CITY_EVENT,
                originRefId = "grimhold_dock_watch",
                rewardGold = 85,
                status = QuestStatus.AVAILABLE
            )
        )

        QuestSystem.register(
            QuestEntry(
                id = "quest_grimhold_smuggler_letters",
                title = "Intercept Smuggler Letters",
                description = "Recover encoded letters before the guild learns too much.",
                cityId = "grimhold",
                originType = QuestOriginType.CITY_EVENT,
                originRefId = "grimhold_smuggler_letters",
                rewardGold = 105,
                status = QuestStatus.AVAILABLE
            )
        )

        QuestSystem.register(
            QuestEntry(
                id = "quest_grimhold_night_patrol",
                title = "Join the Night Patrol",
                description = "Walk with the night watch and survive the alleys.",
                cityId = "grimhold",
                originType = QuestOriginType.CITY_EVENT,
                originRefId = "grimhold_night_patrol",
                rewardGold = 70,
                status = QuestStatus.AVAILABLE
            )
        )
    }

    fun view(playerState: PlayerState): GrimholdSliceViewData {
        val quests = QuestSystem.availableForCity("grimhold")
            .filter { it.id.startsWith("quest_grimhold_") }
            .take(5)
            .map {
                SliceQuestDetail(
                    questId = it.id,
                    title = it.title,
                    shortBrief = brief(it.id),
                    rewardGold = it.rewardGold,
                    difficultyLabel = when {
                        it.rewardGold >= 100 -> "Hard"
                        it.rewardGold >= 80 -> "Medium"
                        else -> "Easy"
                    }
                )
            }

        return GrimholdSliceViewData(
            cityId = "grimhold",
            cityTitle = "Grimhold",
            moodText = "Wet cobbles, dockside whispers and merchant paranoia after dark.",
            backgroundUrl = artwork.backgroundUrl,
            referenceTitle = artwork.referenceTitle,
            sourceLabel = artwork.sourceLabel,
            gold = playerState.gold,
            activeQuestId = playerState.activeQuestId,
            quests = quests
        )
    }

    private fun brief(id: String): String = when {
        id.contains("dock_watch") -> "Keep eyes on suspicious cargo moving after curfew."
        id.contains("smuggler_letters") -> "Recover encoded letters before the guild learns too much."
        id.contains("night_patrol") -> "Walk with the night watch and survive the alleys."
        id.contains("delivery") -> "A sealed bundle must reach the right hands before dawn."
        else -> "Local trouble that may hide a bigger conspiracy."
    }
}