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
    const val MAX_PARTY_SIZE = 4
    const val STARTING_HERO_AGE = 25
    const val DEFAULT_ATTRIBUTE_VALUE = 10
    const val HP_PER_ENDURANCE = 2
    const val HP_BASE_BONUS = 20
    const val ZEALOT_SACRIFICE_HP_LOSS = 5

    // World Stability Thresholds
    const val STABILITY_THRESHOLD_HIGH = 70
    const val STABILITY_THRESHOLD_LOW = 30
    const val STABILITY_THRESHOLD_CRITICAL = 20
    const val STABILITY_GLITCH_THRESHOLD = 40
    const val STABILITY_ATMOSPHERE_GLITCH = 25
    const val STABILITY_MIN_FOR_RECOVERY = 20
    
    // Progression & Meta
    const val XP_PER_LEVEL_BASE = 100
    const val ATTR_POINTS_PER_LEVEL = 2
    const val META_QUEST_THRESHOLD = 30
    const val MAX_QUEST_ADVANCE_SAFETY = 50
    const val MIN_LEVEL = 1
    const val COLLAPSE_TOTAL_THRESHOLD = 0.999f
    const val ONTOLOGICAL_MASS_ELITE_THRESHOLD = 50

    // Economy & Factions
    const val BASE_REPUTATION_BUY_MODIFIER = 1.0f
    const val REPUTATION_MODIFIER_STEP = 0.02f
    const val MIN_BUY_PRICE = 1
    const val MIN_SELL_PRICE = 1
    const val HOSTILE_REPUTATION_THRESHOLD = -50
    const val ANOMALY_TINT_ALPHA = 0x66
    const val DEFAULT_STABILITY_INC = 5

    // Collapse Deltas
    const val COLLAPSE_DELTA_DAY = 0.05f
    const val COLLAPSE_DELTA_TRAVEL_DIVISOR = 1000f
    const val COLLAPSE_DELTA_QUEST_FAIL = 0.03f
    const val COLLAPSE_DELTA_RITUAL = 0.10f

    // UI Constraints
    const val MAX_LOG_ENTRIES = 100
    const val LATEST_LOGS_DISPLAY_COUNT = 19
    const val GLITCH_CHANCE_LOW_STABILITY = 0.15f
    const val FACTION_RAID_CHANCE = 0.2f
    const val ENCOUNTER_CHANCE = 0.3f

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
