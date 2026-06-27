package com.grimreich.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.grimreich.viewmodels.DevMenuViewModel

@Composable
fun DevMenuScreen(
    viewModel: DevMenuViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val logEntries by viewModel.logEntries.collectAsState()
    val questInfo by viewModel.currentQuestInfo.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111111))
            .padding(16.dp)
    ) {
        Text("NARZĘDZIA SKRYBY (DEBUG)", color = Color.Red, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("QUEST INFO: $questInfo", color = Color.Yellow, fontSize = 12.sp)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { viewModel.startQuest("q_blood_icon") }) { Text("START BLOOD") }
            Button(onClick = { viewModel.stepSuccess("q_blood_icon") }) { Text("ADVANCE") }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(logEntries) { log ->
                Text("- $log", color = Color.LightGray, fontSize = 11.sp)
            }
        }

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
        ) {
            Text("POWRÓT")
        }
    }
}
