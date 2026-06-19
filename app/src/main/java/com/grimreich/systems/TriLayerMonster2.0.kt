package com.grimreich.systems

import javax.inject.Inject
import javax.inject.Singleton

enum class Layer { SPAWN, ELITE, MINIBOSS }

data class TriLayerMonster(
    val monsterId: Int,
    val name: String,
    val layer: Layer,
    var baseHealth: Float
)

@Singleton
class TriLayerMonster2_0 @Inject constructor() {
    private val monsters = mutableMapOf<Int, TriLayerMonster>()

    fun initialize() {
        monsters.clear()
    }

    fun getMonster(id: Int): TriLayerMonster? = monsters[id]
}
