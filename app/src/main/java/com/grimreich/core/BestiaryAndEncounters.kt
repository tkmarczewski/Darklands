package com.grimreich.core

import com.grimreich.grimreich.v1.Item

/**
 * Bestiariusz i encountery z Grimreich: pełna lista wrogów ze statystykami,
 * AI, loot tables i definicje encounterów bojowych.
 */

// ────────── ENEMY TYPES ────────────────────────────────────────────────────

enum class EnemyType {
    // Ludzcy
    BANDIT,
    BANDIT_LEADER,
    CITY_GUARD,
    RAUBRITTER_SOLDIER,
    RAUBRITTER_KNIGHT,
    RAUBRITTER_BOSS,
    MERCENARY,
    
    // Kult
    CULTIST,
    CULTIST_PRIEST,
    DEMON_MINOR,
    DEMON_MAJOR,
    
    // Nieumarli
    SKELETON,
    SKELETON_WARRIOR,
    ZOMBIE,
    GHOST,
    
    // Zwierzęta
    WOLF,
    WOLF_PACK_LEADER,
    WILD_BOAR,
    
    // Specjalne
    WITCH,
    DRAGON
}

enum class EnemyAI {
    AGGRESSIVE,      // atakuje zawsze
    DEFENSIVE,       // broni się, nie ściga
    TACTICAL,        // sprytny, ucieka gdy przegrywa
    BERSERK,         // walczy do śmierci
    RANGED           // preferuje dystans
}

data class EnemyStats(
    val maxHp: Int,
    val attack: Int,
    val defense: Int,
    val speed: Int,
    val morale: Int
)

data class Enemy(
    val type: EnemyType,
    val name: String,
    val stats: EnemyStats,
    val ai: EnemyAI,
    val lootTable: LootTable,
    val xpReward: Int
)

data class LootTable(
    val goldMin: Int,
    val goldMax: Int,
    val itemChances: Map<Item, Double> = emptyMap()
)

// ────────── BESTIARY CATALOG ──────────────────────────────────────────────

object Bestiary {
    private val enemies = mapOf(
        EnemyType.BANDIT to Enemy(
            type = EnemyType.BANDIT,
            name = "Bandyta",
            stats = EnemyStats(maxHp = 25, attack = 15, defense = 10, speed = 12, morale = 8),
            ai = EnemyAI.AGGRESSIVE,
            lootTable = LootTable(goldMin = 5, goldMax = 20),
            xpReward = 10
        ),
        EnemyType.BANDIT_LEADER to Enemy(
            type = EnemyType.BANDIT_LEADER,
            name = "Herszt bandytów",
            stats = EnemyStats(maxHp = 40, attack = 20, defense = 15, speed = 14, morale = 12),
            ai = EnemyAI.TACTICAL,
            lootTable = LootTable(goldMin = 30, goldMax = 80),
            xpReward = 25
        ),
        EnemyType.CITY_GUARD to Enemy(
            type = EnemyType.CITY_GUARD,
            name = "Strażnik miejski",
            stats = EnemyStats(maxHp = 35, attack = 18, defense = 18, speed = 10, morale = 15),
            ai = EnemyAI.DEFENSIVE,
            lootTable = LootTable(goldMin = 10, goldMax = 30),
            xpReward = 15
        ),
        EnemyType.RAUBRITTER_SOLDIER to Enemy(
            type = EnemyType.RAUBRITTER_SOLDIER,
            name = "Żołnierz raubrittera",
            stats = EnemyStats(maxHp = 45, attack = 22, defense = 20, speed = 11, morale = 14),
            ai = EnemyAI.AGGRESSIVE,
            lootTable = LootTable(goldMin = 20, goldMax = 50),
            xpReward = 20
        ),
        EnemyType.RAUBRITTER_KNIGHT to Enemy(
            type = EnemyType.RAUBRITTER_KNIGHT,
            name = "Rycerz raubrittera",
            stats = EnemyStats(maxHp = 60, attack = 28, defense = 25, speed = 13, morale = 18),
            ai = EnemyAI.TACTICAL,
            lootTable = LootTable(goldMin = 50, goldMax = 120),
            xpReward = 35
        ),
        EnemyType.RAUBRITTER_BOSS to Enemy(
            type = EnemyType.RAUBRITTER_BOSS,
            name = "Raubritter",
            stats = EnemyStats(maxHp = 100, attack = 35, defense = 30, speed = 15, morale = 25),
            ai = EnemyAI.TACTICAL,
            lootTable = LootTable(goldMin = 200, goldMax = 500),
            xpReward = 100
        ),
        EnemyType.CULTIST to Enemy(
            type = EnemyType.CULTIST,
            name = "Kultysta",
            stats = EnemyStats(maxHp = 30, attack = 16, defense = 12, speed = 11, morale = 10),
            ai = EnemyAI.BERSERK,
            lootTable = LootTable(goldMin = 10, goldMax = 30),
            xpReward = 15
        ),
        EnemyType.CULTIST_PRIEST to Enemy(
            type = EnemyType.CULTIST_PRIEST,
            name = "Kapłan kultu",
            stats = EnemyStats(maxHp = 40, attack = 20, defense = 15, speed = 10, morale = 20),
            ai = EnemyAI.RANGED,
            lootTable = LootTable(goldMin = 30, goldMax = 80),
            xpReward = 30
        ),
        EnemyType.DEMON_MINOR to Enemy(
            type = EnemyType.DEMON_MINOR,
            name = "Mały demon",
            stats = EnemyStats(maxHp = 50, attack = 25, defense = 18, speed = 18, morale = 30),
            ai = EnemyAI.AGGRESSIVE,
            lootTable = LootTable(goldMin = 0, goldMax = 10),
            xpReward = 40
        ),
        EnemyType.DEMON_MAJOR to Enemy(
            type = EnemyType.DEMON_MAJOR,
            name = "Wielki demon",
            stats = EnemyStats(maxHp = 120, attack = 40, defense = 30, speed = 20, morale = 50),
            ai = EnemyAI.BERSERK,
            lootTable = LootTable(goldMin = 0, goldMax = 50),
            xpReward = 150
        ),
        EnemyType.SKELETON to Enemy(
            type = EnemyType.SKELETON,
            name = "Szkielet",
            stats = EnemyStats(maxHp = 20, attack = 12, defense = 8, speed = 8, morale = 50),
            ai = EnemyAI.AGGRESSIVE,
            lootTable = LootTable(goldMin = 0, goldMax = 5),
            xpReward = 8
        ),
        EnemyType.SKELETON_WARRIOR to Enemy(
            type = EnemyType.SKELETON_WARRIOR,
            name = "Szkielet wojownika",
            stats = EnemyStats(maxHp = 35, attack = 18, defense = 15, speed = 10, morale = 50),
            ai = EnemyAI.AGGRESSIVE,
            lootTable = LootTable(goldMin = 5, goldMax = 20),
            xpReward = 18
        ),
        EnemyType.WOLF to Enemy(
            type = EnemyType.WOLF,
            name = "Wilk",
            stats = EnemyStats(maxHp = 20, attack = 14, defense = 8, speed = 16, morale = 6),
            ai = EnemyAI.AGGRESSIVE,
            lootTable = LootTable(goldMin = 0, goldMax = 0),
            xpReward = 5
        ),
        EnemyType.WOLF_PACK_LEADER to Enemy(
            type = EnemyType.WOLF_PACK_LEADER,
            name = "Watażak wilków",
            stats = EnemyStats(maxHp = 35, attack = 18, defense = 12, speed = 18, morale = 10),
            ai = EnemyAI.TACTICAL,
            lootTable = LootTable(goldMin = 0, goldMax = 0),
            xpReward = 12
        ),
        EnemyType.WITCH to Enemy(
            type = EnemyType.WITCH,
            name = "Wiedźma",
            stats = EnemyStats(maxHp = 45, attack = 22, defense = 14, speed = 12, morale = 18),
            ai = EnemyAI.RANGED,
            lootTable = LootTable(goldMin = 20, goldMax = 60),
            xpReward = 35
        ),
        EnemyType.DRAGON to Enemy(
            type = EnemyType.DRAGON,
            name = "Smok",
            stats = EnemyStats(maxHp = 200, attack = 50, defense = 40, speed = 16, morale = 50),
            ai = EnemyAI.BERSERK,
            lootTable = LootTable(goldMin = 500, goldMax = 1500),
            xpReward = 300
        )
    )

    fun get(type: EnemyType): Enemy = enemies[type] ?: Enemy(
        type = EnemyType.BANDIT,
        name = "Błąd Rzeczywistości (Bandyta)",
        stats = EnemyStats(maxHp = 25, attack = 15, defense = 10, speed = 12, morale = 8),
        ai = EnemyAI.AGGRESSIVE,
        lootTable = LootTable(goldMin = 5, goldMax = 20),
        xpReward = 10
    )
    
    // New: Scale enemy to specific level or world ontological intensity
    fun scaleToLevel(enemy: Enemy, level: Int, echoIntensity: Float = 0f): Enemy {
        val multiplier = 1.0f + (level - 1) * 0.1f + (echoIntensity * 0.5f)
        return enemy.copy(
            stats = enemy.stats.copy(
                maxHp = (enemy.stats.maxHp * multiplier).toInt(),
                attack = (enemy.stats.attack * multiplier).toInt(),
                defense = (enemy.stats.defense * multiplier).toInt()
            ),
            xpReward = (enemy.xpReward * multiplier).toInt()
        )
    }
}

// ────────── ENCOUNTER DEFINITIONS ─────────────────────────────────────────

data class BattleEncounter(
    val id: String,
    val name: String,
    val enemies: List<EnemyType>,
    val difficulty: Int
)

object EncounterCatalog {
    private val encounters = mapOf(
        "combat_bandits" to BattleEncounter(
            id = "combat_bandits",
            name = "Bandyci",
            enemies = listOf(
                EnemyType.BANDIT, EnemyType.BANDIT, EnemyType.BANDIT,
                EnemyType.BANDIT_LEADER
            ),
            difficulty = 2
        ),
        "combat_alley_thieves" to BattleEncounter(
            id = "combat_alley_thieves",
            name = "Złodzieje w zaułku",
            enemies = listOf(EnemyType.BANDIT, EnemyType.BANDIT),
            difficulty = 1
        ),
        "combat_inn_brawl" to BattleEncounter(
            id = "combat_inn_brawl",
            name = "Awantura w karczmie",
            enemies = listOf(
                EnemyType.MERCENARY, EnemyType.MERCENARY, EnemyType.BANDIT
            ),
            difficulty = 1
        ),
        "combat_wolves" to BattleEncounter(
            id = "combat_wolves",
            name = "Wataha wilków",
            enemies = listOf(
                EnemyType.WOLF, EnemyType.WOLF, EnemyType.WOLF,
                EnemyType.WOLF_PACK_LEADER
            ),
            difficulty = 2
        ),
        "combat_undead" to BattleEncounter(
            id = "combat_undead",
            name = "Szkielety",
            enemies = listOf(
                EnemyType.SKELETON, EnemyType.SKELETON, EnemyType.SKELETON,
                EnemyType.SKELETON_WARRIOR
            ),
            difficulty = 2
        ),
        "combat_cultists" to BattleEncounter(
            id = "combat_cultists",
            name = "Kultyści",
            enemies = listOf(
                EnemyType.CULTIST, EnemyType.CULTIST, EnemyType.CULTIST,
                EnemyType.CULTIST_PRIEST, EnemyType.DEMON_MINOR
            ),
            difficulty = 4
        ),
        "combat_raubritter_scouts" to BattleEncounter(
            id = "combat_raubritter_scouts",
            name = "Zwiadowcy raubrittera",
            enemies = listOf(
                EnemyType.RAUBRITTER_SOLDIER, EnemyType.RAUBRITTER_SOLDIER,
                EnemyType.RAUBRITTER_KNIGHT
            ),
            difficulty = 3
        ),
        "combat_raubritter_boss" to BattleEncounter(
            id = "combat_raubritter_boss",
            name = "Raubritter i jego straż",
            enemies = listOf(
                EnemyType.RAUBRITTER_SOLDIER, EnemyType.RAUBRITTER_SOLDIER,
                EnemyType.RAUBRITTER_KNIGHT, EnemyType.RAUBRITTER_KNIGHT,
                EnemyType.RAUBRITTER_BOSS
            ),
            difficulty = 5
        ),
        "combat_dragon" to BattleEncounter(
            id = "combat_dragon",
            name = "Smok",
            enemies = listOf(EnemyType.DRAGON),
            difficulty = 6
        )
    )

    fun get(id: String): BattleEncounter = encounters[id] ?: BattleEncounter(
        id = "error_fallback",
        name = "Błąd Paradygmatu",
        enemies = listOf(EnemyType.BANDIT),
        difficulty = 1
    )
    fun all(): List<BattleEncounter> = encounters.values.toList()
}
