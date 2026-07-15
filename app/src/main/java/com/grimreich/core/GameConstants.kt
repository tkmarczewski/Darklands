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
    const val MAX_LEVEL = 10
    const val COLLAPSE_TOTAL_THRESHOLD = 0.999f
    const val ONTOLOGICAL_MASS_ELITE_THRESHOLD = 50
    const val TRAUMA_CHANCE_ELITE = 0.25f
    const val TRAUMA_CHANCE_MASSIVE = 0.15f
    const val TRAUMA_CHANCE_UNSTABLE = 0.10f
    const val TRAUMA_CHANCE_BASE = 0.02f
    const val TRAUMA_STABILITY_LOSS = 10f

    // Economy & Factions
    const val BASE_REPUTATION_BUY_MODIFIER = 1.0f
    const val REPUTATION_MODIFIER_STEP = 0.02f
    const val MIN_BUY_PRICE = 1
    const val MIN_SELL_PRICE = 1
    const val HOSTILE_REPUTATION_THRESHOLD = -50
    const val ANOMALY_TINT_ALPHA = 0x66
    const val DEFAULT_STABILITY_INC = 5
    const val SKILL_STABILITY_LOSS_HEAVY = 10

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

    /**
     * Stałe mechaniki walki (skonsolidowane z GameConstants).
     */
    object Combat {
        const val BASE_DODGE_CHANCE = 0.1f
        const val AGILITY_DODGE_MODIFIER = 0.01f
        const val CRITICAL_HIT_MULTIPLIER = 2.0f
        const val PERCEPTION_CRIT_MODIFIER = 0.02f
        const val CHARISMA_MORALE_REGEN = 5
        const val PIETY_SKILL_SCALING = 0.5f

        const val STATUS_CHANCE_BASE = 0.15f
        const val STATUS_CHANCE_INT_MOD = 0.01f

        const val WOUND_THRESHOLD_LIGHT = 0.4f
        const val WOUND_THRESHOLD_SERIOUS = 0.2f

        const val MORALE_HEROIC_THRESHOLD = 90
        const val MORALE_STEADY_THRESHOLD = 60
        const val MORALE_SHAKEN_THRESHOLD = 40
        const val MORALE_PANICKED_THRESHOLD = 20

        const val KILL_MORALE_BONUS = 20
        const val FLEE_MORALE_PENALTY = 30
        const val MAX_MORALE = 100

        const val POST_COMBAT_HEAL_HP_MIN = 5
        const val POST_COMBAT_HEAL_HP_MAX = 15
        const val HP_RECOVERY_RATIO = 0.25f
    }

    /**
     * Stałe ekonomii i handlu (skonsolidowane z GameConstants).
     */
    object Economy {
        const val SELL_PRICE_MULTIPLIER = 0.6f
        const val BASE_ITEM_VALUE = 10
        const val QUEST_REWARD_GOLD_STANDARD = 50
        const val QUEST_REWARD_GOLD_CROWN = 150
        const val QUEST_REWARD_GOLD_FOREST = 80
        const val QUEST_REWARD_GOLD_ENDGAME_LIGHT = 200
        const val QUEST_REWARD_GOLD_ENDGAME_MID = 500
        const val QUEST_REWARD_GOLD_ENDGAME_HEAVY = 1200
    }

    /**
     * Stałe czasu (skonsolidowane).
     */
    object Time {
        const val HOURS_IN_DAY = 24
        const val MORNING_START = 6
        const val MIDDAY_START = 12
        const val AFTERNOON_START = 15
        const val DUSK_START = 18
        const val EVENING_START = 20
        const val DEEP_NIGHT_START = 23
    }

    /**
     * Stałe rozwoju bohatera.
     */
    object Character {
        const val STARTING_POINTS = 20
        const val MIN_ATTRIBUTE_VALUE = 5
        const val MAX_ATTRIBUTE_VALUE = 20
        const val SKILL_SPECIALIZATION_REQUIRED = 3
        const val SPECIALIZED_SKILL_BASE_VALUE = 20
    }

    /**
     * Stałe świata gry.
     */
    object World {
        const val NPC_GENERATION_SEED_OFFSET = 123
        const val ECHO_MANIFESTATION_THRESHOLD = 40
        const val ECHO_MAX_CHANCE = 0.25f
        const val STABILITY_CRITICAL_THRESHOLD = 20
    }

    /**
     * Stałe narracji i glitchy.
     */
    object Narrative {
        const val GLITCH_CHANCE_BASE = 0.05f
        const val ECHO_GIFT_GOLD = 100
    }
}
