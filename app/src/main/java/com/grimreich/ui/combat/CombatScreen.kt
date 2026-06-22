package com.grimreich.ui.combat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CombatScreen(viewModel: CombatViewModel, onExit: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    val state = uiState.combat
    val party = uiState.party

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0E0E0E))
            .padding(16.dp)
    ) {
        Text(
            text = if (state.active) "⚔ WALKA: ${state.enemyName} (RUNDA ${state.round})" else "⚔ KONIEC WALKI",
            color = Color(0xFFE0C080),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.weight(1f)) {
            Column(modifier = Modifier.weight(1f)) {
                Text("TWOJA DRUŻYNA", color = Color.Gray, fontSize = 10.sp)
                Spacer(modifier = Modifier.height(4.dp))
                party.forEach { hero ->
                    CombatantRow(name = hero.name, hp = hero.hp, maxHp = hero.maxHp, isEnemy = false)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("PRZECIWNICY", color = Color.Gray, fontSize = 10.sp, modifier = Modifier.align(Alignment.End))
                Spacer(modifier = Modifier.height(4.dp))
                CombatantRow(
                    name = if (state.active || state.enemyHp > 0) state.enemyName else "POKONANY",
                    hp = state.enemyHp.coerceAtLeast(0),
                    maxHp = state.enemyMaxHp,
                    isEnemy = true
                )
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(vertical = 8.dp),
            color = Color(0xFF1A1A1A),
            shape = MaterialTheme.shapes.small
        ) {
            LazyColumn(modifier = Modifier.padding(8.dp)) {
                items(state.log.takeLast(20).reversed()) { entry ->
                    Text(text = entry, color = Color.LightGray, fontSize = 12.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                }
            }
        }

        if (state.active) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                CombatButton("ATAK", onClick = { viewModel.attack() })
                CombatButton("OBRONA", onClick = { viewModel.defend() })
                CombatButton("MGŁA", onClick = { viewModel.useSpecial("MIST") })
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                CombatButton("KREW", onClick = { viewModel.useSpecial("BLOOD") })
                CombatButton("ODBICIE", onClick = { viewModel.useSpecial("REFLECTION") })
                CombatButton("UCIECZKA", color = Color(0xFF5A1A1A), onClick = { viewModel.flee() })
            }
        } else {
            Button(
                onClick = onExit,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A2A2A))
            ) {
                Text("POWRÓT", color = Color(0xFFE0C080))
            }
        }
    }
}

@Composable
fun CombatantRow(name: String, hp: Int, maxHp: Int, isEnemy: Boolean) {
    val barColor = if (isEnemy) Color(0xFFB22222) else Color(0xFF228B22)
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = if (isEnemy) Arrangement.End else Arrangement.Start) {
            Text(text = name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
        LinearProgressIndicator(
            progress = { if (maxHp > 0) hp.toFloat() / maxHp else 0f },
            modifier = Modifier.fillMaxWidth().height(4.dp),
            color = barColor,
            trackColor = Color(0xFF333333)
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = if (isEnemy) Arrangement.End else Arrangement.Start) {
            Text(text = "$hp/$maxHp", color = Color.LightGray, fontSize = 10.sp)
        }
    }
}

@Composable
fun CombatButton(text: String, color: Color = Color(0xFF2A2A2A), onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        modifier = Modifier.width(100.dp),
        shape = MaterialTheme.shapes.extraSmall
    ) {
        Text(text = text, color = Color(0xFFE0C080), fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}
