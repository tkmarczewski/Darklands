package com.grimreich.core

import com.grimreich.grimreich.v1.*
import com.grimreich.core.mutations.*

fun GameState.toDto(): SessionStateDto = SessionStateDto(
    version = 2,
    playerName = playerName,
    heroName = heroName,
    characterNameLocked = characterNameLocked,
    metaAwarenessLevel = metaAwarenessLevel,
    grimCurrentRegion = grimCurrentRegion,
    grimPendingExpeditionName = grimPendingExpeditionName,
    pendingQuestId = pendingQuestId,
    pendingDialogueNpcName = pendingDialogueNpcName,
    pendingDialogueNpcRole = pendingDialogueNpcRole,
    pendingDialogueNodeId = pendingDialogueNodeId,
    party = party.map { it.toDto() },
    hireableHeroes = hireableHeroes.map { it.toDto() },
    activeHeroId = activeHeroId,
    inventory = inventory.map { it.toDto() },
    logEntries = logEntries,
    gold = gold,
    quest = quest.toDto(),
    reputation = reputation.toDto(),
    prayer = prayer.toDto(),
    world = world.toDto(),
    combat = combat.toDto(),
    lastSaveTimestamp = lastSaveTimestamp
)

fun SessionStateDto.toDomain(): GameState = GameState(
    playerName = playerName,
    heroName = heroName,
    characterNameLocked = characterNameLocked,
    metaAwarenessLevel = metaAwarenessLevel,
    grimCurrentRegion = grimCurrentRegion,
    grimPendingExpeditionName = grimPendingExpeditionName,
    pendingQuestId = pendingQuestId,
    pendingDialogueNpcName = pendingDialogueNpcName,
    pendingDialogueNpcRole = pendingDialogueNpcRole,
    pendingDialogueNodeId = pendingDialogueNodeId,
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
    activeMutations = activeMutations.map { it.toDto() },
    currentCareer = currentCareer?.name,
    trait = trait?.name,
    skills = skills,
    equipment = equipment
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
    currentCareer = currentCareer?.let { try { Career.valueOf(it) } catch(e: Exception) { null } },
    trait = trait?.let { try { Trait.valueOf(it) } catch(e: Exception) { null } }
).also {
    it.activeMutations.addAll(activeMutations.map { m -> m.toDomain() })
    it.skills.putAll(skills)
    it.equipment.putAll(equipment)
}

fun Item.toDto(): ItemDto = ItemDto(
    id = id,
    name = name,
    type = type.name, 
    slot = slot,
    value = value,
    weight = weight,
    rarity = rarity.name, 
    effects = effects
)

fun ItemDto.toDomain(): Item = Item(
    id = id,
    name = name,
    type = try { ItemType.valueOf(type) } catch(e: Exception) { ItemType.MISC },
    slot = slot,
    value = value,
    weight = weight,
    rarity = try { Rarity.valueOf(rarity) } catch(e: Exception) { Rarity.COMMON },
    effects = effects
)

fun QuestState.toDto(): QuestStateDto = QuestStateDto(
    activeQuests = activeQuests,
    completedQuests = completedQuests,
    questProgress = questProgress,
    activeEndgameQuests = activeEndgameQuests,
    completedEndgameQuests = completedEndgameQuests
)

fun QuestStateDto.toDomain(): QuestState = QuestState().also {
    it.activeQuests.addAll(activeQuests)
    it.completedQuests.addAll(completedQuests)
    it.questProgress.putAll(questProgress)
    it.activeEndgameQuests.addAll(activeEndgameQuests)
    it.completedEndgameQuests.addAll(completedEndgameQuests)
}

fun ReputationState.toDto(): ReputationStateDto = ReputationStateDto(
    cityFactions = cityFactions.mapValues { it.value.toMap() }
)

fun ReputationStateDto.toDomain(): ReputationState = ReputationState().also {
    it.cityFactions.putAll(cityFactions.mapValues { entry -> entry.value.toMutableMap() })
}

fun PrayerState.toDto(): PrayerStateDto = PrayerStateDto(
    faith = faith,
    virtue = virtue,
    sins = sins,
    blessings = blessings
)

fun PrayerStateDto.toDomain(): PrayerState = PrayerState().also {
    it.faith = faith
    it.virtue = virtue
    it.sins = sins
    it.blessings.addAll(blessings)
}

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
    discoveredLocations = discoveredLocations,
    cityEntryCount = cityEntryCount
)

fun WorldStateDto.toDomain(): WorldState = WorldState().also {
    it.region = region
    it.location = location
    it.day = day
    it.timeOfDay = timeOfDay
    it.fatigue = fatigue
    it.lastEncounter = lastEncounter
    it.season = try { Season.valueOf(season) } catch(e: Exception) { Season.AUTUMN }
    it.globalStability = globalStability
    it.weather = try { WeatherType.valueOf(weather) } catch(e: Exception) { WeatherType.CLEAR }
    it.echoIntensity = echoIntensity
    it.collapseProgress = collapseProgress
    it.ontologicalLevel = try { OntologicalLevel.entries.find { l -> l.level == ontologicalLevel } ?: OntologicalLevel.STABLE } catch(e: Exception) { OntologicalLevel.STABLE }
    it.discoveredLocations.addAll(discoveredLocations)
    it.cityEntryCount = cityEntryCount
}

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
    log = log
)

fun CombatStateDto.toDomain(): CombatState = CombatState().also {
    it.active = active
    it.round = round
    it.enemyName = enemyName
    it.enemyHp = enemyHp
    it.enemyMaxHp = enemyMaxHp
    it.enemyAttack = enemyAttack
    it.enemyDefense = enemyDefense
    it.enemyAgility = enemyAgility
    it.enemyIntelligence = enemyIntelligence
    it.enemyStrength = enemyStrength
    it.heroEffects.addAll(heroEffects.map { effect -> effect.toDomain() })
    it.enemyEffects.addAll(enemyEffects.map { effect -> effect.toDomain() })
    it.log.addAll(log)
}

fun StatusEffect.toDto(): StatusEffectDto = StatusEffectDto(
    type = type.name,
    duration = duration,
    magnitude = strength
)

fun StatusEffectDto.toDomain(): StatusEffect = StatusEffect(
    type = try { StatusEffectType.valueOf(type) } catch(e: Exception) { StatusEffectType.POISON },
    duration = duration,
    strength = magnitude
)
