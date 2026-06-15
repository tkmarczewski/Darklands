package com.grimreich.core

/**
 * Centralny katalog stałych gry Grimreich.
 * Wydzielenie tych wartości pozwala na łatwiejsze balansowanie rozgrywki
 * i eliminuje ostrzeżenia o "magicznych liczbach".
 */
object GrimConstants {
    
    object Combat {
        const val BASE_DODGE_CHANCE = 0.05f
        const val AGILITY_DODGE_MODIFIER = 0.02f
        const val CRITICAL_HIT_MULTIPLIER = 1.5f
        
        const val STATUS_CHANCE_BASE = 0.1f
        const val STATUS_CHANCE_INT_MOD = 0.03f
        
        const val WOUND_THRESHOLD_LIGHT = 0.2f
        const val WOUND_THRESHOLD_SERIOUS = 0.4f
        
        const val MORALE_HEROIC_THRESHOLD = 80
        const val MORALE_STEADY_THRESHOLD = 50
        const val MORALE_SHAKEN_THRESHOLD = 30
        const val MORALE_PANICKED_THRESHOLD = 10
        
        const val KILL_MORALE_BONUS = 15
        const val FLEE_MORALE_PENALTY = 20
        const val MAX_MORALE = 100
        
        const val POST_COMBAT_HEAL_HP_MIN = 5
        const val POST_COMBAT_HEAL_HP_MAX = 20
        const val HP_RECOVERY_RATIO = 0.1f
    }
    
    object Economy {
        const val SELL_PRICE_MULTIPLIER = 0.4f
        const val BASE_ITEM_VALUE = 10
        const val QUEST_REWARD_GOLD_STANDARD = 50
        const val QUEST_REWARD_GOLD_CROWN = 150
        const val QUEST_REWARD_GOLD_FOREST = 200
        const val QUEST_REWARD_GOLD_ENDGAME_LIGHT = 80
        const val QUEST_REWARD_GOLD_ENDGAME_MID = 120
        const val QUEST_REWARD_GOLD_ENDGAME_HEAVY = 200
    }
    
    object Character {
        const val STARTING_POINTS = 20
        const val MIN_ATTRIBUTE_VALUE = 10
        const val MAX_ATTRIBUTE_VALUE = 20
        const val SKILL_SPECIALIZATION_REQUIRED = 3
        const val SPECIALIZED_SKILL_BASE_VALUE = 30
    }
    
    object World {
        const val NPC_GENERATION_SEED_OFFSET = 123
        const val ECHO_MANIFESTATION_THRESHOLD = 80
        const val ECHO_MAX_CHANCE = 0.5f
        const val STABILITY_CRITICAL_THRESHOLD = 20
    }

    object Narrative {
        const val GLITCH_CHANCE_BASE = 0.25f
        const val ECHO_GIFT_GOLD = 50
    }

    object Time {
        const val HOURS_IN_DAY = 24
        const val MORNING_START = 6
        const val MIDDAY_START = 12
        const val AFTERNOON_START = 15
        const val DUSK_START = 18
        const val EVENING_START = 21
        const val DEEP_NIGHT_START = 3
    }
}
