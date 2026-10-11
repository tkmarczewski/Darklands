package com.grimreich.core

import com.grimreich.core.mutations.Mutation
import com.grimreich.core.mutations.MutationCategory
import com.grimreich.core.mutations.MutationTier
import com.grimreich.grimreich.v1.Item
import com.grimreich.grimreich.v1.NPC
import org.junit.Assert.*
import org.junit.Test

class GameStateMappersCoverageTest {

    @Test
    fun testFullGameStateToDtoAndBack() {
        val state = GameState().apply {
            playerName = "Tester"
            heroName = "Gideon"
            characterNameLocked = true
            metaAwarenessLevel = 3
            pendingAction = PendingWorldAction.ResolveQuest("q1")
            gold = 500
            isExpeditionActive = true
            lastSaveTimestamp = 123456789L
            grimMutationPhase = 2
            grantedRewardFlags.add("REWARD_1")
            unlockedLoreIds.add("LORE_1")
            knownNpcs["city1"] = listOf(
                NPC(
                    id = "npc1",
                    name = "Jan",
                    role = "Smith",
                    factionId = "f1",
                    personality = "calm",
                    startNodeId = "n1",
                    stability = 0.8f
                )
            )
        }

        val dto = state.toDto()
        assertNotNull(dto)
        assertEquals("Tester", dto.playerName)
        assertEquals("Gideon", dto.heroName)
        assertEquals(3, dto.metaAwarenessLevel)
        assertEquals(500, dto.gold)
        assertTrue(dto.isExpeditionActive)

        val restored = dto.toDomain()
        assertEquals("Tester", restored.playerName)
        assertEquals("Gideon", restored.heroName)
        assertEquals(3, restored.metaAwarenessLevel)
        assertEquals(500, restored.gold)
        assertTrue(restored.isExpeditionActive)
        assertEquals(1, restored.grantedRewardFlags.size)
        assertEquals("REWARD_1", restored.grantedRewardFlags.first())
        assertEquals(1, restored.knownNpcs["city1"]?.size)
    }

    @Test
    fun testHeroToDtoAndBack() {
        val hero = Hero(
            id = "h1",
            name = "Balthazar",
            age = 30,
            strength = 12,
            agility = 10,
            perception = 11,
            intelligence = 14,
            endurance = 15,
            charisma = 8,
            piety = 16,
            virtue = 5,
            divineFavor = 20,
            sanity = 80,
            corruption = 10,
            morale = 90,
            level = 2,
            xp = 150,
            attributePoints = 3,
            portraitRes = "p1",
            hp = 45,
            maxHp = 50,
            isDead = false,
            currentCareer = Career.MERCENARY,
            trait = Trait.IRON_HEART,
            subjectType = Hero.SubjectType.VESSEL,
            ontologicalMass = 10,
            ontologicalStability = 0.9f
        ).apply {
            skills["sword"] = 5
            equipment["weapon"] = "item_sword"
            careerHistory.add(CareerEntry(Career.MERCENARY, 10, 2, System.currentTimeMillis()))
            abilities.add(Ability("a1", "Slash", "Direct hit", CostType.STAMINA, 5))
            passiveAbilities.add("p_ability_1")
            traumaMarks.add(Trauma("t1", "Broken Hand", "Minor trauma", mapOf("strength" to -1), 1))
            activeStatusEffects.add(StatusEffect(StatusEffectType.BLEED, 3, 5))
            worldStatusEffects.add(StatusEffect(StatusEffectType.POISON, 2, 3))
            activeMutations.add(Mutation("m1", "Eye", "Extra eye", MutationCategory.PHYSICAL, MutationTier.DOMINANT, mapOf("perception" to 2), 1))
        }

        val dto = hero.toDto()
        assertEquals("Balthazar", dto.name)
        assertEquals(12, dto.strength)
        assertTrue(dto.currentCareer.equals("MERCENARY", ignoreCase = true))
        assertTrue(dto.trait.equals("IRON_HEART", ignoreCase = true))

        val restored = dto.toDomain()
        assertEquals("Balthazar", restored.name)
        assertEquals(12, restored.strength)
        assertEquals(Career.MERCENARY, restored.currentCareer)
        assertEquals(Trait.IRON_HEART, restored.trait)
        assertEquals(5, restored.skills["sword"])
        assertEquals("item_sword", restored.equipment["weapon"])
        assertEquals(1, restored.careerHistory.size)
        assertEquals(1, restored.abilities.size)
        assertEquals(1, restored.traumaMarks.size)
        assertEquals(1, restored.activeStatusEffects.size)
        assertEquals(1, restored.worldStatusEffects.size)
        assertEquals(1, restored.activeMutations.size)
    }

    @Test
    fun testPendingWorldActionMappers() {
        val actions = listOf(
            PendingWorldAction.None,
            PendingWorldAction.ResolveQuest("q1"),
            PendingWorldAction.QuestCombatWin("q2"),
            PendingWorldAction.Dialogue("Jan", "Blacksmith", "node_1", "related_q")
        )

        for (act in actions) {
            val dto = act.toDto()
            val restored = dto.toDomain()
            assertNotNull(restored)
        }
    }

    @Test
    fun testItemAndQuestStateMappers() {
        val item = Item("i1", "sword_basic", "Miecz", "weapon", "weapon", 20, 2.0, "Common", "Lore text", emptyMap(), mapOf("attack" to 5))
        val itemDto = item.toDto()
        val itemRestored = itemDto.toDomain()
        assertEquals("Miecz", itemRestored.name)
        assertEquals(5, itemRestored.effects["attack"])

        val questState = QuestState().apply {
            activeQuestIds.add("q1")
            completedQuestIds.add("q0")
            progress["q1"] = QuestProgress("q1", QuestStatus.active, 1, mutableMapOf("v1" to "1"))
            worldFlags.add("FLAG_A")
        }
        val questDto = questState.toDto()
        val questRestored = questDto.toDomain()
        assertEquals(1, questRestored.activeQuestIds.size)
        assertEquals(QuestStatus.active, questRestored.progress["q1"]?.status)
    }

    @Test
    fun testReputationAndPrayerMappers() {
        val rep = ReputationState().apply {
            globalFactions["CROWN"] = 25
            cityFactions["city_1"] = mutableMapOf("GUILD" to 15)
        }
        val repDto = rep.toDto()
        val repRestored = repDto.toDomain()
        assertEquals(25, repRestored.globalFactions["CROWN"])
        assertEquals(15, repRestored.cityFactions["city_1"]?.get("GUILD"))

        val prayer = PrayerState(50, 10, 2).apply {
            blessings.add("BLESSING_STRENGTH")
        }
        val prayerDto = prayer.toDto()
        val prayerRestored = prayerDto.toDomain()
        assertEquals(50, prayerRestored.faith)
        assertEquals(1, prayerRestored.blessings.size)
    }

    @Test
    fun testWorldAndCombatStateMappers() {
        val world = WorldState(
            region = "Verdun",
            locationId = "loc_1",
            day = 5,
            timeOfDay = "NOON",
            fatigue = 20,
            lastEncounter = 100L,
            lastCityEventDay = 2L,
            lastWealthDrainDay = 3L,
            season = Season.summer,
            globalStability = 80,
            weather = WeatherType.clear,
            echoIntensity = 1.5f,
            collapseProgress = 0.2f,
            collapseScenarioId = "scen_1",
            ontologicalLevel = com.grimreich.grimreich.v1.OntologicalLevel.material,
            cityEntryCount = 3,
            verdictIncidentsSeen = 1
        ).apply {
            discoveredLocations.add("loc_2")
            reachedThresholds.add("THRESH_1")
        }

        val worldDto = world.toDto()
        val worldRestored = worldDto.toDomain()
        assertEquals("Verdun", worldRestored.region)
        assertEquals(3L, worldRestored.lastWealthDrainDay)
        assertEquals(Season.summer, worldRestored.season)
        assertEquals(1, worldRestored.discoveredLocations.size)

        val combat = CombatState().apply {
            active = true
            round = 2
            enemyName = "Wolf"
            enemyType = "WOLF"
            enemyHp = 20
            enemyMaxHp = 30
            initiativeOrder.add(InitiativeSlot("h1", true, 12))
        }
        val combatDto = combat.toDto()
        val combatRestored = combatDto.toDomain()
        assertTrue(combatRestored.active)
        assertEquals("WOLF", combatRestored.enemyType)
        assertEquals(1, combatRestored.initiativeOrder.size)
    }

    @Test
    fun testPersistentMetaAndSaveSnapshotMappers() {
        val meta = PersistentMeta("anchor_1", 2, 4).apply {
            unlockedLegacyBuffs.add("BUFF_1")
            unitedSelves.add(PersistentMeta.SelfAspect.FEAR)
        }
        val metaDto = meta.toDto()
        val metaRestored = metaDto.toDomain()
        assertEquals("anchor_1", metaRestored.anchorIdentity)
        assertEquals(1, metaRestored.unlockedLegacyBuffs.size)

        val snapshot = SaveSnapshot(1, 1000L, "QuickSave", GameState(), "hash_123")
        val snapshotDto = snapshot.toDto()
        val snapshotRestored = snapshotDto.toDomain()
        assertEquals("QuickSave", snapshotRestored.label)
        assertEquals("hash_123", snapshotRestored.checksum)
    }

    @Test
    fun testSafeEnumValueFallback() {
        val valid = safeEnumValue<Season>("winter", Season.spring)
        assertEquals(Season.winter, valid)

        val invalid = safeEnumValue<Season>("invalid_season", Season.spring)
        assertEquals(Season.spring, invalid)
    }
}
