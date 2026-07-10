package com.grimreich.ui.city

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.grimreich.core.QuestCategory
import com.grimreich.ui.effects.glitchEffect
import com.grimreich.systems.QuestDefinition

@Composable
fun CityScreen(
    viewModel: CityViewModel,
    onMarket: () -> Unit,
    onAlchemy: () -> Unit,
    onTavern: () -> Unit,
    onTemple: () -> Unit,
    onRecruit: () -> Unit,
    onDialogue: (String, String, String) -> Unit,
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
        onEvent = viewModel::onEvent
    )
}

@Composable
fun CityContent(
    state: CityUiState,
    onEvent: (CityUiEvent) -> Unit
) {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        val bgResId = context.resources.getIdentifier(state.backgroundDrawable, "drawable", context.packageName)
        if (bgResId != 0) {
            Image(
                painter = painterResource(id = bgResId),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .glitchEffect(state.isGlitchActive, state.glitchIntensity),
                contentScale = ContentScale.Crop,
                alpha = 0.8f
            )
        }
        
        Box(modifier = Modifier.fillMaxSize().background(Color(0x60000000)))

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Surface(
                modifier = Modifier.fillMaxWidth().height(60.dp),
                color = Color(0xCC000000),
                shape = MaterialTheme.shapes.extraSmall,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC0A060))
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = state.cityName,
                        color = Color(0xFFE0C080),
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier.width(180.dp).fillMaxHeight().verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CityNavBtn(stringResource(R.string.city_btn_exit), { onEvent(CityUiEvent.OnExitClick) }, color = Color(0xFF400000))
                    Spacer(modifier = Modifier.height(10.dp))
                    CityNavBtn(stringResource(R.string.market_title), { onEvent(CityUiEvent.OnMarketClick) })
                    CityNavBtn(stringResource(R.string.city_alchemy), { onEvent(CityUiEvent.OnAlchemyClick) })
                    CityNavBtn(stringResource(R.string.city_tavern), { onEvent(CityUiEvent.OnTavernClick) })
                    CityNavBtn(stringResource(R.string.city_temple), { onEvent(CityUiEvent.OnTempleClick) })
                    CityNavBtn(stringResource(R.string.city_recruit), { onEvent(CityUiEvent.OnRecruitClick) })
                    
                    CityNavBtn(
                        text = "ZADANIA",
                        onClick = { onEvent(CityUiEvent.ToggleQuestMenu(true)) },
                        color = Color(0xFF4A6000)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    Surface(
                        color = Color(0x60000000),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.4f)
                            .padding(bottom = 12.dp),
                        shape = MaterialTheme.shapes.extraSmall
                    ) {
                        LazyColumn(modifier = Modifier.padding(10.dp)) {
                            item {
                                Text(
                                    text = state.cityStatus,
                                    color = Color.LightGray,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                    
                    Text("MIESZKAŃCY", color = Color(0xFFC0A060), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))

                    if (state.npcs.isEmpty()) {
                        Text("Pusto.", color = Color.DarkGray, fontSize = 14.sp)
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(0.6f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(state.npcs) { npc ->
                                NpcRow(npc.name, npc.role) {
                                    onEvent(CityUiEvent.OnNpcClick(npc))
                                }
                            }
                        }
                    }
                }
            }
        }

        if (state.isQuestMenuOpen) {
            AlertDialog(
                onDismissRequest = { onEvent(CityUiEvent.ToggleQuestMenu(false)) },
                title = { Text("TABLICA OGŁOSZEŃ", color = Color(0xFFC0A060)) },
                text = {
                    LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                        state.allAvailableQuests.forEach { (city, quests) ->
                            item { Text(city.uppercase(), color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                            items(quests) { quest ->
                                val color = when(quest.category.name) {
                                    "COMBAT" -> Color(0xFFB22222)
                                    "SOCIAL" -> Color(0xFF4682B4)
                                    "INVESTIGATION" -> Color(0xFFDAA520)
                                    "META" -> Color(0xFF551A8B)
                                    else -> Color(0xFF9932CC)
                                }
                                Surface(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { 
                                        onEvent(CityUiEvent.OnQuestClick(quest))
                                    },
                                    color = Color(0xFF0F0F0F),
                                    shape = MaterialTheme.shapes.extraSmall,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(text = quest.title.uppercase(), color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                                        Text(text = quest.description, color = Color.Gray, fontSize = 10.sp)
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
    }
}

@Composable
private fun CityNavBtn(text: String, onClick: () -> Unit, color: Color = Color(0xFF1A1A1A), enabled: Boolean = true) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth().height(44.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = MaterialTheme.shapes.extraSmall
    ) {
        Text(text = text, color = Color(0xFFE0C080), fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun NpcRow(name: String, role: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        color = Color(0xFF111111),
        shape = MaterialTheme.shapes.extraSmall
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = name, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Text(text = role.uppercase(), color = Color(0xFFC0A060), fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
    }
}
