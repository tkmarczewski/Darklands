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
import com.grimreich.systems.QuestEntry

@Composable
fun ExpeditionScreen(
    viewModel: ExpeditionViewModel,
    onBack: () -> Unit,
    onCombat: (QuestEntry) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    var questToConfirm by remember { mutableStateOf<QuestEntry?>(null) }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("WYPRAWA: ${state.regionName.uppercase()}", color = Color(0xFFC0A060), fontSize = 22.sp, fontWeight = FontWeight.Bold)
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
                onClick = onBack,
                modifier = Modifier.fillMaxWidth().height(50.dp).padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF400000))
            ) {
                Text("POWRÓT DO HUB")
            }package com.grimreich.ui.main

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
import com.grimreich.systems.QuestEntry

@Composable
fun ExpeditionScreen(
    viewModel: ExpeditionViewModel,
    onBack: () -> Unit,
    onCombat: (QuestEntry) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var questToConfirm by remember { mutableStateOf<QuestEntry?>(null) }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("WYPRAWA: ${state.regionName.uppercase()}", color = Color(0xFFC0A060), fontSize = 22.sp, fontWeight = FontWeight.Bold)
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
                onClick = onBack,
                modifier = Modifier.fillMaxWidth().height(50.dp).padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF400000))
            ) {
                Text("POWRÓT DO HUB")
            }
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
                            onCombat(quest)
                            questToConfirm = null
                        } else {
                            viewModel.completeNonCombatQuest(quest) { questToConfirm = null }
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
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFADFF2F))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                        Text("Uwaga: Wyjście poza miasto może wiązać się z walką.", color = Color.Red, fontSize = 12.sp)
                    }
                },
                confirmButton = {
                    Button(onClick = { 
                        onCombat(quest)
                        questToConfirm = null
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
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFADFF2F))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
