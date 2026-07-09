package com.grimreich.ui.inventory

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.grimreich.R
import com.grimreich.grimreich.v1.Item
import com.grimreich.core.Hero

@Composable
fun InventoryScreen(viewModel: InventoryViewModel, onBack: () -> Unit) {
    val state by viewModel.uiState.collectAsState()
    val hero = state.activeHero
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // PANEL BACKGROUND
        val panelBg = context.resources.getIdentifier("ui_panel_inventory", "drawable", context.packageName)
        if (panelBg != 0) {
            Image(
                painter = painterResource(id = panelBg),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds,
                alpha = 0.7f
            )
        }

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            // HEADER
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.inventory_title),
                    color = Color(0xFFC0A060),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF400000)),
                    shape = MaterialTheme.shapes.extraSmall
                ) {
                    Text(stringResource(R.string.btn_back))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxSize()) {
                // LEFT: Hero Visual & Slots
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    // HERO SELECTOR
                    if (state.party.size > 1) {
                        LazyRow(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            items(state.party) { h ->
                                val isSelected = h.id == hero?.id
                                Box(
                                    modifier = Modifier
                                        .background(if (isSelected) Color(0xFFC0A060) else Color(0xFF1A1A1A), MaterialTheme.shapes.extraSmall)
                                        .clickable { viewModel.selectHero(h.id) }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(h.name.uppercase(), color = if (isSelected) Color.Black else Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    if (hero != null) {
                        HeroPaperDoll(hero, state.inventory) { slot -> 
                            viewModel.unequipItem(slot)
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // RIGHT: Inventory Grid & Details
                Column(modifier = Modifier.weight(1.2f)) {
                    Text(stringResource(R.string.inventory_items_label), color = Color(0xFFC0A060), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(5),
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(state.inventory) { item ->
                            InventoryItemBox(
                                item = item,
                                isSelected = state.selectedItem?.id == item.id,
                                onClick = { viewModel.selectItem(item) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ITEM DETAIL CARD
                    state.selectedItem?.let { item ->
                        ItemDetailCard(item) {
                            viewModel.equipItem()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HeroPaperDoll(hero: Hero, allItems: List<Item>, onUnequip: (String) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            color = Color(0x60000000),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC0A060)),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(hero.name.uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
                Text(stringResource(R.string.inventory_hp_format, hero.hp, hero.maxHp), color = Color.Red, fontSize = 12.sp)
                Text(stringResource(R.string.inventory_stats_format, hero.effectiveAttack(allItems), hero.effectiveDefense(allItems)), color = Color.Gray, fontSize = 10.sp)
            }
        }


        // Equipment Slots Layout
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                EquipmentSlot("helmet", hero.equipment["helmet"], allItems, onUnequip)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                EquipmentSlot("weapon", hero.equipment["weapon"], allItems, onUnequip)
                EquipmentSlot("armor", hero.equipment["armor"], allItems, onUnequip)
                EquipmentSlot("shield", hero.equipment["shield"], allItems, onUnequip)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                EquipmentSlot("accessory", hero.equipment["accessory"], allItems, onUnequip)
            }
        }
    }
}

@Composable
fun EquipmentSlot(slotName: String, itemId: String?, allItems: List<Item>, onClick: (String) -> Unit) {
    val context = LocalContext.current
    val item = itemId?.let { id -> allItems.find { it.id == id } }
    val iconResId = item?.properties?.get("icon")?.toString()?.let { 
        context.resources.getIdentifier(it, "drawable", context.packageName) 
    } ?: 0

    Surface(
        modifier = Modifier
            .size(50.dp)
            .clickable { onClick(slotName) },
        color = Color(0xFF151515),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (item != null) Color(0xFFC0A060) else Color.DarkGray)
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (iconResId != 0) {
                Image(painter = painterResource(id = iconResId), contentDescription = null, modifier = Modifier.size(32.dp))
            } else {
                Text(slotName.take(1).uppercase(), color = Color.DarkGray, fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun InventoryItemBox(item: Item, isSelected: Boolean, onClick: () -> Unit) {
    val context = LocalContext.current
    val iconResId = item.properties["icon"]?.toString()?.let { 
        context.resources.getIdentifier(it, "drawable", context.packageName) 
    } ?: 0

    Surface(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable { onClick() },
        color = if (isSelected) Color(0xFF303030) else Color(0xFF151515),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) Color.White else Color(0xFF222222))
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (iconResId != 0) {
                Image(painter = painterResource(id = iconResId), contentDescription = null, modifier = Modifier.size(24.dp))
            } else {
                Text(item.name.take(2).uppercase(), color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun ItemDetailCard(item: Item, onEquip: () -> Unit) {
    Surface(
        color = Color(0xFF101010),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF222222)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(item.name, color = Color(0xFFE0C080), fontWeight = FontWeight.Bold)
                if (item.slot != null) {
                    Button(
                        onClick = onEquip,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF203010)),
                        modifier = Modifier.height(30.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                    ) {
                        Text(stringResource(R.string.inventory_btn_equip), fontSize = 10.sp)
                    }
                }
            }
            Text(item.type.uppercase(), color = Color.Gray, fontSize = 9.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(item.lore, color = Color.LightGray, fontSize = 11.sp, lineHeight = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                val effects = item.effects.entries.joinToString(" | ") { "${it.key.uppercase()}: ${it.value}" }
                Text(effects, color = Color.Green, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text("${item.weight} kg", color = Color.DarkGray, fontSize = 9.sp)
            }
        }
    }
}
