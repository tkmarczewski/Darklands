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
import com.grimreich.core.GameConstants
import com.grimreich.systems.QuestDefinition
import com.grimreich.systems.Encounter
import com.grimreich.systems.EncounterChoice

@Composable
fun ExpeditionScreen(
    viewModel: ExpeditionViewModel,
    onBack: () -> Unit,
    onCombat: () -> Unit,
    onDialogue: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("EKSPLORACJA: ${state.regionName.uppercase()}", color = Color(0xFFC0A060), fontSize = 22.sp, fontWeight = FontWeight.Bold)
            
            Spacer(modifier = Modifier.height(24.dp))

            // Logic fix: Show encounter if active, otherwise show quests
            if (state.activeEncounter != null) {
                EncounterView(state.activeEncounter!!, onChoice = { viewModel.handleEncounterChoice(it) })
            } else if (state.encounterLog != null) {
                EncounterLogView(state.encounterLog!!, onDismiss = { viewModel.dismissEncounter() })
            } else {
                if (state.activeQuests.isEmpty()) {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text("Brak aktywnych celów w tym regionie.", color = Color.DarkGray)
                    }
                } else {
                    LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(state.activeQuests) { quest ->
                            QuestActionCard(quest) {
                                viewModel.startQuest(quest.id, onCombat, onDialogue)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF400000))
            ) {
                Text("POWRÓT")
            }
        }
    }
}

@Composable
fun EncounterView(encounter: Encounter, onChoice: (EncounterChoice) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        color = Color(0xFF151515),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Yellow)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(encounter.title.uppercase(), color = Color.Yellow, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(encounter.description, color = Color.White, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(16.dp))
            
            encounter.choices.forEach { choice ->
                Button(
                    onClick = { onChoice(choice) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF222222))
                ) {
                    Text(choice.label, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun EncounterLogView(log: String, onDismiss: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        color = Color(0xFF0A0A0A),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(log, color = Color.LightGray, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                Text("ZROZUMIAŁEM")
            }
        }
    }
}

@Composable
fun QuestActionCard(quest: QuestDefinition, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111111)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFADFF2F))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(quest.title, color = Color.White, fontWeight = FontWeight.Bold)
            Text(quest.description, color = Color.Gray, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("WYRUSZ >", color = Color.Yellow, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.End))
        }
    }
}
