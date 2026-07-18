package com.grimreich.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("NARZĘDZIA SKRYBY (DEBUG)", color = Color.Red, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A0000)),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
            ) {
                Text("WYJŚCIE X", fontSize = 10.sp)
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))

        // --- SEKCJA 1: STATYSTYKI ---
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                if (gameState.party.isEmpty()) {
                    Text("DRUŻYNA PUSTA", color = Color.Gray, fontSize = 12.sp)
                }
                gameState.party.forEach { hero ->
                    Text("BOHATER: ${hero.name} | WIEK: ${hero.age} | HP: ${hero.hp}/${hero.maxHp}", color = Color.Cyan, fontSize = 12.sp)
                }
                Text("LOKACJA: ${gameState.world.locationId} | DZIEŃ: ${gameState.world.day} | ZŁOTO: ${gameState.gold}", color = Color.Green, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- SEKCJA 2: MODYFIKATORY ŚWIATA ---
        Text("MODYFIKATORY ŚWIATA", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp), 
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            DevButton("GP+1k", Color(0xFF4A4A00)) { viewModel.addGold(1000) }
            DevButton("HEAL", Color(0xFF004A00)) { viewModel.healParty() }
            DevButton("+HERO", Color(0xFF00004A)) { viewModel.addTestHero() }
            DevButton("XP+100", Color.DarkGray) { viewModel.addXp(100) }
            DevButton("LVL+", Color.DarkGray) { viewModel.levelUp() }
            DevButton("DAYS+100", Color.DarkGray) { viewModel.addDays(100) }
            DevButton("COMBAT", Color(0xFF4A0000)) { root.startDevCombat() }
            DevButton("DUMP", Color.Black) { viewModel.dumpState() }
        }

        Spacer(modifier = Modifier.height(12.dp))
        
        // --- SEKCJA 3: KONTROLA ZADAŃ ---
        Text("QUEST INFO: $questInfo", color = Color.Yellow, fontSize = 11.sp)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            DevButton("START BLOOD", Color(0xFF1B5E20)) { viewModel.startQuest("q_blood_icon") }
            DevButton("ADVANCE", Color(0xFF0D47A1)) { viewModel.stepSuccess("q_blood_icon") }
            DevButton("FAIL", Color(0xFFB71C1C)) { viewModel.stepFail("q_blood_icon") }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- SEKCJA 4: SYSTEM ---
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DevButton("FORCE SAVE", Color(0xFF455A64)) { root.saveGame() }
            DevButton("SYNC", Color(0xFF455A64)) { root.forceSync() }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- SEKCJA 5: LOGI (Static height in scrollable column) ---
        Text("LOGI SYSTEMOWE", color = Color.Gray, fontSize = 10.sp)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.Black)
                .padding(4.dp)
                .verticalScroll(rememberScrollState())
        ) {
            logEntries.forEach { log ->
                Text("- $log", color = Color.LightGray, fontSize = 10.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun DevButton(text: String, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(2.dp),
        modifier = Modifier.heightIn(min = 32.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(text, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}
