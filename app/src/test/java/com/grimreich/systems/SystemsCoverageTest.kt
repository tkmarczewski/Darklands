package com.grimreich.systems

import com.grimreich.core.*
import com.grimreich.grimreich.v1.Item
import com.grimreich.world.ItemCatalogue
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class SystemsCoverageTest {

    private lateinit var gameRepository: GameRepository
    private lateinit var itemCatalogue: ItemCatalogue
    private lateinit var inventorySystem: InventorySystem
    private lateinit var ritualSystem: RitualSystem
    private lateinit var alchemyCore: AlchemyCore
    private lateinit var combatSystem: CombatSystem
    private lateinit var state: GameState

    @Before
    fun setUp() {
        gameRepository = mock()
        combatSystem = mock()
        state = GameState()
        whenever(gameRepository.currentState()).thenReturn(state)
        whenever(gameRepository.gameState).thenReturn(kotlinx.coroutines.flow.MutableStateFlow(state))

        whenever(gameRepository.updateState(anyBoolean(), any())).thenAnswer { invocation ->
            val block = invocation.getArgument<(GameState) -> Unit>(1)
            block(state)
            null
        }

        itemCatalogue = ItemCatalogue().apply { seed() }
        inventorySystem = InventorySystem(gameRepository)
        ritualSystem = RitualSystem(gameRepository, combatSystem)
        alchemyCore = AlchemyCore(gameRepository)
    }

    @Test
    fun testTradeGoodCatalogAndTradingEngine() {
        val goods = TradeGoodCatalog.goods
        assertFalse(goods.isEmpty())

        val grain = TradeGoodCatalog.findByType(TradeGoodType.GRAIN)
        assertNotNull(grain)
        assertEquals("Zboże", grain?.name)

        // Mock EconomyCalculator
        val calc = object : EconomyCalculator {
            override fun priceInCity(cityId: String, basePrice: Int): Int = basePrice * 2
            override fun calculateSellPrice(cityId: String, item: Item): Int = item.value / 2
        }
        TradingEngine.initialize(calc)

        state.world.locationId = "city_1"
        state.gold = 1000

        val quote = TradingEngine.quoteBuy("city_1", TradeGoodType.GRAIN, 2)
        assertEquals(20, quote)

        val buyMsg = TradingEngine.buyGood(state, "city_1", TradeGoodType.GRAIN, 2)
        assertTrue(buyMsg.contains("Kupiono"))
        assertEquals(980, state.gold)

        val itemToSell = state.inventory.first()
        val sellQuote = TradingEngine.quoteSell("city_1", itemToSell)
        assertEquals(itemToSell.value / 2, sellQuote)

        val sellMsg = TradingEngine.sellItem(state, "city_1", itemToSell.instanceId)
        assertTrue(sellMsg.contains("Sprzedano"))
    }

    @Test
    fun testInventorySystemOperations() {
        val hero = Hero(id = "h1", name = "MercenaryHero", hp = 50, maxHp = 50)
        state.party.add(hero)

        val sword = Item("i1", "sword_basic", "Miecz", "weapon", "weapon", 20, 2.0, "Common", "", emptyMap(), mapOf("attack" to 5))
        state.inventory.add(sword)

        val equippedMsg = inventorySystem.equip("h1", "i1")
        assertEquals("Założono", equippedMsg)
        assertEquals("i1", hero.equipment["weapon"])

        val unequippedMsg = inventorySystem.unequip("h1", "weapon")
        assertTrue(unequippedMsg.contains("Zdjęto"))
        assertNull(hero.equipment["weapon"])
    }

    @Test
    fun testAlchemyCoreBrewing() {
        val heroHighSkill = Hero(id = "h1", name = "Alchemist", hp = 30, maxHp = 30).apply {
            skills["Alchemia"] = 25
        }
        val highResult = alchemyCore.brew(heroHighSkill)
        assertTrue(highResult.contains("mistrzowski"))

        val heroLowSkill = Hero(id = "h2", name = "Novice", hp = 30, maxHp = 30, endurance = 5).apply {
            skills["Alchemia"] = 2
        }
        val lowResult = alchemyCore.brew(heroLowSkill)
        assertTrue(lowResult.contains("nie wyszła"))
        assertEquals(4, heroLowSkill.endurance)
    }

    @Test
    fun testRitualSystemOperations() {
        val deadMainHero = Hero(id = "hero_main", name = "MainHero", hp = 0, maxHp = 50, isDead = true)
        state.party.add(deadMainHero)

        val resResult = ritualSystem.performResurrection("hero_main")
        assertTrue(resResult)
        assertFalse(deadMainHero.isDead)
        assertEquals(1, deadMainHero.hp)
    }

    @Test
    fun testAlchemyEnums() {
        val brimstone = AlchemyIngredient.BRIMSTONE
        assertEquals("Siarka", brimstone.displayName)
        assertEquals(10, brimstone.basePrice)

        val quality = PotionQuality.HIGH
        assertEquals("Mistrzowska", quality.label)
        assertEquals(2.0f, quality.priceMultiplier, 0.01f)
    }
}
