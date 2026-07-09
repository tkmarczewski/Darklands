package com.grimreich.core

import com.grimreich.core.mutations.Mutation
import com.grimreich.grimreich.v1.NPC
import com.grimreich.grimreich.v1.Item
import com.grimreich.grimreich.v1.GrimWorldEngineFactory

fun GameState.toDto(): SessionStateDto = SessionStateDto(
    version = 1,
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
    lastSaveTimestamp = lastSaveTimestamp,
    grimEchoIntensity = 0f, // Deprecated or handle via grimEngine if needed
    grimMutationPhase = grimMutationPhase,
    grantedRewardFlags = grantedRewardFlags.toList()
)

fun SessionStateDto.toDomain(): GameState = GameState().also {
    it.playerName = playerName
    it.heroName = heroName
    it.characterNameLocked = characterNameLocked
    it.metaAwarenessLevel = metaAwarenessLevel
    it.grimCurrentRegion = grimCurrentRegion
    it.pendingQuestId = pendingQuestId
    it.pendingDialogueNpcName = pendingDialogueNpcName
    it.pendingDialogueNpcRole = pendingDialogueNpcRole
    it.pendingDialogueNodeId = pendingDialogueNodeId
    it.party.addAll(party.map { dto -> dto.toDomain() })
    it.hireableHeroes.addAll(hireableHeroes.map { dto -> dto.toDomain() })
    it.activeHeroId = activeHeroId
    it.inventory.addAll(inventory.map { dto -> dto.toDomain() })
    it.logEntries.addAll(logEntries)
    it.gold = gold
    it.quest = quest.toDomain()
    it.reputation = reputation.toDomain()
    it.prayer = prayer.toDomain()
    it.world = world.toDomain()
    it.combat = combat.toDomain()
    it.knownNpcs.putAll(knownNpcs.mapValues { entry -> entry.value.map { it.toDomain() } })
    it.unlockedLoreIds.addAll(unlockedLoreIds)
    it.persistentMeta = persistentMeta.toDomain()
    it.isExpeditionActive = isExpeditionActive
    it.lastSaveTimestamp = lastSaveTimestamp
    it.grimMutationPhase = grimMutationPhase
    it.grantedRewardFlags.addAll(grantedRewardFlags)
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
    currentCareer = currentCareer?.let { Career.valueOf(it) },
    trait = trait?.let { Trait.valueOf(it) }
).also {
    it.activeMutations.addAll(activeMutations.map { dto -> dto.toDomain() })
    it.skills.putAll(skills)
    it.equipment.putAll(equipment)
    it.careerHistory.addAll(careerHistory.map { it.toDomain() })
    it.abilities.addAll(abilities.map { it.toDomain() })
}

fun CareerEntryDto.toDomain(): CareerEntry = CareerEntry(
    career = Career.valueOf(careerName),
    yearsServed = yearsServed
)

fun CareerEntry.toDto(): CareerEntryDto = CareerEntryDto(
    careerName = career.name,
    yearsServed = yearsServed,
    levelReached = 1, // Default
    dateReached = 0L // Default
)

fun AbilityDto.toDomain(): Ability = Ability(
    id = id,
    name = name,
    description = description ?: "",
    costType = CostType.valueOf(type),
    costValue = costValue ?: 0
)

fun Ability.toDto(): AbilityDto = AbilityDto(
    id = id,
    name = name,
    type = costType.name,
    description = description,
    costValue = costValue
)

fun Mutation.toDto(): MutationDto = MutationDto(
    id = id,
    name = name,
    tier = tier.name,
    category = category.name,
    attributeModifiers = attributeModifiers,
    stabilityImpact = stabilityImpact
)

fun MutationDto.toDomain(): Mutation = Mutation(
    id = id,
    name = name,
    description = "",
    category = com.grimreich.core.mutations.MutationCategory.valueOf(category),
    tier = com.grimreich.core.mutations.MutationTier.valueOf(tier),
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

fun QuestStateDto.toDomain(): QuestState = QuestState().also {
    it.activeQuestIds.addAll(activeQuestIds)
    it.completedQuestIds.addAll(completedQuestIds)
    it.progress.putAll(progress.mapValues { entry -> entry.value.toDomain() })
}

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
    globalFactions = globalFactions.toMap(),
    cityFactions = cityFactions.mapValues { it.value.toMap() }
)

fun ReputationStateDto.toDomain(): ReputationState = ReputationState().also {
    it.globalFactions.putAll(globalFactions)
    it.cityFactions.putAll(cityFactions.mapValues { entry -> entry.value.toMutableMap() })
}

fun PrayerState.toDto(): PrayerStateDto = PrayerStateDto(
    faith = faith,
    virtue = virtue,
    sins = sins,
    blessings = blessings
)

fun PrayerStateDto.toDomain(): PrayerState = PrayerState(
    faith = faith,
    virtue = virtue,
    sins = sins
).also {
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
    collapseScenarioId = collapseScenarioId,
    ontologicalLevel = ontologicalLevel.level,
    discoveredLocations = discoveredLocations,
    cityEntryCount = cityEntryCount,
    verdictIncidentsSeen = verdictIncidentsSeen
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
    collapseScenarioId = collapseScenarioId,
    ontologicalLevel = com.grimreich.grimreich.v1.OntologicalLevel.entries.find { it.level == ontologicalLevel } ?: com.grimreich.grimreich.v1.OntologicalLevel.MATERIAL,
    cityEntryCount = cityEntryCount,
    verdictIncidentsSeen = verdictIncidentsSeen
).also {
    it.discoveredLocations.addAll(discoveredLocations)
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
    log = log,
    currentTargetHeroId = currentTargetHeroId,
    activeHeroId = activeHeroId
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
    it.currentTargetHeroId = currentTargetHeroId
    it.activeHeroId = activeHeroId
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
    type = StatusEffectType.valueOf(type),
    duration = duration,
    strength = magnitude
)

fun PersistentMeta.toDto(): PersistentMetaDto = PersistentMetaDto(
    totalSessionsFinished = totalSessionsFinished,
    unlockedLegacyBuffs = unlockedLegacyBuffs.toList(),
    maxMetaAwarenessReached = maxMetaAwarenessReached
)

fun PersistentMetaDto.toDomain(): PersistentMeta = PersistentMeta(
    totalSessionsFinished = totalSessionsFinished,
    maxMetaAwarenessReached = maxMetaAwarenessReached
).also {
    it.unlockedLegacyBuffs.addAll(unlockedLegacyBuffs)
}
