package com.grimreich.core

import androidx.compose.ui.unit.dp

object GameConstants {
    // Economics
    const val INITIAL_GOLD = 100
    const val HIRE_HERO_COST = 50
    const val REROLL_RECRUITS_COST = 10
    const val DEFAULT_QUEST_REWARD = 100
    const val BEGGAR_GIFT_COST = 5
    const val DEV_GOLD_GIFT = 500
    const val CHURCH_OFFERING_COST = 50

    // Party & Heroes
    const val MAX_QUEST_POOL_SIZE = 5
    const val MAX_RECRUITS_POOL_SIZE = 3
        const val MAX_PARTY_SIZE = 6
    const val STARTING_HERO_AGE = 25
    const val DEFAULT_ATTRIBUTE_VALUE = 10
    const val HP_PER_ENDURANCE = 2
    const val HP_BASE_BONUS = 20
    const val ZEALOT_SACRIFICE_HP_LOSS = 5

    // World Stability Thresholds
    const val STABILITY_THRESHOLD_HIGH = 70
    const val STABILITY_THRESHOLD_LOW = 30
    
    // UI Constraints
    const val MAX_LOG_ENTRIES = 100

    object UI {
        val PADDING_SMALL = 8.dp
        val PADDING_MEDIUM = 16.dp
        val PADDING_LARGE = 24.dp
        val PADDING_HUGE = 32.dp

        val ICON_SMALL = 24.dp
        val ICON_MEDIUM = 48.dp
        val ICON_LARGE = 64.dp

        val BUTTON_HEIGHT_DEFAULT = 50.dp
        val BUTTON_HEIGHT_SMALL = 32.dp

        val CARD_ELEVATION = 4.dp
        val BORDER_WIDTH = 1.dp
    }
}
