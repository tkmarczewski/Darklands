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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimreich.R
import com.grimreich.ui.shared.*
import com.grimreich.ui.effects.glitchEffect

@Composable
fun CityScreen(
    viewModel: CityViewModel,
    onMarket: () -> Unit,
    onAlchemy: () -> Unit,
    onTavern: () -> Unit,
    onTemple: () -> Unit,
    onRecruit: () -> Unit,
    onDialogue: (String, String, String) -> Unit,
    onMap: () -> Unit = {},
    onInventory: () -> Unit = {},
    onChronicle: () -> Unit = {},
    onQuests: () -> Unit = {},
    onExit: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is CityUiEffect.NavigateToDialogue -> onDialogue(effect.name, effect.role, effect.node)
                CityUiEffect.NavigateToMarket -> onMarket()
                CityUiEffect.NavigateToAlchemy -> onAlchemy()
                CityUiEffect.NavigateToTavern -> onTavern()
                CityUiEffect.NavigateToTemple -> onTemple()
                CityUiEffect.NavigateToRecruit -> onRecruit()
                CityUiEffect.NavigateToExit -> onExit()
            }
        }
    }

    CityContent(
        state = state,
        onEvent = viewModel::onEvent,
        onCharacter = { /* Można tu dodać podgląd postaci */ }
    )

    // --- EXPANDING QUILL MENU (V9 INTEGRATION) ---
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
        ExpandingQuillMenu(
            onMap = onMap,
            onInventory = onInventory,
            onChronicle = onChronicle,
            onQuests = onQuests
        )
    }
}

@Composable
fun CityContent(
    state: CityUiState,
    onEvent: (CityUiEvent) -> Unit,
    onCharacter: (String) -> Unit = {}
) {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize().background(Color.Black).padding(4.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // --- GÓRNY PASEK (Zgodny z Hubem) ---
            Row(
                modifier = Modifier.fillMaxWidth().height(30.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = state.rulingFactionName.uppercase(), color = Color(0xFFC0A060), fontSize = 12.sp)
                Text(text = state.cityName.uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = "REGION: PÓŁNOC", color = Color.Gray, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(4.dp))

            // --- KOKPIT MIEJSKI (3 KAFLE V9) ---
            Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                
                // 1. LEWY KAFEL: STATUS MIASTA I LOGI
                GothicObsidianCard(modifier = Modifier.weight(0.7f).fillMaxHeight()) {
                    Text(text = stringResource(R.string.city_manifest_label), color = Color(0xFFC0A060), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    HorizontalDivider(color = Color(0x33C0A060), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            Text(text = state.cityStatus, color = Color.LightGray, fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // 2. ŚRODKOWY KAFEL: WIZJA MIEJSCA
                GothicObsidianCard(modifier = Modifier.weight(1.3f).fillMaxHeight(), headerColor = Color(0xFF4A0000)) {
                    val bgResId = context.resources.getIdentifier(state.backgroundDrawable, "drawable", context.packageName)
                    if (bgResId != 0) {
                        Image(
                            painter = painterResource(id = bgResId),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .glitchEffect(state.isGlitchActive, state.glitchIntensity),
                            contentScale = ContentScale.Crop,
                            alpha = 0.7f
                        )
                    }
                    
                    // NAKŁADKA MIESZKAŃCÓW (Diegetycznie na wizji)
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomStart) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(androidx.compose.ui.graphics.Brush.verticalGradient(listOf(Color.Transparent, Color(0xCC000000))))
                                .padding(8.dp)
                        ) {
                            Text(stringResource(R.string.city_npcs_label), color = Color(0xFFC0A060), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                state.npcs.forEach { npc ->
                                    Surface(
                                        modifier = Modifier.clickable { onEvent(CityUiEvent.OnNpcClick(npc)) },
                                        color = Color(0xFF1A1A1A),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF333333))
                                    ) {
                                        Text(npc.name, color = Color.White, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // 3. PRAWY KAFEL: LOKACJE I ZADANIA
                Column(modifier = Modifier.weight(0.7f).fillMaxHeight()) {
                    GothicObsidianCard(modifier = Modifier.weight(1.2f), headerColor = Color(0xFF1B5E20)) {
                        Text(text = stringResource(R.string.city_locations_label), color = Color(0xFFC0A060), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(top = 4.dp)) {
                            NavTabV9(stringResource(R.string.city_btn_market), onClick = { onEvent(CityUiEvent.OnMarketClick) })
                            NavTabV9(stringResource(R.string.city_btn_tavern), onClick = { onEvent(CityUiEvent.OnTavernClick) })
                            NavTabV9(stringResource(R.string.city_btn_temple), onClick = { onEvent(CityUiEvent.OnTempleClick) })
                            NavTabV9(stringResource(R.string.city_btn_alchemy), onClick = { onEvent(CityUiEvent.OnAlchemyClick) })
                            NavTabV9(stringResource(R.string.city_btn_recruit), onClick = { onEvent(CityUiEvent.OnRecruitClick) })
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    GothicObsidianCard(modifier = Modifier.weight(0.8f), headerColor = Color(0xFF0D47A1)) {
                        Text(text = stringResource(R.string.menu_quests), color = Color(0xFFC0A060), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        NavTabV9(stringResource(R.string.city_btn_quest_board), onClick = { onEvent(CityUiEvent.ToggleQuestMenu(true)) }, modifier = Modifier.padding(top = 4.dp), color = Color(0xFF1A237E))
                        Spacer(modifier = Modifier.height(4.dp))
                        NavTabV9(stringResource(R.string.city_btn_exit_city), onClick = { onEvent(CityUiEvent.OnExitClick) }, color = Color(0xFF400000))
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // --- DOLNY PASEK: DRUŻYNA (Ten sam co w Hubie) ---
            // Uwaga: state.party nie ma w CityUiState bezpośrednio, CityViewModel musi go udostępniać
            // Dla MVP użyjemy GothicObsidianCard z informacją o wyjściu, ale docelowo tu powinien być pasek party.
            GothicObsidianCard(modifier = Modifier.fillMaxWidth().height(60.dp)) {
                Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Text(stringResource(R.string.city_party_management_hint), color = Color.DarkGray, fontSize = 10.sp)
                }
            }
        }

        // QUEST MENU MODAL
        if (state.isQuestMenuOpen) {
            QuestBoardModal(state = state, onEvent = onEvent)
        }
    }
}

@Composable
fun QuestBoardModal(state: CityUiState, onEvent: (CityUiEvent) -> Unit) {
    AlertDialog(
        onDismissRequest = { onEvent(CityUiEvent.ToggleQuestMenu(false)) },
        title = { Text("TABLICA OGŁOSZEŃ", color = Color(0xFFC0A060), fontSize = 18.sp, fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                state.allAvailableQuests.forEach { (city, quests) ->
                    item { Text(city.uppercase(), color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp)) }
                    items(quests) { quest ->
                        Surface(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onEvent(CityUiEvent.OnQuestClick(quest)) },
                            color = Color(0xFF0F0F0F),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF333333))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = quest.title.uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(text = quest.description, color = Color.Gray, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = { onEvent(CityUiEvent.ToggleQuestMenu(false)) }) {
                Text("ZAMKNIJ", color = Color.Gray)
            }
        },
        containerColor = Color(0xFF050505)
    )
}
