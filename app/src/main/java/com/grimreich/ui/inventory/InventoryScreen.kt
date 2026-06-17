package com.grimreich.ui.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimreich.grimreich.v1.Item
import com.grimreich.core.Hero

@Composable
fun InventoryScreen(viewModel: InventoryViewModel, onBack: () -> Unit) {
    val state by viewModel.uiState.collectAsState()
    val hero = state.activeHero

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp)
    ) {
        // HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PLECAK DRUŻYNY",
                color = Color(0xFFE0C080),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A2A2A))) {
                Text("POWRÓT", color = Color(0xFFE0C080), fontSize = 10.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxSize()) {
            // LEFT: Hero Stats and Equipment
            Column(modifier = Modifier.weight(1f)) {
                if (hero != null) {
                    HeroStatusCard(hero)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("EKWIPUNEK", color = Color.Gray, fontSize = 10.sp)
                    // Simplified equipment slots
                    EquipmentRow("BROŃ", hero.equipment["weapon"] ?: "-")
                    EquipmentRow("ZBROJA", hero.equipment["armor"] ?: "-")
                    EquipmentRow("GŁOWA", hero.equipment["helmet"] ?: "-")
                    EquipmentRow("TARCZA", hero.equipment["shield"] ?: "-")
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // RIGHT: Inventory Grid
            Column(modifier = Modifier.weight(1.5f)) {
                Text("PRZEDMIOTY", color = Color.Gray, fontSize = 10.sp)
                Spacer(modifier = Modifier.height(8.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.inventory) { item ->
                        InventoryItemBox(
                            item = item,
                            isSelected = state.selectedItem?.id == item.id,
                            onClick = { viewModel.selectItem(item) }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Item Details
                state.selectedItem?.let { item ->
                    ItemDetailCard(item)
                }
            }
        }
    }
}

@Composable
fun HeroStatusCard(hero: Hero) {
    Surface(
        color = Color(0xFF1E1E1E),
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(hero.name, color = Color(0xFFE0C080), fontWeight = FontWeight.Bold)
            Text("HP: ${hero.hp}/${hero.maxHp}", color = Color.White, fontSize = 12.sp)
            Text("SIŁA: ${hero.strength}", color = Color.LightGray, fontSize = 10.sp)
            Text("ZRĘCZ: ${hero.agility}", color = Color.LightGray, fontSize = 10.sp)
            Text("INTEL: ${hero.intelligence}", color = Color.LightGray, fontSize = 10.sp)
        }
    }
}

@Composable
fun EquipmentRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .background(Color(0xFF1A1A1A))
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray, fontSize = 10.sp)
        Text(value, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun InventoryItemBox(item: Item, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable { onClick() },
        color = if (isSelected) Color(0xFF3D3D3D) else Color(0xFF252525),
        shape = MaterialTheme.shapes.extraSmall,
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0C080)) else null
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(item.name.take(2).uppercase(), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ItemDetailCard(item: Item) {
    Surface(
        color = Color(0xFF2A2A2A),
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(item.name, color = Color(0xFFE0C080), fontWeight = FontWeight.Bold)
            Text(item.type.uppercase(), color = Color.Gray, fontSize = 10.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text("WARTOŚĆ: ${item.value} G", color = Color.White, fontSize = 10.sp)
            Text("WAGA: ${item.weight} kg", color = Color.White, fontSize = 10.sp)
        }
    }
}
