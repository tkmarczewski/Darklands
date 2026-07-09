package com.grimreich.core

import com.grimreich.grimreich.v1.Item

/**
 * Bestiariusz i encountery z Grimreich: pełna lista wrogów ze statystykami,
 * AI, loot tables i definicje encounterów bojowych.
 */

// ────────── ENEMY TYPES ──────────────────────────────────────────────────────
enum class EnemyType {
    // Ludzcy
    BANDIT, BANDIT_LEADER, CITY_GUARD,
    RAUBRITTER_SOLDIER, RAUBRITTER_KNIGHT, RAUBRITTER_BOSS,
    MERCENARY,
    // Kult
    CULTIST, CULTIST_PRIEST, DEMON_MINOR, DEMON_MAJOR,
    // Nieumarli
    SKELETON, SKELETON_WARRIOR, ZOMBIE, GHOST,
    // Zwierzęta
    WOLF, WOLF_PACK_LEADER, WILD_BOAR,
    // Specjalne
    WITCH, DRAGON
}

enum class EnemyAI {
    AGGRESSIVE,  // atakuje zawsze
    DEFENSIVE,   // broni się, nie ściga
    TACTICAL,    // sprytny, ucieka gdy przegrywa
    BERSERK,     // walczy do śmierci
    RANGED       // preferuje dystans
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
    val itemChances: Map<String, Float> = emptyMap()
)

// ────────── BESTIARY CATALOG ─────────────────────────────────────────────────
object Bestiary {
    private val enemies = mutableMapOf<EnemyType, Enemy>()

    fun loadFromList(loadedEnemies: List<Enemy>) {
        loadedEnemies.forEach { enemies[it.type] = it }
    }

    fun get(type: EnemyType): Enemy = enemies[type] ?: Enemy(
        type      = EnemyType.BANDIT,
        name      = "Błąd Rzeczywistości (Bandyta)",
        stats     = EnemyStats(maxHp = 25, attack = 15, defense = 10, speed = 12, morale = 8),
        ai        = EnemyAI.AGGRESSIVE,
        lootTable = LootTable(goldMin = 5, goldMax = 20),
        xpReward  = 10
    )

    fun scaleToLevel(enemy: Enemy, level: Int, echoIntensity: Float = 0f): Enemy {
        val multiplier = 1.0f + (level - 1) * 0.1f + (echoIntensity * 0.5f)
        return enemy.copy(
            stats = enemy.stats.copy(
                maxHp  = (enemy.stats.maxHp  * multiplier).toInt(),
                attack = (enemy.stats.attack  * multiplier).toInt(),
                defense= (enemy.stats.defense * multiplier).toInt()
            ),
            xpReward = (enemy.xpReward * multiplier).toInt()
        )
    }
}

// ────────── ENCOUNTER DEFINITIONS ───────────────────────────────────────────
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
                EnemyType.BANDIT, EnemyType.BANDIT, EnemyType.BANDIT, EnemyType.BANDIT_LEADER
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
                EnemyType.WOLF, EnemyType.WOLF, EnemyType.WOLF, EnemyType.WOLF_PACK_LEADER
            ),
            difficulty = 2
        ),
        "combat_undead" to BattleEncounter(
            id = "combat_undead",
            name = "Szkielety",
            enemies = listOf(
                EnemyType.SKELETON, EnemyType.SKELETON, EnemyType.SKELETON, EnemyType.SKELETON_WARRIOR
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
                EnemyType.RAUBRITTER_SOLDIER, EnemyType.RAUBRITTER_SOLDIER, EnemyType.RAUBRITTER_KNIGHT
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

    fun get(id: String): BattleEncounter {
        val encounter = encounters[id]
        if (encounter == null) {
            android.util.Log.e("EncounterCatalog", "UNRESOLVED ENCOUNTER ID: $id - Fallback to Paradigm Error")
        }
        return encounter ?: BattleEncounter(
            id         = "error_fallback",
            name       = "Błąd Paradygmatu",
            enemies    = listOf(EnemyType.BANDIT),
            difficulty = 1
        )
    }

    fun all(): List<BattleEncounter> = encounters.values.toList()
}
