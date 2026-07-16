package com.grimreich.ui.city

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.grimreich.R
import com.grimreich.ui.shared.*

@Composable
fun MarketScreen(
    viewModel: MarketViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Color.Black).padding(4.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // --- GÓRNY PASEK: MIASTO I ZŁOTO ---
            Row(
                modifier = Modifier.fillMaxWidth().height(30.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(R.string.market_label_title, state.cityName.uppercase()), color = Color(0xFFC0A060), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(painter = painterResource(id = R.drawable.ic_currency_gold), contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = stringResource(R.string.gold_format, state.playerGold), color = Color.Yellow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // --- KOKPIT HANDLOWY (3 KAFLE V9) ---
            Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                
                // 1. LEWY KAFEL: TWOJE ZAPASY (EKWIPUNEK)
                GothicObsidianCard(modifier = Modifier.weight(0.8f).fillMaxHeight(), headerColor = Color(0xFF1B5E20)) {
                    Text(text = stringResource(R.string.market_label_inventory), color = Color(0xFFC0A060), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    HorizontalDivider(color = Color(0x33C0A060), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                    
                    if (state.itemsToSell.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(stringResource(R.string.market_empty), color = Color.DarkGray, fontSize = 10.sp)
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            items(state.itemsToSell) { item ->
                                MarketItemRowV9(
                                    name = item.name,
                                    price = item.sellPrice,
                                    isBuy = false,
                                    onClick = { viewModel.sell(item.id) }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // 2. ŚRODKOWY KAFEL: OFERTA HANDLARZA
                GothicObsidianCard(modifier = Modifier.weight(1.2f).fillMaxHeight(), headerColor = Color(0xFF4A0000)) {
                    Text(text = stringResource(R.string.market_label_goods), color = Color(0xFFC0A060), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    HorizontalDivider(color = Color(0x33C0A060), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                    
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(state.itemsForSale) { item ->
                            MarketItemRowV9(
                                name = item.name,
                                price = item.price,
                                isBuy = true,
                                enabled = state.playerGold >= item.price,
                                onClick = { viewModel.buy(item.id) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // 3. PRAWY KAFEL: NAWIGACJA I KOMUNIKATY
                Column(modifier = Modifier.weight(0.7f).fillMaxHeight()) {
                    GothicObsidianCard(modifier = Modifier.weight(1f), headerColor = Color(0xFF0D47A1)) {
                        Text(text = stringResource(R.string.market_label_info), color = Color(0xFFC0A060), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        HorizontalDivider(color = Color(0x33C0A060), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                        
                        state.errorMessage?.let {
                            Text(text = stringResource(R.string.market_label_warning, it), color = Color.Red, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        } ?: run {
                            Text(text = stringResource(R.string.market_log_waiting), color = Color.Gray, fontSize = 10.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    GothicObsidianCard(modifier = Modifier.weight(0.6f), headerColor = Color(0xFF400000)) {
                        Text(text = stringResource(R.string.tavern_label_exit), color = Color(0xFFC0A060), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        NavTabV9(stringResource(R.string.tavern_btn_return_city), onClick = onBack, color = Color(0xFF400000))
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // --- DOLNY PASEK: DRUŻYNA (V9) ---
            GothicObsidianCard(modifier = Modifier.fillMaxWidth().height(80.dp)) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    state.party.forEach { hero ->
                        HeroPortraitV9(hero = hero, onClick = { /* Inspect hero if needed */ })
                    }
                }
            }
        }
    }
}

@Composable
fun MarketItemRowV9(
    name: String,
    price: Int,
    isBuy: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onClick() },
        color = if (enabled) Color(0xFF111111) else Color(0xFF050505),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (enabled) Color(0xFF333333) else Color(0xFF111111))
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = name.uppercase(), color = if (enabled) Color.White else Color.DarkGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = "${if (isBuy) stringResource(R.string.market_btn_buy) else stringResource(R.string.market_btn_sell)}: ${stringResource(R.string.gold_format, price)}",
                    color = if (enabled) Color(0xFFC0A060) else Color.Red, 
                    fontSize = 9.sp
                )
            }
            if (enabled) {
                Text(text = ">", color = Color(0xFFC0A060), fontWeight = FontWeight.Bold)
            }
        }
    }
}
