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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimreich.R
import com.grimreich.systems.QuestDefinition
import com.grimreich.ui.shared.BadgeV9
import com.grimreich.ui.shared.getQuestCategoryColor

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
            Text(stringResource(R.string.quest_journal_title), color = Color(0xFFE0C080), fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A2A2A))) {
                Text(stringResource(R.string.btn_back), color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { SectionHeader(stringResource(R.string.quest_journal_active)) }
            if (state.activeQuests.isEmpty()) {
                item { Text(stringResource(R.string.quest_journal_empty_active), color = Color.Gray, fontSize = 12.sp) }
            } else {
                items(state.activeQuests, key = { it.definition.id }) { item ->
                    QuestEntryCard(
                        quest = item.definition, 
                        isCompleted = false, 
                        objective = item.objective,
                        isReady = item.isReadyToTurnIn
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
            item { SectionHeader(stringResource(R.string.quest_journal_completed)) }
            if (state.completedQuests.isEmpty()) {
                item { Text(stringResource(R.string.quest_journal_empty_completed), color = Color.Gray, fontSize = 12.sp) }
            } else {
                items(state.completedQuests, key = { it.id }) { quest ->
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
fun QuestEntryCard(
    quest: QuestDefinition, 
    isCompleted: Boolean, 
    objective: String? = null,
    isReady: Boolean = false
) {
    val borderColor = when {
        isCompleted -> Color.DarkGray
        isReady -> Color(0xFFC0A060) // Gold for ready to turn in
        else -> Color(0xFFADFF2F) // Green for active
    }

    Surface(
        color = Color(0xFF151515),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        shape = MaterialTheme.shapes.extraSmall
    ) {
        Column(modifier = Modifier.padding(12.dp).fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isReady) {
                        Text("۞ ", color = Color(0xFFC0A060), fontWeight = FontWeight.Bold)
                    }
                    Text(quest.title, color = if (isCompleted) Color.Gray else Color.White, fontWeight = FontWeight.Bold)
                }
                BadgeV9(text = quest.category.name, color = getQuestCategoryColor(quest.category))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(quest.description, color = Color.Gray, fontSize = 11.sp, lineHeight = 15.sp)
            
            if (!isCompleted && objective != null) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0x11FFFFFF), thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isReady) stringResource(R.string.quest_status_ready) else stringResource(R.string.quest_label_objective, objective), 
                    color = if (isReady) Color(0xFFC0A060) else Color(0xFFADFF2F), 
                    fontSize = 11.sp, 
                    fontWeight = FontWeight.ExtraBold
                )
                if (isReady) {
                    Text(
                        text = "${stringResource(R.string.city_label_location, quest.cityId.uppercase())} -> ${quest.originNpcId.uppercase()}",
                        color = Color.DarkGray,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}
