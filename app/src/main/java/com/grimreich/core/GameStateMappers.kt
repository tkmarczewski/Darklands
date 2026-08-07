package com.grimreich.core

import android.util.Log
import com.grimreich.core.mutations.Mutation
import com.grimreich.grimreich.v1.NPC
import com.grimreich.grimreich.v1.Item
import com.grimreich.grimreich.v1.GrimWorldEngineFactory

fun GameState.toDto(): SessionStateDto = SessionStateDto(
    version = SAVE_VERSION,
    playerName = playerName,
    heroName = heroName,
    characterNameLocked = characterNameLocked,
    metaAwarenessLevel = metaAwarenessLevel,
    pendingAction = pendingAction.toDto(),
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
    grimMutationPhase = grimMutationPhase,
    grantedRewardFlags = grantedRewardFlags.toList(),
    companionShadows = companionShadows.map { it.toDto() }
)

fun SessionStateDto.toDomain(): GameState = GameState().also {
    it.playerName = playerName
    it.heroName = heroName
    it.characterNameLocked = characterNameLocked
    it.metaAwarenessLevel = metaAwarenessLevel
    it.pendingAction = pendingAction.toDomain()
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
    it.companionShadows.addAll(companionShadows.map { dto -> dto.toDomain() })
}

fun PendingWorldAction.toDto(): PendingWorldActionDto = when (this) {
    PendingWorldAction.None -> PendingWorldActionDto.None
    is PendingWorldAction.ResolveQuest -> PendingWorldActionDto.ResolveQuest(questId)
    is PendingWorldAction.QuestCombatWin -> PendingWorldActionDto.QuestCombatWin(questId)
    is PendingWorldAction.Dialogue -> PendingWorldActionDto.Dialogue(npcName, npcRole, nodeId, relatedQuestId)
}

fun PendingWorldActionDto.toDomain(): PendingWorldAction = when (this) {
    PendingWorldActionDto.None -> PendingWorldAction.None
    is PendingWorldActionDto.ResolveQuest -> PendingWorldAction.ResolveQuest(questId)
    is PendingWorldActionDto.QuestCombatWin -> PendingWorldAction.QuestCombatWin(questId)
    is PendingWorldActionDto.Dialogue -> PendingWorldAction.Dialogue(npcName, npcRole, nodeId, relatedQuestId)
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
    abilities = abilities.map { it.toDto() },
    passiveAbilities = passiveAbilities.toList(),
    subjectType = subjectType.name,
    ontologicalMass = ontologicalMass,
    traumaMarks = traumaMarks.map { it.toDto() },
    ontologicalStability = ontologicalStability,
    activeStatusEffects = activeStatusEffects.map { it.toDto() }
)

fun Trauma.toDto(): TraumaDto = TraumaDto(id, name, description, statModifiers, severity)
fun TraumaDto.toDomain(): Trauma = Trauma(id, name, description, statModifiers, severity)

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
    currentCareer = currentCareer?.let { runCatching { Career.valueOf(it.uppercase()) }.onFailure { Log.w("Mappers", "Failed to parse career: $it") }.getOrNull() ?: Career.MERCENARY },
    trait = trait?.let { runCatching { Trait.valueOf(it.uppercase()) }.onFailure { Log.w("Mappers", "Failed to parse trait: $it") }.getOrNull() ?: Trait.NONE },
    subjectType = runCatching { Hero.SubjectType.valueOf(subjectType.uppercase()) }.onFailure { Log.w("Mappers", "Failed to parse subjectType: $subjectType") }.getOrDefault(Hero.SubjectType.VESSEL),
    ontologicalMass = ontologicalMass,
    ontologicalStability = ontologicalStability
).also {
    it.activeMutations.addAll(activeMutations.map { dto -> dto.toDomain() })
    it.skills.putAll(skills)
    it.equipment.putAll(equipment)
    it.careerHistory.addAll(careerHistory.map { it.toDomain() })
    it.abilities.addAll(abilities.map { it.toDomain() })
    it.passiveAbilities.addAll(passiveAbilities)
    it.traumaMarks.addAll(traumaMarks.map { it.toDomain() })
    it.activeStatusEffects.addAll(activeStatusEffects.map { it.toDomain() })
}

fun CareerEntryDto.toDomain(): CareerEntry = CareerEntry(
    career = runCatching { Career.valueOf(careerName.uppercase()) }.onFailure { Log.w("Mappers", "Failed to parse career entry: $careerName") }.getOrDefault(Career.MERCENARY),
    daysServed = daysServed,
    levelReached = levelReached,
    dateReached = dateReached
)

fun CareerEntry.toDto(): CareerEntryDto = CareerEntryDto(
    careerName = career.name,
    daysServed = daysServed,
    levelReached = levelReached,
    dateReached = dateReached
)

fun AbilityDto.toDomain(): Ability = Ability(
    id = id,
    name = name,
    description = description ?: "",
    costType = runCatching { CostType.valueOf(type.uppercase()) }.onFailure { Log.w("Mappers", "Failed to parse costType: $type") }.getOrDefault(CostType.HP),
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
    description = description,
    tier = tier.name,
    category = category.name,
    attributeModifiers = attributeModifiers,
    stabilityImpact = stabilityImpact
)

fun MutationDto.toDomain(): Mutation = Mutation(
    id = id,
    name = name,
    description = description,
    category = runCatching { com.grimreich.core.mutations.MutationCategory.valueOf(category.uppercase()) }.onFailure { Log.w("Mappers", "Failed to parse mutation category: $category") }.getOrDefault(com.grimreich.core.mutations.MutationCategory.PHYSICAL),
    tier = runCatching { com.grimreich.core.mutations.MutationTier.valueOf(tier.uppercase()) }.onFailure { Log.w("Mappers", "Failed to parse mutation tier: $tier") }.getOrDefault(com.grimreich.core.mutations.MutationTier.DORMANT),
    attributeModifiers = attributeModifiers,
    stabilityImpact = stabilityImpact
)

fun Item.toDto(): ItemDto = ItemDto(
    instanceId = instanceId,
    templateId = templateId,
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
    instanceId = instanceId,
    templateId = templateId,
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
    failedQuestIds = failedQuestIds.toList(),
    progress = progress.mapValues { it.value.toDto() }
)

fun QuestStateDto.toDomain(): QuestState = QuestState().also {
    it.activeQuestIds.addAll(activeQuestIds)
    it.completedQuestIds.addAll(completedQuestIds)
    it.failedQuestIds.addAll(failedQuestIds)
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
    status = runCatching { QuestStatus.valueOf(status.uppercase()) }.onFailure { Log.w("Mappers", "Failed to parse quest status: $status") }.getOrDefault(QuestStatus.LOCKED),
    currentStepIndex = currentStepIndex,
    variables = variables.toMutableMap()
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
    locationId = locationId,
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
    verdictIncidentsSeen = verdictIncidentsSeen,
    reachedThresholds = reachedThresholds.toList()
)

fun WorldStateDto.toDomain(): WorldState = WorldState(
    region = region,
    locationId = locationId,
    day = day,
    timeOfDay = timeOfDay,
    fatigue = fatigue,
    lastEncounter = lastEncounter,
    season = runCatching { Season.valueOf(season.lowercase()) }.onFailure { Log.w("Mappers", "Failed to parse season: $season") }.getOrDefault(Season.spring),
    globalStability = globalStability,
    weather = runCatching { WeatherType.valueOf(weather.lowercase()) }.onFailure { Log.w("Mappers", "Failed to parse weather: $weather") }.getOrDefault(WeatherType.clear),
    echoIntensity = echoIntensity,
    collapseProgress = collapseProgress,
    collapseScenarioId = collapseScenarioId,
    ontologicalLevel = com.grimreich.grimreich.v1.OntologicalLevel.entries.find { it.level == ontologicalLevel } ?: com.grimreich.grimreich.v1.OntologicalLevel.material,
    cityEntryCount = cityEntryCount,
    verdictIncidentsSeen = verdictIncidentsSeen
).also {
    it.discoveredLocations.addAll(discoveredLocations)
    it.reachedThresholds.addAll(reachedThresholds)
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
    enemyStamina = enemyStamina,
    enemyMorale = enemyMorale,
    enemyEffects = enemyEffects.map { it.toDto() },
    heroEffects = heroEffects.map { it.toDto() },
    log = log,
    currentTargetHeroId = currentTargetHeroId,
    activeHeroId = activeHeroId,
    initiativeOrder = initiativeOrder.map { it.toDto() },
    currentTurnIndex = currentTurnIndex
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
    it.enemyStamina = enemyStamina
    it.enemyMorale = enemyMorale
    it.currentTargetHeroId = currentTargetHeroId
    it.activeHeroId = activeHeroId
    it.currentTurnIndex = currentTurnIndex
    it.heroEffects.addAll(heroEffects.map { effect -> effect.toDomain() })
    it.enemyEffects.addAll(enemyEffects.map { effect -> effect.toDomain() })
    it.initiativeOrder.addAll(initiativeOrder.map { it.toDomain() })
    it.log.addAll(log)
}

fun InitiativeSlot.toDto(): InitiativeSlotDto = InitiativeSlotDto(id, isPlayer, initiativeValue)
fun InitiativeSlotDto.toDomain(): InitiativeSlot = InitiativeSlot(id, isPlayer, initiativeValue)

fun NpcDto.toDomain(): NPC = NPC(
    id = id,
    name = name,
    role = role,
    factionId = factionId,
    personality = personality,
    startNodeId = startNodeId,
    stability = stability.toFloat()
)

fun NPC.toDto(): NpcDto = NpcDto(
    id = id,
    name = name,
    role = role,
    factionId = factionId,
    personality = personality,
    startNodeId = startNodeId,
    stability = stability.toInt()
)

fun StatusEffect.toDto(): StatusEffectDto = StatusEffectDto(
    type = type.name,
    duration = duration,
    magnitude = strength
)

fun StatusEffectDto.toDomain(): StatusEffect = StatusEffect(
    type = runCatching { StatusEffectType.valueOf(type.uppercase()) }.onFailure { Log.w("Mappers", "Failed to parse statusEffect type: $type") }.getOrDefault(StatusEffectType.POISON),
    duration = duration,
    strength = magnitude
)

fun PersistentMeta.toDto(): PersistentMetaDto = PersistentMetaDto(
    anchorIdentity = anchorIdentity,
    totalSessionsFinished = totalSessionsFinished,
    unlockedLegacyBuffs = unlockedLegacyBuffs.toList(),
    maxMetaAwarenessReached = maxMetaAwarenessReached,
    unitedSelves = unitedSelves.map { it.name }
)

fun PersistentMetaDto.toDomain(): PersistentMeta = PersistentMeta(
    anchorIdentity = anchorIdentity,
    totalSessionsFinished = totalSessionsFinished,
    maxMetaAwarenessReached = maxMetaAwarenessReached
).also {
    it.unlockedLegacyBuffs.addAll(unlockedLegacyBuffs)
    it.unitedSelves.addAll(unitedSelves.map { s -> runCatching { PersistentMeta.SelfAspect.valueOf(s.uppercase()) }.getOrDefault(PersistentMeta.SelfAspect.FEAR) })
}

inline fun <reified T : Enum<T>> safeEnumValue(name: String?, default: T): T {
    if (name == null) return default
    return try {
        java.lang.Enum.valueOf(T::class.java, name)
    } catch (e: Exception) {
        Log.w("GameStateMappers", "Failed to parse enum ${T::class.java.simpleName} from value: $name. Using default: $default")
        default
    }
}

fun SaveSnapshot.toDto(): SaveSnapshotDto = SaveSnapshotDto(
    version = version,
    timestamp = timestamp,
    label = label,
    session = state.toDto(),
    checksum = checksum
)

fun SaveSnapshotDto.toDomain(): SaveSnapshot = SaveSnapshot(
    version = version,
    timestamp = timestamp,
    label = label,
    state = session.toDomain(),
    checksum = checksum
)
