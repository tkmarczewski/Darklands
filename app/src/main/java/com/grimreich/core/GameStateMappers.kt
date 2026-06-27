package com.grimreich.core

import com.grimreich.grimreich.v1.*
import com.grimreich.core.mutations.*

fun GameState.toDto(): SessionStateDto = SessionStateDto(
    version = 3,
    playerName = playerName,
    heroName = heroName,
    characterNameLocked = characterNameLocked,
    metaAwarenessLevel = metaAwarenessLevel,
    grimCurrentRegion = grimCurrentRegion,
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
    knownNpcs = knownNpcs.mapValues { entry -> entry.value.map { it.toDto() } },
    unlockedLoreIds = unlockedLoreIds.toList(),
    persistentMeta = persistentMeta.toDto(),
    isExpeditionActive = isExpeditionActive,
    lastSaveTimestamp = lastSaveTimestamp
)

fun SessionStateDto.toDomain(): GameState = GameState(
    playerName = playerName,
    heroName = heroName,
    characterNameLocked = characterNameLocked,
    metaAwarenessLevel = metaAwarenessLevel,
    grimCurrentRegion = grimCurrentRegion,
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
).also {
    it.knownNpcs.putAll(knownNpcs.mapValues { entry -> entry.value.map { it.toDomain() } })
    it.unlockedLoreIds.addAll(unlockedLoreIds)
    it.persistentMeta.apply {
        totalSessionsFinished = persistentMeta.totalSessionsFinished
        unlockedLegacyBuffs.addAll(persistentMeta.unlockedLegacyBuffs)
        maxMetaAwarenessReached = persistentMeta.maxMetaAwarenessReached
    }
    it.isExpeditionActive = isExpeditionActive
    it.lastSaveTimestamp = lastSaveTimestamp
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
    isDead = isDead,
    activeMutations = activeMutations.map { it.toDto() },
    currentCareer = currentCareer?.name,
    trait = trait?.name,
    skills = skills,
    equipment = equipment,
    careerHistory = careerHistory.map { it.toDto() },
    abilities = abilities.map { it.toDto() }
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
    isDead = isDead,
    currentCareer = currentCareer?.let { try { Career.valueOf(it) } catch(e: Exception) { null } },
    trait = trait?.let { try { Trait.valueOf(it) } catch(e: Exception) { null } }
).also {
    it.activeMutations.addAll(activeMutations.map { m -> m.toDomain() })
    it.skills.putAll(skills)
    it.equipment.putAll(equipment)
    it.careerHistory.addAll(careerHistory.map { ce -> ce.toDomain() })
    it.abilities.addAll(abilities.map { a -> a.toDomain() })
}

fun CareerEntryDto.toDomain(): CareerEntry = CareerEntry(
    career = Career.valueOf(careerName),
    yearsServed = 0 // Info lost in DTO but better than nothing
)

fun CareerEntry.toDto(): CareerEntryDto = CareerEntryDto(
    careerName = career.name,
    levelReached = 0,
    dateReached = 0
)

fun AbilityDto.toDomain(): Ability = Ability(
    id = id,
    name = name,
    description = "",
    costType = try { CostType.valueOf(type) } catch(e: Exception) { CostType.NONE },
    costValue = 0
)

fun Ability.toDto(): AbilityDto = AbilityDto(
    id = id,
    name = name,
    type = costType.name
)

fun Mutation.toDto(): MutationDto = MutationDto(
    id = id,
    name = name,
    tier = tier.name,
    attributeModifiers = attributeModifiers,
    stabilityImpact = stabilityImpact
)

fun MutationDto.toDomain(): Mutation = Mutation(
    id = id,
    name = name,
    description = "",
    category = MutationCategory.PHYSICAL,
    tier = try { MutationTier.valueOf(tier) } catch(e: Exception) { MutationTier.MANIFESTED },
    attributeModifiers = attributeModifiers,
    stabilityImpact = stabilityImpact
)

fun Item.toDto(): ItemDto = ItemDto(
    id = id,
    name = name,
    type = type,
    slot = slot,
    value = value,
    weight = weight,
    rarity = rarity,
    lore = lore,
    effects = effects
)

fun ItemDto.toDomain(): Item = Item(
    id = id,
    name = name,
    type = type,
    slot = slot,
    value = value,
    weight = weight,
    rarity = rarity,
    lore = lore ?: "",
    effects = effects
)

fun QuestState.toDto(): QuestStateDto = QuestStateDto(
    activeQuestIds = activeQuestIds.toList(),
    completedQuestIds = completedQuestIds.toList(),
    progress = progress.mapValues { it.value.toDto() }
)

fun QuestStateDto.toDomain(): QuestState = QuestState(
    activeQuestIds = activeQuestIds.toMutableSet(),
    completedQuestIds = completedQuestIds.toMutableSet(),
    progress = progress.mapValues { it.value.toDomain() }.toMutableMap()
)

fun QuestProgress.toDto(): QuestProgressDto = QuestProgressDto(
    questId = questId,
    status = status.name,
    currentStepIndex = currentStepIndex,
    variables = variables
)

fun QuestProgressDto.toDomain(): QuestProgress = QuestProgress(
    questId = questId,
    status = QuestStatus.valueOf(status),
    currentStepIndex = currentStepIndex,
    variables = variables
)

fun ReputationState.toDto(): ReputationStateDto = ReputationStateDto(
    cityFactions = cityFactions.mapValues { it.value.toMap() },
    globalFactions = globalFactions.toMap()
)

fun ReputationStateDto.toDomain(): ReputationState = ReputationState().also {
    it.cityFactions.putAll(cityFactions.mapValues { entry -> entry.value.toMutableMap() })
    it.globalFactions.putAll(globalFactions.toMutableMap())
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
    cityEntryCount = cityEntryCount,
    verdictIncidentsSeen = verdictIncidentsSeen
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
    it.ontologicalLevel = try { OntologicalLevel.entries.find { l -> l.level == ontologicalLevel } ?: OntologicalLevel.MATERIAL } catch(e: Exception) { OntologicalLevel.MATERIAL }
    it.discoveredLocations.addAll(discoveredLocations)
    it.cityEntryCount = cityEntryCount
    it.verdictIncidentsSeen = verdictIncidentsSeen
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

fun NpcDto.toDomain(): NPC = NPC(
    id = id,
    name = name,
    role = role,
    factionId = factionId,
    personality = personality,
    startNodeId = startNodeId,
    stability = stability
)

fun NPC.toDto(): NpcDto = NpcDto(
    id = id,
    name = name,
    role = role,
    factionId = factionId,
    personality = personality,
    startNodeId = startNodeId,
    stability = stability
)

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

fun PersistentMeta.toDto(): PersistentMetaDto = PersistentMetaDto(
    totalSessionsFinished = totalSessionsFinished,
    unlockedLegacyBuffs = unlockedLegacyBuffs.toList(),
    maxMetaAwarenessReached = maxMetaAwarenessReached
)
