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
import com.grimreich.systems.QuestCategory
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
    onDialogue: () -> Unit,
    onExit: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // BACKGROUND
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
            // HEADER
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
                // LEFT: Nav Actions (SCROLLABLE)
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier.width(180.dp).fillMaxHeight().verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CityNavBtn("POWRÓT DO HUB", onExit, color = Color(0xFF400000))
                    
                    Spacer(modifier = Modifier.height(10.dp))

                    CityNavBtn("TARG", onMarket)
                    CityNavBtn("ALCHEMIA", onAlchemy)
                    CityNavBtn("KARCZMA", onTavern)
                    CityNavBtn("KAPLICA", onTemple)
                    CityNavBtn("WERBUNEK", onRecruit)
                    
                    val qCount = state.activeQuestsCount
                    CityNavBtn(
                        text = if (qCount > 0) "QUESTY ($qCount)" else "BRAK ZADAŃ",
                        onClick = { viewModel.toggleQuestMenu(true) },
                        color = if (qCount > 0) Color(0xFF4A6000) else Color(0xFF1A1A1A),
                        enabled = qCount > 0
                    )
                    
                    Spacer(modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.width(16.dp))

                // RIGHT: NPC List & Lore
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
                    
                    Text("MIESZKAŃCY:", color = Color(0xFFC0A060), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))

                    if (state.npcs.isEmpty()) {
                        Text("Ulice są puste...", color = Color.DarkGray, fontSize = 14.sp)
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(0.6f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(state.npcs) { npc ->
                                NpcRow(npc.name, npc.role) {
                                    viewModel.startDialogue(npc.name, npc.role, npc.startNodeId ?: "end", onDialogue)
                                }
                            }
                        }
                    }
                }
            }
        }

        // QUEST SELECTION OVERLAY
        if (state.isQuestMenuOpen) {
            AlertDialog(
                onDismissRequest = { viewModel.toggleQuestMenu(false) },
                title = { Text("WYBIERZ ZADANIE", color = Color(0xFFC0A060)) },
                text = {
                    if (state.activeLocalQuests.isEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                            Text("Brak aktywnych zadań dla tej lokacji.", color = Color.Gray, fontSize = 14.sp)
                        }
                    } else {
                        LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                            items(state.activeLocalQuests) { quest ->
                                val color = when(quest.category) {
                                    QuestCategory.COMBAT -> Color(0xFFB22222)
                                    QuestCategory.SOCIAL -> Color(0xFF4682B4)
                                    QuestCategory.INVESTIGATION -> Color(0xFFDAA520)
                                    QuestCategory.MIXED -> Color(0xFF9932CC)
                                }
                                val icon = when(quest.category) {
                                    QuestCategory.COMBAT -> "⚔️"
                                    QuestCategory.SOCIAL -> "💬"
                                    QuestCategory.INVESTIGATION -> "🔍"
                                    QuestCategory.MIXED -> "💠"
                                }

                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable { 
                                            viewModel.selectQuestAndOpenDialogue(quest, onDialogue)
                                        },
                                    color = Color(0xFF0F0F0F),
                                    shape = MaterialTheme.shapes.extraSmall,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(icon, fontSize = 18.sp, modifier = Modifier.padding(end = 8.dp))
                                            Text(
                                                text = quest.title.uppercase(),
                                                color = Color.White,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 13.sp,
                                                letterSpacing = 1.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = quest.description,
                                            color = Color.Gray,
                                            fontSize = 10.sp,
                                            lineHeight = 12.sp
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("LVL: ${quest.recommendedLevel}", color = color, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            Text("${quest.rewardGold} G", color = Color(0xFFE0C080), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { viewModel.toggleQuestMenu(false) }) {
                        Text("ZAMKNIJ", color = Color.Gray)
                    }
                },
                containerColor = Color(0xFF050505),
                shape = MaterialTheme.shapes.extraSmall
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
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            disabledContainerColor = Color(0xFF0F0F0F)
        ),
        shape = MaterialTheme.shapes.extraSmall,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF333333))
    ) {
        Text(
            text = text, 
            color = if (enabled) Color(0xFFE0C080) else Color.DarkGray, 
            fontSize = 12.sp, 
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun NpcRow(name: String, role: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        color = Color(0xFF111111),
        shape = MaterialTheme.shapes.extraSmall,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF222222))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = name, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Text(text = role.uppercase(), color = Color(0xFFC0A060), fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
    }
}
