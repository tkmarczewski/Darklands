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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimreich.R
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
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is CharacterHubUiEffect.ShowMessage -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    CharacterHubContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent,
        onBack = onBack
    )
}

@Composable
fun CharacterHubContent(
    state: CharacterHubUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (CharacterHubUiEvent) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column(modifier = Modifier.background(Color.Black)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(R.string.label_party), color = Color(0xFFC0A060), fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF400000))) {
                        Text(stringResource(R.string.btn_back))
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
fun HeroTabChip(hero: HeroUi, isSelected: Boolean, onClick: () -> Unit) {
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
fun HeroOverview(hero: HeroUi) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(stringResource(R.string.label_stats), color = Color(0xFFC0A060), fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        StatRow(stringResource(R.string.stat_str), hero.combatStats.strength)
        StatRow(stringResource(R.string.stat_agi), hero.combatStats.agility)
        StatRow(stringResource(R.string.stat_int), hero.combatStats.intelligence)
        StatRow(stringResource(R.string.stat_per), hero.combatStats.perception)
        StatRow(stringResource(R.string.stat_end), hero.combatStats.endurance)
        StatRow(stringResource(R.string.stat_cha), hero.combatStats.charisma)
        StatRow(stringResource(R.string.stat_pie), hero.combatStats.piety)
        
        Spacer(modifier = Modifier.height(16.dp))
        Text(stringResource(R.string.label_combat), color = Color(0xFFC0A060), fontWeight = FontWeight.Bold)
        StatRow("ATAK", hero.combatStats.attack)
        StatRow("PANCERZ", hero.combatStats.armor)
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
fun HeroEquipment(hero: HeroUi, inventory: List<InventoryItemUi>, onEvent: (CharacterHubUiEvent) -> Unit) {
    var selectedItem by remember { mutableStateOf<InventoryItemUi?>(null) }
    
    Row(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column(modifier = Modifier.weight(1f)) {
            Text(stringResource(R.string.label_character), color = Color(0xFFC0A060), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            // HeroPaperDoll(hero, inventory) { slot -> onEvent(CharacterHubUiEvent.UnequipItem(slot)) }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1.2f)) {
            Text(stringResource(R.string.label_backpack), color = Color(0xFFC0A060), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(inventory) { item ->
                    InventoryItemBoxUi(item, isSelected = selectedItem?.instanceId == item.instanceId) {
                        selectedItem = item
                    }
                }
            }
            
            selectedItem?.let { item ->
                ItemDetailCardUi(item) {
                    onEvent(CharacterHubUiEvent.EquipItem(item.instanceId))
                }
            }
        }
    }
}

@Composable
fun InventoryItemBoxUi(item: InventoryItemUi, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        color = if (isSelected) Color(0xFF333333) else Color(0xFF111111),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) Color.Yellow else Color.DarkGray)
    ) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(item.name, color = Color.White, fontSize = 12.sp)
            Spacer(modifier = Modifier.weight(1f))
            if (item.isEquipped) {
                Text("[E]", color = Color.Green, fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun ItemDetailCardUi(item: InventoryItemUi, onEquip: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF222222))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(item.name.uppercase(), fontWeight = FontWeight.Bold, color = Color.Yellow)
            Text(item.type, fontSize = 10.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onEquip,
                modifier = Modifier.fillMaxWidth(),
                enabled = !item.isEquipped && item.canEquip
            ) {
                Text(if (item.isEquipped) stringResource(R.string.btn_equipped) else stringResource(R.string.btn_equip))
            }
        }
    }
}

@Composable
fun PartyManagement(heroes: List<HeroUi>, onEvent: (CharacterHubUiEvent) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(heroes) { hero ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = if (hero.isActiveHero) Color(0xFF222222) else Color(0xFF111111),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (hero.isActiveHero) Color(0xFFC0A060) else Color(0xFF333333))
            ) {
                Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(hero.name.uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
                    Text(hero.status.name, color = when(hero.status) {
                        HeroStatusUi.dead -> Color.Red
                        HeroStatusUi.wounded -> Color.Yellow
                        HeroStatusUi.alive -> Color.Green
                        else -> Color.Gray
                    }, fontSize = 10.sp)
                }
            }
        }
    }
}
