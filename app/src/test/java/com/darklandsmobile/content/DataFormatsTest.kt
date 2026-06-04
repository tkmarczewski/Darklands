package com.darklandsmobile.content

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.DefaultAsserter.assertNotNull

/**
 * Unit tests for data format constraints and validation.
 * Sprint 0.1 & 0.2: Weryfikuj, że formaty danych są poprawne.
 */
class DataFormatsTest {

    // ==================== CITY TESTS ====================

    @Test
    fun `CityData requires non-blank id`() {
        assertThrows<IllegalArgumentException> {
            CityData(id = "", name = "Test City", region = "test", type = CityType.CITY, population = 5000)
        }
    }

    @Test
    fun `CityData requires non-blank name`() {
        assertThrows<IllegalArgumentException> {
            CityData(id = "test", name = "", region = "test", type = CityType.CITY, population = 5000)
        }
    }

    @Test
    fun `CityData requires positive population`() {
        assertThrows<IllegalArgumentException> {
            CityData(id = "test", name = "Test", region = "test", type = CityType.CITY, population = 0)
        }
    }

    @Test
    fun `CityData is valid with correct parameters`() {
        val city = CityData(
            id = "cologne",
            name = "Köln",
            region = "rhineland",
            type = CityType.METROPOLIS,
            population = 40000,
            priceModifier = 1.1f
        )
        assert(city.id == "cologne")
        assert(city.name == "Köln")
        assert(city.population == 40000)
    }

    // ==================== SAINT TESTS ====================

    @Test
    fun `SaintData requires non-blank id`() {
        assertThrows<IllegalArgumentException> {
            SaintData(
                id = "",
                name = "Saint",
                domain = "War",
                patronage = "Soldiers"
            )
        }
    }

    @Test
    fun `SaintData requires non-blank name`() {
        assertThrows<IllegalArgumentException> {
            SaintData(
                id = "st_test",
                name = "",
                domain = "War",
                patronage = "Soldiers"
            )
        }
    }

    @Test
    fun `SaintPower requires positive faith cost`() {
        assertThrows<IllegalArgumentException> {
            SaintPower(
                name = "Test Power",
                description = "Test",
                faithCost = 0,
                effect = "test"
            )
        }
    }

    @Test
    fun `SaintData with power is valid`() {
        val saint = SaintData(
            id = "st_george",
            name = "St. George",
            domain = "Combat",
            patronage = "Soldiers, Dragon slayers",
            power = SaintPower(
                name = "Dragon Slayer",
                description = "Demons weaken",
                faithCost = 20,
                effect = "demon_weakness"
            )
        )
        assert(saint.id == "st_george")
        assert(saint.power?.faithCost == 20)
    }

    // ==================== CAREER TESTS ====================

    @Test
    fun `CareerData requires non-blank id`() {
        assertThrows<IllegalArgumentException> {
            CareerData(
                id = "",
                name = "Soldier",
                group = CareerGroup.MILITARY,
                description = "Test",
                effects = CareerEffects()
            )
        }
    }

    @Test
    fun `CareerData requires non-blank name`() {
        assertThrows<IllegalArgumentException> {
            CareerData(
                id = "soldier",
                name = "",
                group = CareerGroup.MILITARY,
                description = "Test",
                effects = CareerEffects()
            )
        }
    }

    @Test
    fun `CareerData with requirements is valid`() {
        val career = CareerData(
            id = "soldier",
            name = "Żołnierz",
            group = CareerGroup.MILITARY,
            description = "Professional soldier",
            requirements = CareerRequirements(
                minAge = 18,
                minStrength = 4,
                previousCareers = listOf("recruit")
            ),
            effects = CareerEffects(
                attributeBonuses = mapOf("strength" to 1),
                skillBonuses = mapOf("melee" to 2)
            )
        )
        assert(career.requirements?.minStrength == 4)
        assert(career.effects.attributeBonuses["strength"] == 1)
    }

    // ==================== ENEMY TESTS ====================

    @Test
    fun `EnemyStats requires positive hp`() {
        assertThrows<IllegalArgumentException> {
            EnemyStats(hp = 0, strength = 5, agility = 5, intellect = 2, constitution = 5, armor = 2)
        }
    }

    @Test
    fun `EnemyStats requires non-negative armor`() {
        assertThrows<IllegalArgumentException> {
            EnemyStats(hp = 30, strength = 5, agility = 5, intellect = 2, constitution = 5, armor = -1)
        }
    }

    @Test
    fun `EnemyType requires non-blank id`() {
        assertThrows<IllegalArgumentException> {
            EnemyType(
                id = "",
                name = "Bandit",
                type = EnemyCategory.HUMANOID,
                baseStats = EnemyStats(hp = 30, strength = 3, agility = 3, intellect = 2, constitution = 3, armor = 1)
            )
        }
    }

    @Test
    fun `EnemyType is valid with correct parameters`() {
        val enemy = EnemyType(
            id = "werewolf",
            name = "Wilkołak",
            type = EnemyCategory.MONSTER,
            baseStats = EnemyStats(hp = 60, strength = 6, agility = 5, intellect = 2, constitution = 5, armor = 2),
            specialTraits = listOf("nocturnal_bonus", "regeneration")
        )
        assert(enemy.id == "werewolf")
        assert(enemy.specialTraits.size == 2)
    }

    // ==================== QUEST TESTS ====================

    @Test
    fun `QuestEvent requires non-blank id`() {
        assertThrows<IllegalArgumentException> {
            QuestEvent(id = "", description = "Test event")
        }
    }

    @Test
    fun `QuestRewards requires non-negative gold`() {
        assertThrows<IllegalArgumentException> {
            QuestRewards(gold = -10)
        }
    }

    @Test
    fun `QuestEnding requires non-blank id`() {
        assertThrows<IllegalArgumentException> {
            QuestEnding(id = "", description = "Test", requirementEvents = emptyList())
        }
    }

    @Test
    fun `QuestChain requires non-empty events`() {
        assertThrows<IllegalArgumentException> {
            QuestChain(
                id = "test_quest",
                name = "Test Quest",
                startingRegion = "test",
                events = emptyList(),
                rewards = QuestRewards(),
                endings = listOf(QuestEnding(id = "ending1", description = "Test", requirementEvents = emptyList()))
            )
        }
    }

    @Test
    fun `QuestChain requires non-empty endings`() {
        assertThrows<IllegalArgumentException> {
            QuestChain(
                id = "test_quest",
                name = "Test Quest",
                startingRegion = "test",
                events = listOf(QuestEvent(id = "event1", description = "Test")),
                rewards = QuestRewards(),
                endings = emptyList()
            )
        }
    }

    @Test
    fun `QuestChain is valid with correct parameters`() {
        val quest = QuestChain(
            id = "witch_hunt",
            name = "Witch Hunt",
            startingRegion = "bohemia",
            events = listOf(
                QuestEvent(id = "start", description = "Hear rumor", nextEventIds = listOf("investigate")),
                QuestEvent(id = "investigate", description = "Investigate", nextEventIds = listOf("confrontation"))
            ),
            rewards = QuestRewards(gold = 100, virtue = 5),
            endings = listOf(
                QuestEnding(id = "good", description = "Good ending", requirementEvents = listOf("start", "investigate"))
            )
        )
        assert(quest.id == "witch_hunt")
        assert(quest.events.size == 2)
        assert(quest.rewards.gold == 100)
    }

    // ==================== RUMOR TESTS ====================

    @Test
    fun `Rumor requires non-blank id`() {
        assertThrows<IllegalArgumentException> {
            Rumor(id = "", text = "Test rumor", region = "test", sourceType = RumorSource.TAVERN)
        }
    }

    @Test
    fun `Rumor requires non-blank text`() {
        assertThrows<IllegalArgumentException> {
            Rumor(id = "r1", text = "", region = "test", sourceType = RumorSource.TAVERN)
        }
    }

    @Test
    fun `Rumor veracity must be between 0 and 1`() {
        assertThrows<IllegalArgumentException> {
            Rumor(id = "r1", text = "Test", region = "test", sourceType = RumorSource.TAVERN, veracity = 1.5f)
        }

        assertThrows<IllegalArgumentException> {
            Rumor(id = "r1", text = "Test", region = "test", sourceType = RumorSource.TAVERN, veracity = -0.1f)
        }
    }

    @Test
    fun `Rumor is valid with correct parameters`() {
        val rumor = Rumor(
            id = "r_witch",
            text = "There are witches in the forest",
            veracity = 0.7f,
            region = "bohemia",
            sourceType = RumorSource.CHURCH,
            linkedQuestId = "witch_hunt"
        )
        assert(rumor.id == "r_witch")
        assert(rumor.veracity == 0.7f)
    }

    // ==================== NPC TESTS ====================

    @Test
    fun `NamedNpc requires non-blank id`() {
        assertThrows<IllegalArgumentException> {
            NamedNpc(id = "", name = "John", role = NpcRole.TAVERN_KEEPER, cityId = "prague")
        }
    }

    @Test
    fun `NamedNpc requires non-blank name`() {
        assertThrows<IllegalArgumentException> {
            NamedNpc(id = "npc1", name = "", role = NpcRole.TAVERN_KEEPER, cityId = "prague")
        }
    }

    @Test
    fun `NamedNpc requires non-blank cityId`() {
        assertThrows<IllegalArgumentException> {
            NamedNpc(id = "npc1", name = "John", role = NpcRole.TAVERN_KEEPER, cityId = "")
        }
    }

    @Test
    fun `NamedNpc is valid with correct parameters`() {
        val npc = NamedNpc(
            id = "npc_innkeeper_prague",
            name = "Old Jan",
            role = NpcRole.INNKEEPER,
            cityId = "prague",
            description = "Friendly innkeeper with good rumors"
        )
        assert(npc.id == "npc_innkeeper_prague")
        assert(npc.role == NpcRole.INNKEEPER)
    }

    // ==================== SOCIAL BACKGROUND TESTS ====================

    @Test
    fun `SocialBackground enum has 6 values`() {
        val backgrounds = SocialBackground.values()
        assert(backgrounds.size == 6)
        assert(SocialBackground.NOBILITY in backgrounds)
        assert(SocialBackground.URBAN_COMMONERS in backgrounds)
    }

    // ==================== CAREER GROUP TESTS ====================

    @Test
    fun `CareerGroup enum has 6 values`() {
        val groups = CareerGroup.values()
        assert(groups.size == 6)
        assert(CareerGroup.MILITARY in groups)
        assert(CareerGroup.UNDERWORLD in groups)
    }
}
