package com.grimreich.ui

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.grimreich.R
import com.grimreich.core.GameRepository
import com.grimreich.core.TradeGoodCatalog
import com.grimreich.core.TradeGoodType
import com.grimreich.core.CityMarketCatalog
import com.grimreich.systems.TradeSystem

class TradeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trade)

        val cityId = GameRepository.state.world.location.lowercase()
        render(cityId)
    }

    private fun render(cityId: String) {
        val g = GameRepository.state
        val market = CityMarketCatalog.getMarket(cityId)
        
        findViewById<TextView>(R.id.tradeStatus).text = "Twoje złoto: ${g.gold}\nLokalizacja: ${cityId.uppercase()}"
        
        val container = findViewById<LinearLayout>(R.id.goodsContainer)
        container.removeAllViews()
        
        TradeGoodCatalog.goods.forEach { good ->
            val price = market?.getPrice(good.type) ?: good.basePrice
            val btn = Button(this)
            btn.text = "Kup ${good.name} ($price g)"
            btn.setOnClickListener {
                val result = TradeSystem.buyGood(cityId, good.type)
                findViewById<TextView>(R.id.tvTradeResult).text = result
                render(cityId)
            }
            container.addView(btn)
        }
    }
}
