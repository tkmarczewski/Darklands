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
import com.grimreich.core.GameRepository
import com.grimreich.systems.QuestEntry
import com.grimreich.systems.QuestStatus
import com.grimreich.systems.QuestSystem
import com.grimreich.ui.theme.GrimTheme

@Composable
fun QuestJournalScreen(
    onBack: () -> Unit
) {
    val state = GameRepository.state
    val activeQuests = state.quest.activeQuests.mapNotNull { QuestSystem.getQuest(it) }
    val completedQuests = state.quest.completedQuests.mapNotNull { QuestSystem.getQuest(it) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Text(
            text = "DZIENNIK ZADAŃ",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFFC0A060),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            item {
                SectionHeader("AKTYWNE")
            }
            if (activeQuests.isEmpty()) {
                item { EmptyLabel("Brak aktywnych zadań.") }
            } else {
                items(activeQuests) { quest ->
                    QuestItem(quest)
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                SectionHeader("UKOŃCZONE")
            }
            if (completedQuests.isEmpty()) {
                item { EmptyLabel("Brak ukończonych zadań.") }
            } else {
                items(completedQuests) { quest ->
                    QuestItem(quest, isCompleted = true)
                }
            }
        }

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF400000))
        ) {
            Text("POWRÓT", color = Color.White)
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        color = Color(0xFF800000),
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )
    Divider(color = Color(0xFF404040), thickness = 1.dp)
}

@Composable
private fun EmptyLabel(text: String) {
    Text(
        text = text,
        color = Color.Gray,
        fontSize = 14.sp,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun QuestItem(quest: QuestEntry, isCompleted: Boolean = false) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = quest.title,
                style = MaterialTheme.typography.titleMedium,
                color = if (isCompleted) Color.Gray else Color(0xFFC0A060)
            )
            Text(
                text = quest.description,
                style = MaterialTheme.typography.bodySmall,
                color = Color.LightGray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "CEL: ${quest.objective}",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF800000),
                fontWeight = FontWeight.Bold
            )
            if (!isCompleted) {
                Text(
                    text = "NAGRODA: ${quest.rewardGold} G",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Green
                )
            }
        }
    }
}
