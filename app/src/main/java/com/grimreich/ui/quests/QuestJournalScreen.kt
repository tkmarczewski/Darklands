package com.grimreich.ui.quests

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimreich.systems.QuestEntry
import com.grimreich.ui.quests.QuestJournalViewModel

@Composable
fun QuestJournalScreen(
    viewModel: QuestJournalViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Text(
            text = "DZIENNIK ZADAŃ",
            color = Color(0xFFC0A060),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 16.dp)
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            item { SectionHeader("AKTYWNE") }
            if (state.activeQuests.isEmpty()) {
                item { EmptyLabel("Brak podjętych zadań.") }
            } else {
                items(state.activeQuests) { quest ->
                    QuestCard(quest, isActive = true, onAccept = {})
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
            item { SectionHeader("DOSTĘPNE W OKOLICY") }
            if (state.availableQuests.isEmpty()) {
                item { EmptyLabel("Brak dostępnych zadań.") }
            } else {
                items(state.availableQuests) { quest ->
                    QuestCard(quest, isActive = false, onAccept = { viewModel.acceptQuest(quest.id) })
                }
            }
            
            val achievable = state.activeQuests.filter { it.status == com.grimreich.systems.QuestStatus.CEL_OSIAGNIETY }
            if (achievable.isNotEmpty()) {
                item { Spacer(modifier = Modifier.height(16.dp)) }
                item { SectionHeader("GOTOWE DO ODDANIA") }
                items(achievable) { quest ->
                    QuestCard(quest, isAchieved = true, onAccept = {})
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
            item { SectionHeader("UKOŃCZONE") }
            if (state.completedQuests.isEmpty()) {
                item { EmptyLabel("Brak ukończonych zadań.") }
            } else {
                items(state.completedQuests) { quest ->
                    QuestCard(quest, isCompleted = true, onAccept = {})
                }
            }
        }

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF400000))
        ) {
            Text("POWRÓT")
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        color = Color.Red,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun EmptyLabel(text: String) {
    Text(
        text = text,
        color = Color.Gray,
        fontSize = 12.sp,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
private fun QuestCard(
    quest: QuestEntry,
    isActive: Boolean = false,
    isCompleted: Boolean = false,
    isAchieved: Boolean = false,
    onAccept: () -> Unit
) {
    val context = LocalContext.current
    val iconName = when(quest.category) {
        "Intrigue" -> "ic_artifact_eye"
        "Anomaly" -> "ic_scroll_blood"
        "Beast" -> "ic_item_sword_1h"
        "Drama" -> "ic_artifact_heart"
        "Verdict" -> "ic_artifact_mask"
        "Chain" -> "ic_artifact_blood"
        else -> "ic_scroll_ice"
    }
    val iconResId = context.resources.getIdentifier(iconName, "drawable", context.packageName)

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF101010)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF222222))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            if (iconResId != 0) {
                Image(
                    painter = painterResource(id = iconResId),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp).padding(end = 12.dp)
                )
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = quest.title, color = if (isAchieved) Color.Green else Color(0xFFE0C080), fontWeight = FontWeight.Bold)
                    if (!isActive && !isCompleted && !isAchieved) {
                        Button(
                            onClick = onAccept,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF203010)),
                            modifier = Modifier.height(32.dp).padding(0.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                        ) {
                            Text("PRZYJMIJ", fontSize = 10.sp)
                        }
                    }
                }
                Text(text = quest.description, color = Color.White, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    val objectiveText = if (isAchieved) "ZADANIE WYKONANE. WRÓĆ DO ZLECENIODAWCY." else "CEL: ${quest.objective}"
                    Text(text = objectiveText, color = if (isAchieved) Color.Green else if (isCompleted) Color.Gray else Color(0xFFADFF2F), fontSize = 10.sp)
                    
                    if (quest.factionRewardId != null) {
                        Text(text = "+${quest.factionRewardAmount} REP: ${quest.factionRewardId.uppercase()}", color = Color(0xFFC0A060), fontSize = 9.sp)
                    } else {
                        Text(text = "LOKACJA: ${quest.cityId.uppercase().replace("_", " ")}", color = Color.Gray, fontSize = 9.sp)
                    }
                }
            }
        }
    }
}
