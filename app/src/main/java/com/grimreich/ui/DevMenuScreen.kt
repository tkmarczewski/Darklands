package com.grimreich.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.grimreich.systems.QuestDefinitionRegistry
import com.grimreich.viewmodels.DevMenuViewModel

@Composable
fun DevMenuScreen(
    viewModel: DevMenuViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val logEntries by viewModel.logEntries.collectAsState()
    val currentQuestInfo by viewModel.currentQuestInfo.collectAsState()
    var selectedQuestId by remember { mutableStateOf("test_quest") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("DEV MENU", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        Text("Wybierz quest:", style = MaterialTheme.typography.labelLarge)
        LazyColumn(modifier = Modifier.height(200.dp)) {
            items(QuestDefinitionRegistry.allDefinitions) { def ->
                TextButton(onClick = { selectedQuestId = def.id }) {
                    Text(
                        text = "[${def.category}] ${def.title}",
                        color = if (def.id == selectedQuestId)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("Wybrany: $selectedQuestId", style = MaterialTheme.typography.bodySmall)
        Text("Aktualny krok: $currentQuestInfo", style = MaterialTheme.typography.bodySmall)

        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { viewModel.startQuest(selectedQuestId) }) { Text("Start") }
            Button(onClick = { viewModel.stepSuccess(selectedQuestId) }) { Text("Step ✓") }
            Button(onClick = { viewModel.stepFail(selectedQuestId) }) { Text("Step ✗") }
            Button(onClick = { viewModel.resetQuest(selectedQuestId) }) { Text("Reset") }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("Log:", style = MaterialTheme.typography.labelLarge)
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(logEntries.reversed()) { entry ->
                Text(entry, style = MaterialTheme.typography.bodySmall)
                Divider()
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Wróć")
        }
    }
}
