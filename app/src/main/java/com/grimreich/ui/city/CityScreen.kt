package com.grimreich.ui.city

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
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
import com.grimreich.R
import com.grimreich.core.GameRepository
import com.grimreich.world.ProceduralNpcGenerator
import com.grimreich.systems.QuestRegistry
import com.grimreich.systems.QuestSystem
import com.grimreich.world.CityCatalogue

@Composable
fun CityScreen(
    viewModel: CityViewModel,
    onMarket: () -> Unit,
    onTavern: () -> Unit,
    onTemple: () -> Unit,
    onRecruit: () -> Unit,
    onNpcClick: (String, String, String) -> Unit,
    onExit: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    val currentCityId = GameRepository.state.grimCurrentRegion ?: "wybrzeze_polnocne"
    val npcs = state.npcs

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // BACKGROUND
        val bgResId = context.resources.getIdentifier(state.backgroundDrawable, "drawable", context.packageName)
        if (bgResId != 0) {
            Image(
                painter = painterResource(id = bgResId),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.6f
            )
        }
        
        Box(modifier = Modifier.fillMaxSize().background(Color(0xB0000000)))

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            // HEADER
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xD0000000),
                shape = MaterialTheme.shapes.extraSmall,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC0A060))
            ) {
                Text(
                    text = state.cityName,
                    color = Color(0xFFE0C080),
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    modifier = Modifier.padding(8.dp).align(Alignment.CenterHorizontally)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                // LEFT: Nav Actions
                Column(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CityNavButton("TARG", onClick = onMarket)
                    CityNavButton("KARCZMA", onClick = onTavern)
                    CityNavButton("KAPLICA", onClick = onTemple)
                    CityNavButton("WERBUNEK", onClick = onRecruit)
                    
                    val questBtnText = if (state.activeQuestsCount > 0) "QUEST (${state.activeQuestsCount})" else "BRAK ZADAŃ"
                    CityNavButton(
                        text = questBtnText, 
                        color = if (state.activeQuestsCount > 0) Color(0xFFADFF2F) else Color(0xFF2A2A2A), 
                        enabled = state.activeQuestsCount > 0,
                        onClick = { 
                            val quest = com.grimreich.core.GameRepository.state.quest.activeQuests
                                .mapNotNull { com.grimreich.systems.QuestSystem.getQuest(it) }
                                .find { it.cityId == currentCityId }
                                ?: com.grimreich.systems.QuestSystem.availableForCity(currentCityId).firstOrNull()
                                
                            if (quest != null) {
                                // Dynamic node resolution
                                val node = when {
                                    quest.id.startsWith("q_verdict") -> "${quest.id}_start"
                                    quest.originRefId == "aelion" -> "aelion_start"
                                    else -> "mystic_start" // Fallback to mystic for procedural
                                }
                                onNpcClick(quest.originRefId, quest.originRefId, node)
                            }
                        }
                    )
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    Button(
                        onClick = onExit,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5A1A1A)),
                        shape = MaterialTheme.shapes.extraSmall,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF900000))
                    ) {
                        Text("WYJDŹ Z MIASTA", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // RIGHT: NPC List and Lore
                Column(modifier = Modifier.weight(1.5f).fillMaxHeight()) {
                    Surface(
                        color = Color(0x60000000),
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.extraSmall
                    ) {
                        Text(
                            text = state.cityStatus,
                            color = Color(0xFFE0C080),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text("MIESZKAŃCY:", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    
                    LazyColumn(
                        modifier = Modifier.weight(1f).padding(top = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(npcs) { npc ->
                            NpcListItem(name = npc.name, role = npc.role) {
                                onNpcClick(npc.name, npc.role, npc.startNodeId ?: "end")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CityNavButton(text: String, color: Color = Color(0xFF1A1A1A), enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth().height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color, 
            disabledContainerColor = Color(0xFF0A0A0A)
        ),
        shape = MaterialTheme.shapes.extraSmall,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (enabled) Color(0xFF444444) else Color(0xFF222222))
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
fun NpcListItem(name: String, role: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        color = Color(0xFF151515),
        shape = MaterialTheme.shapes.extraSmall,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF252525))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = role.uppercase(), color = Color(0xFFC0A060), fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
