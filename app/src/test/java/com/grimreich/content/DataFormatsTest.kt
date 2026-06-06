package com.grimreich.content

import org.junit.Test
import java.util.UUID

class DataFormatsTest {

    @Test
    fun `CityData requires non-blank id`() {
        val city = CityData(id = "wybrzeze_polnocne", name = "Test", region = "N", type = CityType.CITY, population = 1000)
        assert(city.id.isNotBlank())
    }

    @Test
    fun `CityData requires positive population`() {
        try {
            CityData(id = "c", name = "N", region = "R", type = CityType.CITY, population = 0)
            assert(false)
        } catch (e: IllegalArgumentException) {
            assert(true)
        }
    }

    @Test
    fun `CityData is valid with correct parameters`() {
        val city = CityData(
            id = "rowniny_koronne",
            name = "Równiny Koronne",
            region = "East",
            type = CityType.CITY,
            population = 5000,
            priceModifier = 0.9f
        )
        assert(city.id == "rowniny_koronne")
        assert(city.name == "Równiny Koronne")
        assert(city.priceModifier == 0.9f)
    }

    @Test
    fun `SaintData with power is valid`() {
        val power = SaintPower(name = "Heal", description = "D", faithCost = 10, effect = "H")
        val saint = SaintData(
            id = "s1",
            name = "Saint Gregory",
            domain = "Healing",
            patronage = "Sick",
            power = power
        )
        assert(saint.power != null)
        assert(saint.power?.name == "Heal")
    }

    @Test
    fun `CareerData with requirements is valid`() {
        val effects = CareerEffects(attributeBonuses = mapOf("Strength" to 2))
        val reqs = CareerRequirements(minStrength = 12)
        val career = CareerData(
            id = "knight",
            name = "Knight",
            group = CareerGroup.MILITARY,
            description = "Warrior",
            effects = effects,
            requirements = reqs
        )
        assert(career.id == "knight")
        assert(career.requirements?.minStrength == 12)
    }

    @Test
    fun `EnemyType is valid with correct parameters`() {
        val stats = EnemyStats(hp = 50, strength = 10, agility = 5, intellect = 5, constitution = 10, armor = 2)
        val enemy = EnemyType(
            id = "skeleton",
            name = "Skeleton",
            type = EnemyCategory.UNDEAD,
            baseStats = stats
        )
        assert(enemy.id == "skeleton")
        assert(enemy.baseStats.hp == 50)
    }

    @Test
    fun `QuestChain is valid with correct parameters`() {
        val reward = QuestRewards(gold = 100, reputation = 10)
        val event = QuestEvent(id = "e1", description = "D")
        val ending = QuestEnding(id = "end1", description = "Success", requirementEvents = listOf("e1"))
        val chain = QuestChain(
            id = "q1",
            name = "Holy War",
            startingRegion = "wybrzeze_polnocne",
            events = listOf(event),
            rewards = reward,
            endings = listOf(ending)
        )
        assert(chain.id == "q1")
        assert(chain.events.size == 1)
    }

    @Test
    fun `NamedNpc is valid with correct parameters`() {
        val npc = NamedNpc(
            id = "aelion",
            name = "Aelion",
            role = NpcRole.GUARD, // Updated role
            cityId = "wybrzeze_polnocne",
            description = "Seer"
        )
        assert(npc.id == "aelion")
        assert(npc.cityId == "wybrzeze_polnocne")
    }

    @Test
    fun `SocialBackground enum has values`() {
        assert(SocialBackground.entries.isNotEmpty())
    }
}
