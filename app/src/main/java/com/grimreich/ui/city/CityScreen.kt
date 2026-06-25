package com.grimreich.ui.city

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimreich.ui.effects.glitchEffect

@Composable
fun CityScreen(
    viewModel: CityViewModel,
    onMarket: () -> Unit,
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
                    .glitchEffect(state.isGlitchActive, 1.5f),
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
                Box(contentAlignment = Alignment.Center) {
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
                // LEFT: Nav Actions
                Column(
                    modifier = Modifier.width(180.dp).fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CityNavBtn("TARG", onMarket)
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
                    
                    Button(
                        onClick = onExit,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF800000)),
                        shape = MaterialTheme.shapes.extraSmall,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF0000))
                    ) {
                        Text("WYJDŹ", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // RIGHT: NPC List & Lore
                Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    Surface(
                        color = Color(0x60000000),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        shape = MaterialTheme.shapes.extraSmall
                    ) {
                        Text(
                            text = state.cityStatus,
                            color = Color.LightGray,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                    
                    Text("MIESZKAŃCY:", color = Color(0xFFC0A060), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))

                    if (state.npcs.isEmpty()) {
                        Text("Ulice są puste...", color = Color.DarkGray, fontSize = 14.sp)
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
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
                        LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                            items(state.activeLocalQuests) { quest ->
                                Surface(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { 
                                        viewModel.selectQuestAndOpenDialogue(quest, onDialogue)
                                    },
                                    color = Color(0xFF111111),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF222222))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(quest.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(quest.originRefId.uppercase(), color = Color.Red, fontSize = 10.sp)
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
