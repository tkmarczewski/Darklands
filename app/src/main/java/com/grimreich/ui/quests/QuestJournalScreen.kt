package com.grimreich.ui.quests

import androidx.compose.foundation.background
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

@Composable
fun QuestJournalScreen(
    viewModel: QuestJournalViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("DZIENNIK ZADAŃ", color = Color(0xFFE0C080), fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A2A2A))) {
                Text("POWRÓT", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { SectionHeader("AKTYWNE") }
            if (state.activeQuests.isEmpty()) {
                item { Text("Brak aktywnych zadań.", color = Color.Gray, fontSize = 12.sp) }
            } else {
                items(state.activeQuests) { quest ->
                    QuestEntryCard(quest, isCompleted = false)
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
            item { SectionHeader("UKOŃCZONE") }
            if (state.completedQuests.isEmpty()) {
                item { Text("Brak ukończonych zadań.", color = Color.Gray, fontSize = 12.sp) }
            } else {
                items(state.completedQuests) { quest ->
                    QuestEntryCard(quest, isCompleted = true)
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(title, color = Color(0xFFADFF2F), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun QuestEntryCard(quest: QuestDefinition, isCompleted: Boolean) {
    Surface(
        color = Color(0xFF151515),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isCompleted) Color.Gray else Color(0xFFADFF2F)),
        shape = MaterialTheme.shapes.extraSmall
    ) {
        Column(modifier = Modifier.padding(12.dp).fillMaxWidth()) {
            Text(quest.title, color = if (isCompleted) Color.Gray else Color.White, fontWeight = FontWeight.Bold)
            Text(quest.description, color = Color.DarkGray, fontSize = 11.sp)
        }
    }
}
