package com.grimreich.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.grimreich.viewmodels.DevMenuViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DevMenuScreen(
    viewModel: DevMenuViewModel = hiltViewModel(),
    root: com.grimreich.ui.main.GameRootViewModel,
    onBack: () -> Unit
) {
    val logEntries by viewModel.logEntries.collectAsState()
    val questInfo by viewModel.currentQuestInfo.collectAsState()
    val gameState by viewModel.gameRepository.gameState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111111))
            .padding(16.dp)
    ) {
        Text("NARZĘDZIA SKRYBY (DEBUG)", color = Color.Red, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        
        Spacer(modifier = Modifier.height(8.dp))

        // --- SEKCJA 1: STATYSTYKI ---
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                gameState.party.forEach { hero ->
                    Text("BOHATER: ${hero.name} | WIEK: ${hero.age} | HP: ${hero.hp}/${hero.maxHp}", color = Color.Cyan, fontSize = 12.sp)
                }
                Text("LOKACJA: ${gameState.world.locationId} | DZIEŃ: ${gameState.world.day} | ZŁOTO: ${gameState.gold}", color = Color.Green, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- SEKCJA 2: MODYFIKATORY ŚWIATA ---
        Text("MODYFIKATORY ŚWIATA", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 4.dp)) {
            Button(onClick = { viewModel.addGold(1000) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A4A00))) { Text("GP+1k") }
            Button(onClick = { viewModel.healParty() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004A00))) { Text("HEAL") }
            Button(onClick = { viewModel.addTestHero() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00004A))) { Text("+HERO") }
            Button(onClick = { viewModel.addXp(100) }) { Text("XP+100") }
            Button(onClick = { viewModel.levelUp() }) { Text("LVL+") }
            Button(onClick = { viewModel.addDays(100) }) { Text("DAYS+100") }
            Button(onClick = { root.startDevCombat() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A0000))) { Text("COMBAT") }
            Button(onClick = { viewModel.dumpState() }) { Text("DUMP") }
        }

        Spacer(modifier = Modifier.height(8.dp))
        
        // --- SEKCJA 3: KONTROLA ZADAŃ ---
        Text("QUEST INFO: $questInfo", color = Color.Yellow, fontSize = 12.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 4.dp)) {
            Button(onClick = { viewModel.startQuest("q_blood_icon") }) { Text("START BLOOD") }
            Button(onClick = { viewModel.stepSuccess("q_blood_icon") }) { Text("ADVANCE") }
            Button(onClick = { viewModel.stepFail("q_blood_icon") }, colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)) { Text("FAIL") }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- SEKCJA 4: SYSTEM ---
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { root.saveGame() }) { Text("FORCE SAVE") }
            Button(onClick = { root.forceSync() }) { Text("SYNC") }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- SEKCJA 5: LOGI ---
        LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth().background(Color.Black).padding(4.dp)) {
            items(logEntries) { log ->
                Text("- $log", color = Color.LightGray, fontSize = 10.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF333333))
        ) {
            Text("POWRÓT DO GRY")
        }
    }
}
