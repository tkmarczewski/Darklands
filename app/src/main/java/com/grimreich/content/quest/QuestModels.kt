package com.grimreich.content.quest

data class QuestEvent(
    val id: String,
    val description: String,
    val nextEventIds: List<String> = emptyList()
) {
    init {
        require(id.isNotBlank()) { "ID cannot be blank" }
    }
}

data class QuestRewards(
    val gold: Int = 0,
    val virtue: Int = 0,
    val reputation: Int = 0
) {
    init {
        require(gold >= 0) { "Gold cannot be negative" }
    }
}

data class QuestEnding(
    val id: String,
    val description: String,
    val requirementEvents: List<String>
) {
    init {
        require(id.isNotBlank()) { "ID cannot be blank" }
    }
}

data class QuestChain(
    val id: String,
    val name: String,
    val startingRegion: String,
    val events: List<QuestEvent>,
    val rewards: QuestRewards,
    val endings: List<QuestEnding>
) {
    init {
        require(events.isNotEmpty()) { "Events cannot be empty" }
        require(endings.isNotEmpty()) { "Endings cannot be empty" }
    }
}

enum class RumorSource {
    TAVERN, CHURCH, MARKET, NOBILITY, UNDERWORLD
}

data class Rumor(
    val id: String,
    val text: String,
    val region: String,
    val sourceType: RumorSource,
    val veracity: Float = 0.5f,
    val linkedQuestId: String? = null
) {
    init {
        require(id.isNotBlank()) { "ID cannot be blank" }
        require(text.isNotBlank()) { "Text cannot be blank" }
        require(veracity in 0f..1f) { "Veracity must be between 0 and 1" }
    }
}
