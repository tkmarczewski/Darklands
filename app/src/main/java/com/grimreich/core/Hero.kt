package com.grimreich.core

import com.grimreich.core.mutations.Mutation
import com.grimreich.grimreich.v1.Item

data class Hero(
    val id: String,
    val name: String,
    var age: Int,
    // Atrybuty pierwotne (7 atrybutów wg oryginału)
    var strength: Int = 10,
    var agility: Int = 10,
    var perception: Int = 10,  // Postrzeganie — pułapki, skradanie, obserwacja
    var intelligence: Int = 10,
    var endurance: Int = 10,
    var charisma: Int = 10,
    var piety: Int = 10,  // Pobożność

    // Status psychiczny i duchowy
    var virtue: Int = 0,
    var divineFavor: Int = 50,  // DF: 0-150, konieczne do modlitwy do świętych
    var sanity: Int = 100,      // 0-100, spada podczas ekspedycji na Drugą Stronę
    var corruption: Int = 0,    // 0-100, rośnie przy kontaktach z mrokiem
    var morale: Int = 70,       // 0-100, bieżący stan ducha

    // Progresja
    var level: Int = 1,
    var xp: Int = 0,
    var attributePoints: Int = 0,
    var portraitRes: String = "port_knight",  // Default portrait

    // Punkty życia
    var hp: Int = 30,
    var maxHp: Int = 30,
    var isDead: Boolean = false,

    // Kariera
    var currentCareer: Career? = null,
    var careerHistory: MutableList<Career> = mutableListOf(),

    // Cechy
    var trait: Trait? = null,

    // Ekwipunek: 5 slotów zgodnie z Hero.kt default
    val equipment: MutableMap<String, String?> = mutableMapOf(
        "weapon" to null,
        "armor" to null,
        "helmet" to null,
        "shield" to null,
        "accessory" to null
    ),

    val abilities: MutableList<Ability> = mutableListOf(),
    val skills: MutableMap<String, Int> = mutableMapOf(),
    val activeMutations: MutableList<Mutation> = mutableListOf()
) {

    /**
     * Normalizes mutable fields to valid ranges.
     * Called after every state mutation to ensure invariants.
     */
    fun normalize() {
        hp = hp.coerceIn(0, maxHp)
        sanity = sanity.coerceIn(0, 100)
        corruption = corruption.coerceIn(0, 100)
        morale = morale.coerceIn(0, 100)
        divineFavor = divineFavor.coerceIn(0, 150)
        // FIX: endurance was clamped with coerceIn(0, 99) — a magic number with no origin.
        // CombatantState uses coerceAtLeast(0) with no upper bound.
        // Using coerceAtLeast(0) here for consistency.
        endurance = endurance.coerceAtLeast(0)
    }

    /**
     * Returns the equipment bonus for a given stat (e.g. "attack", "defense", "armor").
     * Sum of all equipped items' effects for that stat.
     *
     * @param stat the stat key to look up (e.g. "attack", "defense", "armor")
     * @param items the global inventory list to look up item details
     * @return the sum of all equipped items' bonus for that stat, or 0 if none equipped.
     */
    fun getEquipmentBonus(stat: String, items: List<Item>): Int {
        // FIX: Original code referenced `state.inventory` which was an undefined identifier.
        // The method signature already provides `items` as parameter.
        // Also handles missing items safely (item ID not in inventory).
        return equipment.values
            .filterNotNull()
            .mapNotNull { id -> items.firstOrNull { it.id == id } }
            .sumOf { it.effects[stat] ?: 0 }
    }

    fun effectiveAttack(inventory: List<Item>): Int =
        (strength / 2).coerceAtLeast(1) + getEquipmentBonus("attack", inventory)

    fun effectiveDefense(inventory: List<Item>): Int =
        (agility / 2).coerceAtLeast(1) + getEquipmentBonus("defense", inventory)

    fun effectiveArmor(inventory: List<Item>): Int =
        getEquipmentBonus("armor", inventory)
}
