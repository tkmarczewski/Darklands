package com.grimreich.core

import com.grimreich.grimreich.v1.*
import com.grimreich.core.mutations.Mutation

data class Hero(
    var id: String = "",
    var name: String = "",
    var age: Int = 25,

    // Atrybuty bazowe
    var strength: Int = 10,
    var agility: Int = 10,
    var perception: Int = 10,
    var intelligence: Int = 10,
    var endurance: Int = 10,
    var charisma: Int = 10,
    var piety: Int = 10,

    // Statystyki pochodne i zasoby
    var virtue: Int = 50,
    var divineFavor: Int = 0,
    var sanity: Int = 100,
    var corruption: Int = 0,
    var morale: Int = 80,

    // Postęp
    var level: Int = 1,
    var xp: Int = 0,
    var attributePoints: Int = 0,
    var portraitRes: String = "port_knight",  // Default portrait

    // Punkty życia
    var hp: Int = 40,
    var maxHp: Int = 40,
    var isDead: Boolean = false,

    // Kariera
    var currentCareer: Career? = null,
    var careerHistory: MutableList<CareerEntry> = mutableListOf(),

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
    val activeMutations: MutableList<Mutation> = mutableListOf(),
    val passiveAbilities: MutableSet<String> = mutableSetOf()
) {

    /**
     * Normalizes mutable fields to valid ranges.
     * Called after every state mutation to ensure invariants.
     */
    fun normalize() {
        // Dynamic Max HP calculation based on Endurance
        val oldMaxHp = maxHp
        maxHp = effectiveEndurance() * GameConstants.HP_PER_ENDURANCE + GameConstants.HP_BASE_BONUS
        
        // If max HP increased (e.g. via stat upgrade), grant the same amount of current HP
        if (maxHp > oldMaxHp && !isDead) {
            hp += (maxHp - oldMaxHp)
        }

        hp = hp.coerceIn(0, maxHp)
        sanity = sanity.coerceIn(0, 100)
        corruption = corruption.coerceIn(0, 100)
        morale = morale.coerceIn(0, 100)
        divineFavor = divineFavor.coerceIn(0, 150)
        endurance = endurance.coerceAtLeast(0)
    }

    fun effectiveStrength(): Int = strength + (if (currentCareer == Career.MERCENARY || currentCareer == Career.KNIGHT) 2 else 0)
    fun effectiveAgility(): Int = agility + (if (currentCareer == Career.THIEF || currentCareer == Career.ROGUE) 3 else 0)
    fun effectiveIntelligence(): Int = intelligence + (if (currentCareer == Career.SCHOLAR || currentCareer == Career.ALCHEMIST) 4 else 0)
    fun effectiveEndurance(): Int = endurance + (if (currentCareer == Career.MERCENARY || currentCareer == Career.GUARD) 2 else 0)

    fun getEquipmentBonus(statName: String, allItems: List<Item>): Int {
        var bonus = 0
        equipment.values.filterNotNull().forEach { itemId ->
            val item = allItems.find { it.instanceId == itemId }
            item?.effects?.get(statName)?.let { bonus += it }
        }
        return bonus
    }

    fun effectiveAttack(allItems: List<Item>): Int {
        return effectiveStrength() + getEquipmentBonus("attack", allItems)
    }

    fun effectiveDefense(allItems: List<Item>): Int {
        return effectiveAgility() + getEquipmentBonus("defense", allItems)
    }

    fun effectiveArmor(allItems: List<Item>): Int {
        return getEquipmentArmor(allItems)
    }

    private fun getEquipmentArmor(allItems: List<Item>): Int {
        var armor = 0
        equipment.values.filterNotNull().forEach { itemId ->
            val item = allItems.find { it.instanceId == itemId }
            item?.effects?.get("armor")?.let { armor += it }
        }
        return armor
    }

    fun deepCopy(): Hero = this.copy(
        careerHistory = this.careerHistory.toMutableList(),
        abilities = this.abilities.toMutableList(),
        skills = this.skills.toMutableMap(),
        activeMutations = this.activeMutations.map { it.copy() }.toMutableList(),
        passiveAbilities = this.passiveAbilities.toMutableSet(),
        equipment = this.equipment.toMutableMap()
    )
}
