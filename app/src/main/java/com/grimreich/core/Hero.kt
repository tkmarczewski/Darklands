package com.grimreich.core

import com.grimreich.grimreich.v1.*
import com.grimreich.core.mutations.Mutation

/**
 * Główny model bohatera w świecie Grimreich.
 * 
 * UWAGA DOTYCZĄCA STATYSTYK:
 * - Atrybuty bazowe (np. [strength]) to wartości na stałe przypisane do postaci, 
 *   zwiększane przez awanse i bonusy stałe z profesji ([Career.strBonus]).
 * - Metody "effective" (np. [effectiveStrength]) obliczają ostateczną wartość bojową,
 *   uwzględniając dynamiczne bonusy z aktualnie pełnionej profesji oraz ekwipunku.
 */
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
    val passiveAbilities: MutableSet<String> = mutableSetOf(),
    
    // CAREER MASTERY (Darklands / Iteration 6)
    var isMaster: Boolean = false,
    var masteryTrait: String? = null,

    // --- ONTOLOGICAL AUDIT: Hierarchia Bytu ---
    var subjectType: SubjectType = SubjectType.VESSEL,
    var ontologicalMass: Int = 10,

    // --- SYSTEM TRAUMY (Funkcjonalność A) ---
    val traumaMarks: MutableList<Trauma> = mutableListOf(),
    var ontologicalStability: Float = 100f, // 0 - 100

    // CAREER STATUS EFFECTS (BUG-06)
    val activeStatusEffects: MutableList<StatusEffect> = mutableListOf()
) {

    enum class SubjectType {
        VESSEL,     // Standardowy mieszkaniec L1
        AWAKE,      // Przebudzony (Prorok)
        TRAVELER,   // Inkarnacja Kotwicy
        ANCHOR      // Sama Kotwica (Gracz)
    }

    /**
     * Normalizes mutable fields to valid ranges.
     * Called after every state mutation to ensure invariants.
     */
    fun normalize() {
        // Dynamic Max HP calculation based on Endurance
        val oldMaxHp = maxHp
        maxHp = effectiveEndurance() * GameConstants.HP_PER_ENDURANCE + GameConstants.HP_BASE_BONUS
        
        // Mastery check: 10 years in any career
        checkMastery()

        // If max HP increased (e.g. via stat upgrade), grant the same amount of current HP
        // FIX BUG: Only heal if hero was alive and not at 0 HP
        if (maxHp > oldMaxHp && !isDead && hp > 0) {
            hp += (maxHp - oldMaxHp)
        }

        hp = hp.coerceIn(0, maxHp)
        
        // --- FIX BUG: Death consistency ---
        if (hp <= 0 && !isDead) {
            isDead = true
        }

        sanity = sanity.coerceIn(0, 100)
        corruption = corruption.coerceIn(0, 100)
        morale = morale.coerceIn(0, 100)
        divineFavor = divineFavor.coerceIn(0, 150)
        endurance = endurance.coerceAtLeast(0)
        ontologicalStability = ontologicalStability.coerceIn(0f, 100f)
    }

    private fun checkMastery() {
        if (isMaster) return
        val hasLongService = careerHistory.any { it.yearsServed >= 10 }
        if (hasLongService) {
            isMaster = true
            applyMasteryPerk()
        }
    }

    private fun applyMasteryPerk() {
        masteryTrait = when (currentCareer) {
            Career.KNIGHT -> "mastery_knight"
            Career.MERCENARY -> "mastery_mercenary"
            Career.SCHOLAR -> "mastery_scholar"
            Career.THIEF, Career.ROGUE -> "mastery_thief"
            Career.ALCHEMIST -> "mastery_alchemist"
            Career.PRIEST, Career.INQUISITOR -> "mastery_priest"
            else -> "mastery_generic"
        }
    }

    fun effectiveStrength(): Int {
        var bonus = (if (currentCareer == Career.MERCENARY || currentCareer == Career.KNIGHT) 2 else 0) +
                    (if (isMaster && currentCareer == Career.KNIGHT) 5 else 0)
        traumaMarks.forEach { bonus += it.statModifiers["strength"] ?: 0 }
        return strength + bonus
    }

    fun effectiveAgility(): Int {
        var bonus = (if (currentCareer == Career.THIEF || currentCareer == Career.ROGUE) 3 else 0) +
                    (if (isMaster && (currentCareer == Career.THIEF || currentCareer == Career.ROGUE)) 5 else 0)
        traumaMarks.forEach { bonus += it.statModifiers["agility"] ?: 0 }
        return agility + bonus
    }

    fun effectiveIntelligence(): Int {
        var bonus = (if (currentCareer == Career.SCHOLAR || currentCareer == Career.ALCHEMIST) 4 else 0)
        traumaMarks.forEach { bonus += it.statModifiers["intelligence"] ?: 0 }
        return intelligence + bonus
    }

    fun effectiveEndurance(): Int {
        var bonus = (if (currentCareer == Career.MERCENARY || currentCareer == Career.GUARD) 2 else 0)
        traumaMarks.forEach { bonus += it.statModifiers["endurance"] ?: 0 }
        return endurance + bonus
    }

    fun effectivePerception(): Int {
        var bonus = (if (currentCareer == Career.ROGUE || currentCareer == Career.SCHOLAR) 2 else 0)
        traumaMarks.forEach { bonus += it.statModifiers["perception"] ?: 0 }
        return perception + bonus
    }

    fun effectivePiety(): Int {
        var bonus = (if (currentCareer == Career.PRIEST || currentCareer == Career.INQUISITOR) 3 else 0)
        traumaMarks.forEach { bonus += it.statModifiers["piety"] ?: 0 }
        return piety + bonus
    }

    fun effectiveCharisma(): Int {
        var bonus = (if (currentCareer == Career.KNIGHT || currentCareer == Career.MERCENARY) 2 else 0)
        traumaMarks.forEach { bonus += it.statModifiers["charisma"] ?: 0 }
        return charisma + bonus
    }

    fun getEquipmentBonus(statName: String, allItems: List<Item>): Int {
        var bonus = 0
        equipment.values.filterNotNull().forEach { itemId ->
            val item = allItems.find { it.instanceId == itemId }
            item?.effects?.get(statName)?.let { bonus += it }
        }
        return bonus
    }

    fun effectiveAttack(allItems: List<Item>): Int {
        var bonus = getEquipmentBonus("attack", allItems)
        traumaMarks.forEach { bonus += it.statModifiers["attack"] ?: 0 }
        return effectiveStrength() + bonus
    }

    fun effectiveDefense(allItems: List<Item>): Int {
        var bonus = getEquipmentBonus("defense", allItems)
        traumaMarks.forEach { bonus += it.statModifiers["defense"] ?: 0 }
        return effectiveAgility() + bonus
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
        equipment = this.equipment.toMutableMap(),
        traumaMarks = this.traumaMarks.map { it.copy() }.toMutableList(),
        activeStatusEffects = this.activeStatusEffects.map { it.copy() }.toMutableList()
    )
}

