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
    
    val currentCityId = GameRepository.state.grimCurrentRegion ?: ""
    val npcs = ProceduralNpcGenerator.generateForCity(currentCityId, 1)

    Box(modifier = Modifier.fillMaxSize()) {
        // BACKGROUND
        val bgResId = context.resources.getIdentifier(state.backgroundDrawable, "drawable", context.packageName)
        if (bgResId != 0) {
            Image(
                painter = painterResource(id = bgResId),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        
        Box(modifier = Modifier.fillMaxSize().background(Color(0xB0000000)))

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            // HEADER
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xD0000000),
                shape = MaterialTheme.shapes.extraSmall
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

            Row(modifier = Modifier.fillMaxSize()) {
                // LEFT: Nav Actions
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                            val currentCityId = GameRepository.state.grimCurrentRegion ?: ""
                            val quest = com.grimreich.core.GameRepository.state.quest.activeQuests
                                .mapNotNull { com.grimreich.systems.QuestSystem.getQuest(it) }
                                .find { it.cityId == currentCityId }
                                
                            if (quest != null) {
                                onNpcClick(quest.originRefId, quest.originRefId, "${quest.id}_start")
                            }
                        }
                    )
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    Button(
                        onClick = onExit,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5A1A1A))
                    ) {
                        Text("WYJDŹ Z MIASTA", color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // RIGHT: NPC List and Lore
                Column(modifier = Modifier.weight(1.5f)) {
                    Surface(
                        color = Color(0x60000000),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = state.cityStatus,
                            color = Color(0xFFE0C080),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text("MIESZKAŃCY:", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    
                    LazyColumn(
                        modifier = Modifier.weight(1f).padding(top = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(npcs) { npc ->
                            val hasActiveTasks = QuestSystem.all().any {
                                (it.originRefId.lowercase() == npc.name.lowercase() || it.originRefId.lowercase() == npc.role.lowercase()) &&
                                (it.status == com.grimreich.systems.QuestStatus.DOSTEPNE || it.status == com.grimreich.systems.QuestStatus.AKTYWNE)
                            }
                            
                            val isKnownQuestGiver = QuestRegistry.allTemplates.any { it.preferredCityId == currentCityId && it.id.contains(npc.role.lowercase()) }

                            if (!isKnownQuestGiver || hasActiveTasks) {
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
}

@Composable
fun CityNavButton(text: String, color: Color = Color(0xFF2A2A2A), enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth().height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color, disabledContainerColor = color.copy(alpha = 0.5f)),
        shape = MaterialTheme.shapes.extraSmall
    ) {
        Text(text = text, color = if (enabled) Color(0xFFE0C080) else Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun NpcListItem(name: String, role: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        color = Color(0xFF1A1A1A),
        shape = MaterialTheme.shapes.extraSmall
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = name, color = Color.White, fontWeight = FontWeight.Bold)
            Text(text = role.uppercase(), color = Color.Gray, fontSize = 10.sp)
        }
    }
}
