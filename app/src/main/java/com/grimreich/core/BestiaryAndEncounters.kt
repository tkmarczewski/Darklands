package com.grimreich.core

import com.grimreich.grimreich.v1.Item
import kotlinx.serialization.Serializable

/**
 * Bestiariusz i encountery z Grimreich: pełna lista wrogów ze statystykami,
 * AI, loot tables i definicje encounterów bojowych.
 */

// ────────── ENEMY TYPES ──────────────────────────────────────────────────────
@Serializable
enum class EnemyType {
    // Ludzcy
    bandit, bandit_leader, city_guard,
    raubritter_soldier, raubritter_knight, raubritter_boss,
    mercenary,
    // Kult
    cultist, cultist_priest, demon_minor, demon_major,
    // Nieumarli
    skeleton, skeleton_warrior, zombie, ghost,
    // Zwierzęta
    wolf, wolf_pack_leader, wild_boar,
    // Specjalne
    witch, dragon,
    // Ontologiczne
    past_shade_elite,
    
    // Brakujące typy z questów (Stabilizacja V2.0)
    possessed_statue,
    fallen_priest,
    masked_impostor,
    silent_guardian,
    raven_assassin,
    arch_ritualist,
    ceiling_crawler,
    ghostly_sentinel,
    mind_reflection,
    ink_nightmare,
    ectoplasmic,
    void_wolf,
    paradox_hunter,
    mutant_ghoul,
    vampire_monk,
    golden_colossus,
    dark_rider,
    dream_wraith,
    hidden_demon,
    blood_curse,
    blood_wraith,
    doppelganger,
    winged_hulk,
    steel_wraith
}

@Serializable
enum class EnemyAI {
    aggressive,  // atakuje zawsze
    defensive,   // broni się, nie ściga
    tactical,    // sprytny, ucieka gdy przegrywa
    berserk,     // walczy do śmierci
    ranged       // preferuje dystans
}

@Serializable
data class EnemyStats(
    val maxHp: Int,
    val attack: Int,
    val defense: Int,
    val speed: Int,
    val morale: Int
)

@Serializable
data class Enemy(
    val type: EnemyType,
    val name: String,
    val stats: EnemyStats,
    val ai: EnemyAI,
    val lootTable: LootTable,
    val xpReward: Int,

    // --- ONTOLOGICAL AUDIT: Hierarchia Bytu ---
    val ontologicalMass: Int = 5,
    val ranga: String = "Odrzut" // Odrzut, Naczynie, Dziecko Pęknięcia, etc.
)

@Serializable
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
        type      = EnemyType.bandit,
        name      = "Błąd Rzeczywistości (Bandyta)",
        stats     = EnemyStats(maxHp = 25, attack = 15, defense = 10, speed = 12, morale = 8),
        ai        = EnemyAI.aggressive,
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
                EnemyType.bandit, EnemyType.bandit, EnemyType.bandit, EnemyType.bandit_leader
            ),
            difficulty = 2
        ),
        "combat_alley_thieves" to BattleEncounter(
            id = "combat_alley_thieves",
            name = "Złodzieje w zaułku",
            enemies = listOf(EnemyType.bandit, EnemyType.bandit),
            difficulty = 1
        ),
        "combat_inn_brawl" to BattleEncounter(
            id = "combat_inn_brawl",
            name = "Awantura w karczmie",
            enemies = listOf(
                EnemyType.mercenary, EnemyType.mercenary, EnemyType.bandit
            ),
            difficulty = 1
        ),
        "combat_wolves" to BattleEncounter(
            id = "combat_wolves",
            name = "Wataha wilków",
            enemies = listOf(
                EnemyType.wolf, EnemyType.wolf, EnemyType.wolf, EnemyType.wolf_pack_leader
            ),
            difficulty = 2
        ),
        "combat_undead" to BattleEncounter(
            id = "combat_undead",
            name = "Szkielety",
            enemies = listOf(
                EnemyType.skeleton, EnemyType.skeleton, EnemyType.skeleton, EnemyType.skeleton_warrior
            ),
            difficulty = 2
        ),
        "combat_cultists" to BattleEncounter(
            id = "combat_cultists",
            name = "Kultyści",
            enemies = listOf(
                EnemyType.cultist, EnemyType.cultist, EnemyType.cultist,
                EnemyType.cultist_priest, EnemyType.demon_minor
            ),
            difficulty = 4
        ),
        "combat_raubritter_scouts" to BattleEncounter(
            id = "combat_raubritter_scouts",
            name = "Zwiadowcy raubrittera",
            enemies = listOf(
                EnemyType.raubritter_soldier, EnemyType.raubritter_soldier, EnemyType.raubritter_knight
            ),
            difficulty = 3
        ),
        "combat_raubritter_boss" to BattleEncounter(
            id = "combat_raubritter_boss",
            name = "Raubritter i jego straż",
            enemies = listOf(
                EnemyType.raubritter_soldier, EnemyType.raubritter_soldier,
                EnemyType.raubritter_knight, EnemyType.raubritter_knight,
                EnemyType.raubritter_boss
            ),
            difficulty = 5
        ),
        "combat_dragon" to BattleEncounter(
            id = "combat_dragon",
            name = "Smok",
            enemies = listOf(EnemyType.dragon),
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
            enemies    = listOf(EnemyType.bandit),
            difficulty = 1
        )
    }

    fun all(): List<BattleEncounter> = encounters.values.toList()
}
