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
import com.grimreich.systems.QuestEntry
import com.grimreich.systems.Encounter
import com.grimreich.systems.EncounterChoice

@Composable
fun ExpeditionScreen(
    viewModel: ExpeditionViewModel,
    onBack: () -> Unit,
    onCombat: (QuestEntry) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var questToConfirm by remember { mutableStateOf<QuestEntry?>(null) }

    // React to Raids
    LaunchedEffect(state.raidCombatData) {
        state.raidCombatData?.let { (id, stats) ->
            onCombat(QuestEntry(id, stats.first, "", "", "", 0, hasCombat = true))
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(GameConstants.UI.PADDING_MEDIUM),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("WYPRAWA: ${state.regionName.uppercase()}", color = Color(0xFFC0A060), fontSize = 22.sp, fontWeight = FontWeight.Bold)
            
            state.encounterLog?.let { log ->
                Surface(
                    color = Color(0xFF111111),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray)
                ) {
                    Text(log, color = Color.White, modifier = Modifier.padding(8.dp), fontSize = 12.sp)
                }
            }

            Text("CELE W POBLIŻU:", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp, bottom = 16.dp))

            if (state.outsideQuests.isEmpty()) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text("Brak aktywnych celów poza murami miasta.", color = Color.DarkGray)
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(state.outsideQuests) { quest ->
                        ExpeditionQuestCard(quest) { questToConfirm = quest }
                    }
                }
            }

            Button(
                onClick = { viewModel.exitExpedition(onBack) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(GameConstants.UI.BUTTON_HEIGHT_DEFAULT)
                    .padding(top = GameConstants.UI.PADDING_MEDIUM),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF400000))
            ) {
                Text("POWRÓT DO HUB")
            }
        }

        // RANDOM ENCOUNTER OVERLAY
        state.activeEncounter?.let { encounter ->
            AlertDialog(
                onDismissRequest = { viewModel.dismissEncounter() },
                title = { Text(encounter.title, color = Color(0xFFE0C080)) },
                text = { Text(encounter.description, color = Color.White) },
                confirmButton = {
                    Column {
                        encounter.choices.forEach { choice ->
                            Button(
                                onClick = { viewModel.handleEncounterChoice(choice) },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A1A1A)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF333333))
                            ) {
                                Text(choice.label, color = Color(0xFFE0C080), fontSize = 12.sp)
                            }
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.dismissEncounter() }) {
                        Text("ZAMKNIJ", color = Color.Gray)
                    }
                },
                containerColor = Color(0xFF050505),
                shape = MaterialTheme.shapes.extraSmall
            )
        }

        // CONFIRMATION OVERLAY with Combat Warning
        questToConfirm?.let { quest ->
            AlertDialog(
                onDismissRequest = { questToConfirm = null },
                title = { Text("WYRUSZYĆ?", color = Color.Yellow) },
                text = {
                    Column {
                        Text(quest.title, color = Color.White, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        if (quest.hasCombat) {
                            Text("Uwaga: Wyjście poza miasto wiąże się z walką.", color = Color.Red, fontSize = 12.sp)
                        } else {
                            Text("Cel pokojowy. Powrót do hub po ukończeniu.", color = Color.Green, fontSize = 12.sp)
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (quest.hasCombat) {
                            viewModel.startQuestCombat(quest) {
                                onCombat(quest)
                                questToConfirm = null
                            }
                        } else {
                            viewModel.completeNonCombatQuest(quest) {
                                questToConfirm = null
                            }
                        }
                    }) {
                        Text("WYRUSZ")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { questToConfirm = null }) {
                        Text("ANULUJ", color = Color.Gray)
                    }
                },
                containerColor = Color(0xFF151515),
                shape = MaterialTheme.shapes.extraSmall
            )
        }
    }
}

@Composable
private fun ExpeditionQuestCard(quest: QuestEntry, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF101010)),
        border = androidx.compose.foundation.BorderStroke(GameConstants.UI.BORDER_WIDTH, Color(0xFFADFF2F))
    ) {
        Column(modifier = Modifier.padding(GameConstants.UI.PADDING_MEDIUM)) {
            Text(quest.title, color = Color.White, fontWeight = FontWeight.Bold)
            Text(quest.description, color = Color.LightGray, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("CEL: ${quest.objective}", color = Color(0xFFADFF2F), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Text("WYRUSZ >", color = Color.Yellow, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}
