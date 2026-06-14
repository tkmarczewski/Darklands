package com.grimreich.ui

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.core.GameRepository
import com.grimreich.core.TradeGoodCatalog
import com.grimreich.core.TradeGoodType
import com.grimreich.core.CityMarketCatalog
import com.grimreich.systems.TradeSystem
import com.grimreich.world.ItemCatalogue
import com.grimreich.grimreich.v1.Item

class TradeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trade)

        val cityId = GameRepository.state.grimCurrentRegion
            ?: GameRepository.state.world.location.lowercase().replace(" ", "_")
        render(cityId)

        findViewById<Button>(R.id.btnExitTrade).setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        val cityId = GameRepository.state.grimCurrentRegion
            ?: GameRepository.state.world.location.lowercase().replace(" ", "_")
        render(cityId)
    }

    private fun render(cityId: String) {
        val g = GameRepository.state
        val market = CityMarketCatalog.getMarket(cityId)

        findViewById<TextView>(R.id.tradeStatus).text =
            "ZŁOTO: ${g.gold} | LOKALIZACJA: ${cityId.replace("_", " ").uppercase()}"

        val container = findViewById<LinearLayout>(R.id.goodsContainer)
        container.removeAllViews()

        // === SEKCJA: KUP TOWARY ===
        addSectionHeader(container, "🛍 KUP TOWARY")
        TradeGoodCatalog.goods.forEach { good ->
            val price = market?.getPrice(good.type) ?: good.basePrice
            addButton(container, "KUP ${good.name} (${price}g) - ${good.description}",
                "#80001A00", "#ADFF2F"
            ) {
                val result = TradeSystem.buyGood(cityId, good.type)
                UiUtils.showNarrativePopup(this, "TRANSAKCJA", result) { render(cityId) }
            }
        }

        // === SEKCJA: SKŁADNIKI ALCHEMICZNE ===
        addSectionHeader(container, "⚗ KUP SKŁADNIKI ALCHEMICZNE")
        val alchemyIngredients = listOf(
            Triple("ing_herb", "Swięte Ziele", 10),
            Triple("ing_root", "Prze klęty Korzeń", 15),
            Triple("ing_water", "Woda Święcona", 20)
        )
        alchemyIngredients.forEach { (id, name, basePrice) ->
            val price = (basePrice * 1.2).toInt() // Slight markup at merchants
            addButton(container, "KUP $name (${price}g) - Składnik alchemiczny",
                "#80001822", "#00FFCC"
            ) {
                if (g.gold >= price) {
                    g.gold -= price
                    val item = ItemCatalogue.findById(id)
                        ?: Item(id, name, "ingredient", null, basePrice, 0.1)
                    g.inventory.add(item)
                    UiUtils.showNarrativePopup(this, "TRANSAKCJA",
                        "Kupiono $name za $price złota."
                    ) { render(cityId) }
                } else {
                    UiUtils.showNarrativePopup(this, "TRANSAKCJA",
                        "Brak złota! (potrzeba $price)"
                    ) {}
                }
            }
        }

        // === SEKCJA: SPRZEDAJ PRZEDMIOTY ===
        addSectionHeader(container, "💰 SPRZEDAJ Z INWENTARZA")
        val inventory = g.inventory
        if (inventory.isEmpty()) {
            val tv = TextView(this)
            tv.text = "  (Inwentarz pusty)"
            tv.setTextColor(android.graphics.Color.GRAY)
            tv.setPadding(16, 8, 16, 8)
            container.addView(tv)
        } else {
            // Group by id for display
            inventory.distinctBy { it.id }.forEach { item ->
                val count = inventory.count { it.id == item.id }
                val sellPrice = (item.value * 0.6).toInt().coerceAtLeast(1)
                addButton(container,
                    "SPRZEDAJ ${item.name} x$count (${sellPrice}g/szt) - wartość: ${item.value}g",
                    "#80200000", "#FFD700"
                ) {
                    val result = TradeSystem.sellItem(item, cityId)
                    UiUtils.showNarrativePopup(this, "TRANSAKCJA", result) { render(cityId) }
                }
            }
        }
    }

    private fun addSectionHeader(container: LinearLayout, title: String) {
        val tv = TextView(this)
        tv.text = title
        tv.setTextColor(android.graphics.Color.parseColor("#FFD700"))
        tv.textSize = 16f
        tv.setPadding(0, 24, 0, 8)
        tv.setTypeface(null, android.graphics.Typeface.BOLD)
        container.addView(tv)
    }

    private fun addButton(
        container: LinearLayout,
        label: String,
        bgColor: String,
        textColor: String,
        onClick: () -> Unit
    ) {
        val btn = Button(this).apply {
            text = label
            setBackgroundColor(android.graphics.Color.parseColor(bgColor))
            setTextColor(android.graphics.Color.parseColor(textColor))
            setPadding(16, 16, 16, 16)
            textSize = 13f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { it.setMargins(0, 0, 0, 6) }
            setOnClickListener { onClick() }
        }
        container.addView(btn)
    }
}
