package com.grimreich.ui.city

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun MarketScreen(
    viewModel: MarketViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("TARG", color = Color(0xFFE0C080), fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("Złoto: ${state.playerGold}", color = Color.Yellow, fontSize = 14.sp)
        }

        Text(state.cityName, color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(bottom = 12.dp))

        Row(modifier = Modifier.weight(1f)) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text("DO KUPIENIA", color = Color(0xFF888844), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(state.itemsForSale) { item ->
                        MarketItemRow(
                            name = item.name,
                            price = item.price,
                            actionLabel = "KUP",
                            enabled = state.playerGold >= item.price,
                            onClick = { viewModel.buy(item.id) }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF400000)),
                    shape = MaterialTheme.shapes.extraSmall
                ) {
                    Text("POWRÓT", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                Text("TWÓJ EKWIPUNEK", color = Color(0xFF888844), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                LazyColumn {
                    items(state.itemsToSell) { item ->
                        MarketItemRow(
                            name = item.name,
                            price = item.sellPrice,
                            actionLabel = "SPRZEDAJ",
                            enabled = true,
                            onClick = { viewModel.sell(item.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MarketItemRow(
    name: String,
    price: Int,
    actionLabel: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = Color(0xFF0E0E0E),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2A2A1A)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text("$price zł", color = if (enabled) Color(0xFFE0C080) else Color.Red, fontSize = 11.sp)
            }
            Button(
                onClick = onClick,
                enabled = enabled,
                modifier = Modifier.height(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2A4000),
                    disabledContainerColor = Color(0xFF1A1A1A)
                )
            ) {
                Text(actionLabel, fontSize = 10.sp)
            }
        }
    }
}
