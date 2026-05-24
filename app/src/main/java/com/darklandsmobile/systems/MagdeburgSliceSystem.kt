package com.darklandsmobile.systems

import com.darklandsmobile.core.MagdeburgSliceViewData
import com.darklandsmobile.core.PlayerState
import com.darklandsmobile.core.SliceArtwork
import com.darklandsmobile.core.SliceQuestDetail

object MagdeburgSliceSystem {
    private val artwork = SliceArtwork(
        cityId = "magdeburg",
        backgroundUrl = "https://pplx-res.cloudinary.com/image/upload/pplx_search_images/446eeb623b65ddc779cf24de3e294c2948bc349d.jpg",
        referenceTitle = "Medieval Town at Night",
        sourceLabel = "Reference image for internal playtests"
    )

    fun seed() {
        ExpandedContentSeeder.seed(seed = 34)
        QuestSystem.register(
            QuestEntry(
                id = "quest_magdeburg_dock_watch",
                title = "Watch the River Docks",
                cityId = "magdeburg",
                rewardGold = 85,
                status = QuestStatus.AVAILABLE
            )
        )
        QuestSystem.register(
            QuestEntry(
                id = "quest_magdeburg_smuggler_letters",
                title = "Intercept Smuggler Letters",
                cityId = "magdeburg",
                rewardGold = 105,
                status = QuestStatus.AVAILABLE
            )
        )
        QuestSystem.register(
            QuestEntry(
                id = "quest_magdeburg_night_patrol",
                title = "Join the Night Patrol",
                cityId = "magdeburg",
                rewardGold = 70,
                status = QuestStatus.AVAILABLE
            )
        )
    }

    fun view(playerState: PlayerState): MagdeburgSliceViewData {
        val quests = QuestSystem.availableForCity("magdeburg")
            .filter { it.id.startsWith("quest_magdeburg_") }
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

        return MagdeburgSliceViewData(
            cityId = "magdeburg",
            cityTitle = "Magdeburg",
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
