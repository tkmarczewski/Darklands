package com.grimreich.ui.main

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimreich.systems.QuestDefinition
import com.grimreich.systems.Encounter
import com.grimreich.systems.EncounterChoice
import com.grimreich.ui.shared.*

@Composable
fun ExpeditionScreen(
    viewModel: ExpeditionViewModel,
    onBack: () -> Unit,
    onCombat: () -> Unit,
    onDialogue: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                ExpeditionUiEffect.NavigateToCombat -> onCombat()
                ExpeditionUiEffect.NavigateToDialogue -> onDialogue()
                ExpeditionUiEffect.NavigateBack -> onBack()
            }
        }
    }

    ExpeditionContent(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun ExpeditionContent(
    state: ExpeditionUiState,
    onEvent: (ExpeditionUiEvent) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black).padding(4.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // --- GÓRNY PASEK STATUSU ---
            Row(
                modifier = Modifier.fillMaxWidth().height(30.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "EKSPLORACJA TERENU", color = Color(0xFFC0A060), fontSize = 12.sp)
                Text(text = state.regionName.uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = "AGRESJA: WYSOKA", color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(4.dp))

            // --- KOKPIT EKSPEDYCYJNY (3 KAFLE V9) ---
            Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                
                // 1. LEWY KAFEL: LOGI I ZAPISY TRYBUNAŁU
                GothicObsidianCard(modifier = Modifier.weight(0.7f).fillMaxHeight()) {
                    Text(text = "ZAPISY TERENOWE", color = Color(0xFFC0A060), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Divider(color = Color(0x33C0A060), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                    
                    if (state.content is ExpeditionContentState.EncounterLog) {
                        Text(
                            text = (state.content as ExpeditionContentState.EncounterLog).message,
                            color = Color.LightGray,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        NavTabV9("ZROZUMIAŁEM", onClick = { onEvent(ExpeditionUiEvent.OnDismissEncounter) })
                    } else {
                        Text(text = "> Sensory rejestrują anomalie w strukturze mgły...", color = Color.Gray, fontSize = 10.sp)
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // 2. ŚRODKOWY KAFEL: GŁÓWNA AKCJA / ENCOUNTER
                GothicObsidianCard(modifier = Modifier.weight(1.3f).fillMaxHeight(), headerColor = Color(0xFFE65100)) {
                    when (val content = state.content) {
                        ExpeditionContentState.Loading -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = Color(0xFFC0A060))
                            }
                        }
                        is ExpeditionContentState.EncounterActive -> {
                            EncounterViewV9(content.encounter, onChoice = { onEvent(ExpeditionUiEvent.OnEncounterChoiceClick(it)) })
                        }
                        else -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("OCZEKIWANIE NA KONTAKT", color = Color.DarkGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // 3. PRAWY KAFEL: CELE I NAWIGACJA
                Column(modifier = Modifier.weight(0.7f).fillMaxHeight()) {
                    GothicObsidianCard(modifier = Modifier.weight(1f), headerColor = Color(0xFF1B5E20)) {
                        Text(text = "CELE AKTYWNE", color = Color(0xFFC0A060), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Divider(color = Color(0x33C0A060), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                        
                        val quests = (state.content as? ExpeditionContentState.QuestList)?.quests ?: emptyList()
                        if (quests.isEmpty()) {
                            Text("Brak celów.", color = Color.DarkGray, fontSize = 11.sp)
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                items(quests) { quest ->
                                    QuestActionCardV9(quest) { onEvent(ExpeditionUiEvent.OnQuestClick(quest.id)) }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    GothicObsidianCard(modifier = Modifier.weight(0.5f), headerColor = Color(0xFF400000)) {
                        Text(text = "POWRÓT", color = Color(0xFFC0A060), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        NavTabV9("ODWRÓT DO HUBU", onClick = { onEvent(ExpeditionUiEvent.OnBackClick) }, color = Color(0xFF400000))
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // --- DOLNY PASEK: INFORMACYJNY ---
            GothicObsidianCard(modifier = Modifier.fillMaxWidth().height(40.dp)) {
                Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Text("STATUS KOTWICY: STABILNY | ZAPIS LOGÓW AKTYWNY", color = Color.DarkGray, fontSize = 9.sp)
                }
            }
        }
    }
}

@Composable
fun EncounterViewV9(encounter: Encounter, onChoice: (EncounterChoice) -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = encounter.title.uppercase(), color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = encounter.description, color = Color.LightGray, fontSize = 13.sp, lineHeight = 18.sp)
        
        Spacer(modifier = Modifier.weight(1f))
        
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            encounter.choices.forEach { choice ->
                NavTabV9(text = choice.label, onClick = { onChoice(choice) }, color = Color(0xFF151515))
            }
        }
    }
}

@Composable
fun QuestActionCardV9(quest: QuestDefinition, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        color = Color(0xFF0A0A0A),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF333333))
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = quest.title.uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Text(text = "POZIOM: ${quest.recommendedLevel}", color = Color.Gray, fontSize = 9.sp)
        }
    }
}
