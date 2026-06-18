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

@Composable
fun QuestJournalScreen(
    onBack: () -> Unit
) {
    // FORCE SEED REFRESH
    LaunchedEffect(Unit) {
        QuestSystem.seedIntegratedContent()
    }

    val state = GameRepository.state
    val activeQuests = state.quest.activeQuests.mapNotNull { QuestSystem.getQuest(it) }
    val completedQuests = state.quest.completedQuests.mapNotNull { QuestSystem.getQuest(it) }
    
    // Also show available quests in current city
    val rawCity = state.grimCurrentRegion
    val cityId = rawCity.lowercase()
        .replace("ą", "a").replace("ć", "c").replace("ę", "e")
        .replace("ł", "l").replace("ń", "n").replace("ó", "o")
        .replace("ś", "s").replace("ź", "z").replace("ż", "z")
        .replace(" ", "_")
    
    val availableQuests = QuestSystem.availableForCity(cityId)

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
            modifier = Modifier.padding(bottom = 16.dp).align(Alignment.CenterHorizontally)
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            item { SectionHeader("AKTYWNE") }
            if (activeQuests.isEmpty()) {
                item { EmptyLabel("Brak podjętych zadań.") }
            } else {
                items(activeQuests) { quest -> QuestCard(quest) }
            }

            item { Spacer(modifier = Modifier.height(20.dp)); SectionHeader("DOSTĘPNE W OKOLICY") }
            if (availableQuests.isEmpty()) {
                item { EmptyLabel("Brak nowych ogłoszeń.") }
            } else {
                items(availableQuests) { quest ->
                    QuestCard(quest, canAccept = true)
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)); SectionHeader("UKOŃCZONE") }
            if (completedQuests.isEmpty()) {
                item { EmptyLabel("Twoja legenda dopiero się zaczyna.") }
            } else {
                items(completedQuests) { quest -> QuestCard(quest, isCompleted = true) }
            }
        }

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().height(50.dp).padding(top = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF400000))
        ) {
            Text("POWRÓT", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = Color(0xFF800000),
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 4.dp)
    )
    HorizontalDivider(color = Color(0xFF333333), thickness = 1.dp)
}

@Composable
private fun EmptyLabel(text: String) {
    Text(
        text = text,
        color = Color.DarkGray,
        fontSize = 12.sp,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun QuestCard(quest: QuestEntry, isCompleted: Boolean = false, canAccept: Boolean = false) {
    var accepted by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0A0A)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF222222))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = quest.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isCompleted) Color.Gray else Color(0xFFC0A060),
                    modifier = Modifier.weight(1f)
                )
                if (canAccept && !accepted) {
                    Button(
                        onClick = {
                            QuestSystem.activate(quest.id)
                            accepted = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A4000)),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("PRZYJMIJ", fontSize = 10.sp, color = Color.White)
                    }
                }
            }
            Text(text = quest.description, fontSize = 12.sp, color = Color.LightGray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "CEL: ${quest.objective}", fontSize = 10.sp, color = Color(0xFF800000), fontWeight = FontWeight.Bold)
        }
    }
}
