package com.darklandsmobile.systems

import com.darklandsmobile.core.Enemy
import com.darklandsmobile.core.Segment

/**
 * TriLayerMonster2.0 - System trzech warstw dla potworow w GrimReich.
 * Warstwa 1: Spawn/Mob podstawowy
 * Warstwa 2: Elite/Champion
 * Warstwa 3: Mini-boss/Regional boss
 */
data class TriLayerMonster(
    val monsterId: Int,
    val name: String,
    val layer: Layer,
    val baseHealth: Float,
    val baseDamage: Float,
    val aiBehavior: AIWeightedBehavior,
    val lootTable: LootTable,
    val mutation: MonsterMutation? = null,
    val isUnique: Boolean = false,
    val spawnWeight: Float = 0.5f
)

enum class Layer {
    SPAWN,      // Warstwa 1: Podstawowe moby
    ELITE,      // Warstwa 2: Zdywersyfikowane elity
    MINIBOSS    // Warstwa 3: Minibossy
}

data class AIWeightedBehavior(
    val aggression: Float = 0.5f,
    val defense: Float = 0.3f,
    val mobility: Float = 0.2f,
    val magicCaster: Float = 0.0f
)

data class LootTable(
    val goldMin: Int = 10,
    val goldMax: Int = 50,
    val itemDropChance: Float = 0.15f,
    val specialDropChance: Float = 0.02f,
    val uniqueItemChance: Float = 0.005f
)

object TriLayerMonster2_0 {

    private val monsters = mutableMapOf<Int, TriLayerMonster>()

    fun initialize() {
        monsters.clear()
    }

    fun addMonster(monster: TriLayerMonster) {
        monsters[monster.monsterId] = monster
    }

    fun createSpawnMonster(
        id: Int,
        name: String,
        health: Float = 100.0f,
        damage: Float = 20.0f,
        behavior: AIWeightedBehavior = AIWeightedBehavior(aggression = 0.6f)
    ): TriLayerMonster {
        val monster = TriLayerMonster(
            monsterId = id,
            name = name,
            layer = Layer.SPAWN,
            baseHealth = health,
            baseDamage = damage,
            aiBehavior = behavior,
            lootTable = LootTable(goldMin = 5, goldMax = 25)
        )
        monsters[id] = monster
        return monster
    }

    fun createEliteMonster(
        id: Int,
        name: String,
        health: Float = 300.0f,
        damage: Float = 50.0f,
        behavior: AIWeightedBehavior = AIWeightedBehavior(
            aggression = 0.7f,
            defense = 0.5f
        )
    ): TriLayerMonster {
        val monster = TriLayerMonster(
            monsterId = id,
            name = name,
            layer = Layer.ELITE,
            baseHealth = health,
            baseDamage = damage,
            aiBehavior = behavior,
            lootTable = LootTable(
                goldMin = 30,
                goldMax = 100,
                itemDropChance = 0.3f,
                specialDropChance = 0.1f
            ),
            spawnWeight = 0.2f
        )
        monsters[id] = monster
        return monster
    }

    fun createMiniboss(
        id: Int,
        name: String,
        health: Float = 800.0f,
        damage: Float = 100.0f,
        behavior: AIWeightedBehavior = AIWeightedBehavior(
            aggression = 0.8f,
            defense = 0.6f,
            mobility = 0.4f
        )
    ): TriLayerMonster {
        val monster = TriLayerMonster(
            monsterId = id,
            name = name,
            layer = Layer.MINIBOSS,
            baseHealth = health,
            baseDamage = damage,
            aiBehavior = behavior,
            lootTable = LootTable(
                goldMin = 100,
                goldMax = 500,
                itemDropChance = 0.5f,
                specialDropChance = 0.25f,
                uniqueItemChance = 0.05f
            ),
            isUnique = true,
            spawnWeight = 0.05f
        )
        monsters[id] = monster
        return monster
    }

    fun getMonstersByLayer(layer: Layer): List<TriLayerMonster> =
        monsters.filter { it.value.layer == layer }.values.toList()

    fun getRandomSpawnMonster(): TriLayerMonster? {
        val spawns = getMonstersByLayer(Layer.SPAWN)
        return spawns.randomOrNull()
    }

    fun getRandomElite(): TriLayerMonster? {
        val elites = getMonstersByLayer(Layer.ELITE)
        return elites.randomOrNull()
    }

    fun getMiniboss(): TriLayerMonster? {
        val minibosses = getMonstersByLayer(Layer.MINIBOSS)
        return minibosses.firstOrNull { !it.isUnique } ?: minibosses.randomOrNull()
    }

    fun applyMutation(monsterId: Int, mutation: MonsterMutation) {
        val monster = monsters[monsterId] ?: return
        val grimMut = monster.copy(
            baseHealth = monster.baseHealth * MutacjePotworow2_0.getHealthMultiplier(mutation),
            baseDamage = monster.baseDamage * MutacjePotworow2_0.getDamageMultiplier(mutation),
            mutation = mutation
        )
        monsters[monsterId] = grimMut
    }

    fun getAllMonsters(): Collection<TriLayerMonster> = monsters.values
    fun getMonster(monsterId: Int): TriLayerMonster? = monsters[monsterId]
}
