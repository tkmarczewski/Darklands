package com.grimreich.core

import com.grimreich.grimreich.v1.*

fun GameState.toDto(): SessionStateDto {
    return SessionStateDto(
        version = 2, // Engine migration version
        playerName = playerName,
        characterNameLocked = characterNameLocked,
        metaAwarenessLevel = metaAwarenessLevel,
        grimCurrentRegion = grimCurrentRegion,
        grimPendingExpeditionName = grimPendingExpeditionName,
        pendingQuestId = pendingQuestId,
        party = party.map { it.toDto() },
        hireableHeroes = hireableHeroes.map { it.toDto() },
        activeHeroId = activeHeroId,
        inventory = inventory.map { it.toDto() },
        logEntries = logEntries.toList(),
        gold = gold,
        quest = quest.toDto(),
        reputation = reputation.toDto(),
        prayer = prayer.toDto(),
        world = world.toDto(),
        combat = combat.toDto(),
        lastSaveTimestamp = System.currentTimeMillis()
    )
}

fun SessionStateDto.toDomain(): GameState {
    return GameState(
        playerName = playerName,
        characterNameLocked = characterNameLocked,
        metaAwarenessLevel = metaAwarenessLevel,
        grimCurrentRegion = grimCurrentRegion,
        grimPendingExpeditionName = grimPendingExpeditionName,
        pendingQuestId = pendingQuestId,
        party = party.map { it.toDomain() }.toMutableList(),
        hireableHeroes = hireableHeroes.map { it.toDomain() }.toMutableList(),
        activeHeroId = activeHeroId,
        inventory = inventory.map { it.toDomain() }.toMutableList(),
        logEntries = logEntries.toMutableList(),
        gold = gold,
        quest = quest.toDomain(),
        reputation = reputation.toDomain(),
        prayer = prayer.toDomain(),
        world = world.toDomain(),
        combat = combat.toDomain(),
        lastSaveTimestamp = lastSaveTimestamp
    )
}

fun Hero.toDto(): HeroDto = HeroDto(
    id = id,
    name = name,
    age = age,
    strength = strength,
    agility = agility,
    perception = perception,
    intelligence = intelligence,
    endurance = endurance,
    charisma = charisma,
    piety = piety,
    virtue = virtue,
    divineFavor = divineFavor,
    sanity = sanity,
    corruption = corruption,
    morale = morale,
    level = level,
    xp = xp,
    attributePoints = attributePoints,
    portraitRes = portraitRes,
    hp = hp,
    maxHp = maxHp,
    currentCareer = currentCareer?.name,
    trait = trait?.name,
    skills = skills.toMap(),
    equipment = equipment.toMap()
)

fun HeroDto.toDomain(): Hero = Hero(
    id = id,
    name = name,
    age = age,
    strength = strength,
    agility = agility,
    perception = perception,
    intelligence = intelligence,
    endurance = endurance,
    charisma = charisma,
    piety = piety,
    virtue = virtue,
    divineFavor = divineFavor,
    sanity = sanity,
    corruption = corruption,
    morale = morale,
    level = level,
    xp = xp,
    attributePoints = attributePoints,
    portraitRes = portraitRes,
    hp = hp,
    maxHp = maxHp,
    currentCareer = currentCareer?.let { Career.valueOf(it) },
    trait = trait?.let { Trait.valueOf(it) }
).also {
    it.skills.putAll(skills)
    it.equipment.putAll(equipment)
}

fun Item.toDto(): ItemDto = ItemDto(
    id = id,
    name = name,
    type = type,
    slot = slot,
    value = value,
    weight = weight,
    rarity = rarity,
    effects = effects.toMap()
)

fun ItemDto.toDomain(): Item = Item(
    id = id,
    name = name,
    type = type,
    slot = slot,
    value = value,
    weight = weight,
    rarity = rarity,
    effects = effects.toMutableMap()
)

fun QuestState.toDto(): QuestStateDto = QuestStateDto(
    activeQuests = activeQuests.toList(),
    completedQuests = completedQuests.toList(),
    questProgress = questProgress.toMap(),
    activeEndgameQuests = activeEndgameQuests.toList(),
    completedEndgameQuests = completedEndgameQuests.toList()
)

fun QuestStateDto.toDomain(): QuestState = QuestState(
    activeQuests = activeQuests.toMutableList(),
    completedQuests = completedQuests.toMutableList(),
    questProgress = questProgress.toMutableMap(),
    activeEndgameQuests = activeEndgameQuests.toMutableList(),
    completedEndgameQuests = completedEndgameQuests.toMutableList()
)

fun ReputationState.toDto(): ReputationStateDto = ReputationStateDto(
    cityFactions = cityFactions.mapValues { it.value.toMap() }
)

fun ReputationStateDto.toDomain(): ReputationState = ReputationState(
    cityFactions = cityFactions.mapValues { it.value.toMutableMap() }.toMutableMap()
)

fun PrayerState.toDto(): PrayerStateDto = PrayerStateDto(
    faith = faith,
    virtue = virtue,
    sins = sins,
    blessings = blessings.toList()
)

fun PrayerStateDto.toDomain(): PrayerState = PrayerState(
    faith = faith,
    virtue = virtue,
    sins = sins,
    blessings = blessings.toMutableList()
)

fun WorldState.toDto(): WorldStateDto = WorldStateDto(
    region = region,
    location = location,
    day = day,
    timeOfDay = timeOfDay,
    fatigue = fatigue,
    lastEncounter = lastEncounter,
    season = season.name,
    globalStability = globalStability,
    weather = weather.name,
    echoIntensity = echoIntensity,
    collapseProgress = collapseProgress,
    ontologicalLevel = ontologicalLevel.level,
    discoveredLocations = discoveredLocations.toList(),
    cityEntryCount = cityEntryCount
)

fun WorldStateDto.toDomain(): WorldState = WorldState(
    region = region,
    location = location,
    day = day,
    timeOfDay = timeOfDay,
    fatigue = fatigue,
    lastEncounter = lastEncounter,
    season = Season.valueOf(season),
    globalStability = globalStability,
    weather = WeatherType.valueOf(weather),
    echoIntensity = echoIntensity,
    collapseProgress = collapseProgress,
    ontologicalLevel = OntologicalLevel.values().find { it.level == ontologicalLevel } ?: OntologicalLevel.MATERIAL,
    discoveredLocations = discoveredLocations.toMutableList(),
    cityEntryCount = cityEntryCount
)

fun CombatState.toDto(): CombatStateDto = CombatStateDto(
    active = active,
    round = round,
    enemyName = enemyName,
    enemyHp = enemyHp,
    enemyMaxHp = enemyMaxHp,
    enemyAttack = enemyAttack,
    enemyDefense = enemyDefense,
    enemyAgility = enemyAgility,
    enemyIntelligence = enemyIntelligence,
    enemyStrength = enemyStrength,
    enemyEffects = enemyEffects.map { it.toDto() },
    heroEffects = heroEffects.map { it.toDto() },
    log = log.toList()
)

fun CombatStateDto.toDomain(): CombatState = CombatState(
    active = active,
    round = round,
    enemyName = enemyName,
    enemyHp = enemyHp,
    enemyMaxHp = enemyMaxHp,
    enemyAttack = enemyAttack,
    enemyDefense = enemyDefense,
    enemyAgility = enemyAgility,
    enemyIntelligence = enemyIntelligence,
    enemyStrength = enemyStrength,
    enemyEffects = enemyEffects.map { it.toDomain() }.toMutableList(),
    heroEffects = heroEffects.map { it.toDomain() }.toMutableList(),
    log = log.toMutableList()
)

fun StatusEffect.toDto(): StatusEffectDto = StatusEffectDto(
    type = type.name,
    duration = duration,
    strength = strength
)

fun StatusEffectDto.toDomain(): StatusEffect = StatusEffect(
    type = StatusEffectType.valueOf(type),
    duration = duration,
    strength = strength
)
