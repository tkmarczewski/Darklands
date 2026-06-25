package com.grimreich.ui.combat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
                .height(160.dp)
                .padding(vertical = 8.dp),
            color = Color(0xFF1A1A1A),
            shape = MaterialTheme.shapes.small
        ) {
            LazyColumn(modifier = Modifier.padding(8.dp)) {
                items(state.log.takeLast(30).reversed()) { entry ->
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

            if (uiState.potions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text("TWOJE MIKSTURY:", color = Color(0xFFADFF2F), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.potions) { potion ->
                        PotionBtn(potion.name) { viewModel.usePotion(potion.id) }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text("ZDOLNOŚCI ECHO (KOSZT ŚWIATA):", color = Color.Red, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                CombatButton("REWIZJA", color = Color(0xFF400040), onClick = { viewModel.useEchoSkill("REVISION") })
                CombatButton("WYMAZANIE", color = Color(0xFF000040), onClick = { viewModel.useEchoSkill("ERASURE") })
                CombatButton("NADPISANIE", color = Color(0xFF404000), onClick = { viewModel.useEchoSkill("OVERWRITE") })
            }
        } else {
            Button(
                onClick = { viewModel.exitCombat(onExit) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A6000))
            ) {
                Text("ZAKOŃCZ", color = Color.White, fontWeight = FontWeight.Bold)
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

@Composable
fun PotionBtn(name: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = Color(0xFF2A4000),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFADFF2F))
    ) {
        Text(
            text = name.uppercase(),
            color = Color.White,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
        )
    }
}
