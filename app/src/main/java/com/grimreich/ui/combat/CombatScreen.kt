package com.grimreich.ui.combat

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
import androidx.hilt.navigation.compose.hiltViewModel
import com.grimreich.core.GameConstants

@Composable
fun CombatScreen(
    viewModel: CombatViewModel = hiltViewModel(),
    onExit: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        // ENEMY
        CombatantRow(state.combat.enemyName, state.combat.enemyHp, state.combat.enemyMaxHp, isEnemy = true)
        
        Spacer(modifier = Modifier.height(16.dp))

        // LOG (SCROLLABLE)
        Surface(
            color = Color(0xFF111111),
            modifier = Modifier.weight(1f).fillMaxWidth(),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.DarkGray)
        ) {
            LazyColumn(
                modifier = Modifier.padding(12.dp),
                reverseLayout = true
            ) {
                items(state.combat.log.asReversed()) { msg ->
                    Text(msg, color = Color.LightGray, fontSize = 11.sp, lineHeight = 14.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // HEROES
        state.party.forEach { hero ->
            CombatantRow(hero.name, hero.hp, hero.maxHp, isEnemy = false)
            Spacer(modifier = Modifier.height(4.dp))
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ACTIONS
        if (state.combat.active) {
            // Skill List
            if (state.availableSkills.isNotEmpty()) {
                LazyColumn(modifier = Modifier.heightIn(max = 120.dp)) {
                    items(state.availableSkills) { skill ->
                        Button(
                            onClick = { viewModel.useSkill(skill.id) },
                            modifier = Modifier.fillMaxWidth().height(36.dp).padding(vertical = 2.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF333333)),
                            shape = MaterialTheme.shapes.extraSmall
                        ) {
                            Text(skill.name.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CombatButton("ATAK", Color(0xFF800000), modifier = Modifier.weight(1f)) { viewModel.attack() }
                CombatButton("OBRONA", Color(0xFF444444), modifier = Modifier.weight(1f)) { viewModel.defend() }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CombatButton("REWIZJA", Color(0xFF004488), modifier = Modifier.weight(1f)) { viewModel.useEchoSkill("REVISION") }
                CombatButton("NADPISANIE", Color(0xFF440088), modifier = Modifier.weight(1f)) { viewModel.useEchoSkill("OVERWRITE") }
            }
        } else {
            Button(
                onClick = { viewModel.exitCombat(onExit) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A2A2A))
            ) {
                Text("ZAKOŃCZ WALKĘ", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CombatantRow(name: String, hp: Int, maxHp: Int, isEnemy: Boolean) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(name.uppercase(), color = if (isEnemy) Color.Red else Color(0xFFE0C080), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text("$hp / $maxHp HP", color = Color.White, fontSize = 11.sp)
        }
        LinearProgressIndicator(
            progress = { if (maxHp > 0) hp.toFloat() / maxHp else 0f },
            modifier = Modifier.fillMaxWidth().height(4.dp),
            color = if (isEnemy) Color.Red else Color(0xFFADFF2F),
            trackColor = Color(0xFF222222)
        )
    }
}

@Composable
fun CombatButton(text: String, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = MaterialTheme.shapes.extraSmall
    ) {
        Text(text, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}
