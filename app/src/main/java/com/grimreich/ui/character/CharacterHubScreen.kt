package com.grimreich.ui.character

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimreich.core.Hero
import com.grimreich.grimreich.v1.Item
import com.grimreich.ui.inventory.HeroPaperDoll
import com.grimreich.ui.inventory.InventoryItemBox
import com.grimreich.ui.inventory.ItemDetailCard

@Composable
fun CharacterHubScreen(
    viewModel: CharacterHubViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    CharacterHubContent(
        state = state,
        onEvent = viewModel::onEvent,
        onBack = onBack
    )
}

@Composable
fun CharacterHubContent(
    state: CharacterHubUiState,
    onEvent: (CharacterHubUiEvent) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(Color.Black)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("DRUŻYNA", color = Color(0xFFC0A060), fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF400000))) {
                        Text("POWRÓT")
                    }
                }
                
                // Hero Selector
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.heroes) { hero ->
                        HeroTabChip(
                            hero = hero,
                            isSelected = hero.id == state.selectedHeroId,
                            onClick = { onEvent(CharacterHubUiEvent.SelectHero(hero.id)) }
                        )
                    }
                }

                // Tab Selector
                TabRow(
                    selectedTabIndex = state.selectedTab.ordinal,
                    containerColor = Color.Black,
                    contentColor = Color(0xFFC0A060),
                    divider = {}
                ) {
                    CharacterHubTab.entries.forEach { tab ->
                        Tab(
                            selected = state.selectedTab == tab,
                            onClick = { onEvent(CharacterHubUiEvent.SelectTab(tab)) },
                            text = { Text(tab.name, fontSize = 10.sp) }
                        )
                    }
                }
            }
        },
        containerColor = Color.Black
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.Yellow)
            } else {
                state.selectedHero?.let { hero ->
                    when (state.selectedTab) {
                        CharacterHubTab.OVERVIEW -> HeroOverview(hero)
                        CharacterHubTab.EQUIPMENT -> HeroEquipment(hero, state.inventory, onEvent)
                        CharacterHubTab.PARTY -> PartyManagement(state.heroes, onEvent)
                    }
                }
            }
        }
    }
}

@Composable
fun HeroTabChip(hero: Hero, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        color = if (isSelected) Color(0xFFC0A060) else Color(0xFF1A1A1A),
        shape = MaterialTheme.shapes.extraSmall
    ) {
        Text(
            text = hero.name.uppercase(),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = if (isSelected) Color.Black else Color.Gray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun HeroOverview(hero: Hero) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("STATYSTYKI", color = Color(0xFFC0A060), fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        StatRow("SIŁA", hero.strength)
        StatRow("ZRĘCZNOŚĆ", hero.agility)
        StatRow("INTELIGENCJA", hero.intelligence)
        StatRow("PERCEPCJA", hero.perception)
        StatRow("WYTRZYMAŁOŚĆ", hero.endurance)
        StatRow("CHARYZMA", hero.charisma)
        StatRow("POBOŻNOŚĆ", hero.piety)
    }
}

@Composable
fun StatRow(label: String, value: Int) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text(value.toString(), color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun HeroEquipment(hero: Hero, inventory: List<Item>, onEvent: (CharacterHubUiEvent) -> Unit) {
    var selectedItem by remember { mutableStateOf<Item?>(null) }
    
    Row(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column(modifier = Modifier.weight(1f)) {
            HeroPaperDoll(hero, inventory) { slot ->
                onEvent(CharacterHubUiEvent.UnequipItem(slot))
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1.2f)) {
            Text("PLECAK", color = Color(0xFFC0A060), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(inventory) { item ->
                    InventoryItemBox(item, isSelected = selectedItem?.instanceId == item.instanceId) {
                        selectedItem = item
                    }
                }
            }
            
            selectedItem?.let { item ->
                ItemDetailCard(item) {
                    onEvent(CharacterHubUiEvent.EquipItem(item.instanceId))
                }
            }
        }
    }
}

@Composable
fun PartyManagement(heroes: List<Hero>, onEvent: (CharacterHubUiEvent) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(heroes) { hero ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF111111),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF333333))
            ) {
                Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(hero.name.uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
                    Text(if (hero.isDead) "POLEGŁY" else "ZDRÓW", color = if (hero.isDead) Color.Red else Color.Green, fontSize = 10.sp)
                }
            }
        }
    }
}
