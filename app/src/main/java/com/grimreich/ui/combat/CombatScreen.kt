package com.grimreich.ui.combat

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.grimreich.R
import com.grimreich.core.GameConstants
import kotlin.random.Random

@Composable
fun CombatScreen(
    viewModel: CombatViewModel = hiltViewModel(),
    onExit: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    // GLITCH ANIMATION
    val infiniteTransition = rememberInfiniteTransition(label = "glitch")
    val jitterX by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(40, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "jitter"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (state.worldStability < 10 && Random.nextFloat() < 0.05f) Color(0xFF200000) else Color.Black)
            .padding(16.dp)
    ) {
        // ENEMY
        val enemyName = if (state.worldStability < 30 && Random.nextFloat() < 0.1f) "UNKNOWN_ENTITY" else state.combat.enemyName
        CombatantRow(enemyName, state.combat.enemyHp, state.combat.enemyMaxHp, isEnemy = true, stability = state.worldStability, jitter = jitterX)
        
        Spacer(modifier = Modifier.height(16.dp))

        // LOG
        Surface(
            color = Color(0xFF111111),
            modifier = Modifier.weight(1f).fillMaxWidth(),
            border = androidx.compose.foundation.BorderStroke(1.dp, if (state.worldStability < 20) Color.Red else Color.DarkGray)
        ) {
            LazyColumn(
                modifier = Modifier.padding(12.dp),
                reverseLayout = true
            ) {
                items(state.combat.log.asReversed()) { msg ->
                    val displayedMsg = if (state.worldStability < 15) {
                        msg.map { if (Random.nextFloat() < 0.03f) '#' else it }.joinToString("")
                    } else msg
                    Text(displayedMsg, color = if (state.worldStability < 10) Color.Red else Color.LightGray, fontSize = 11.sp, lineHeight = 14.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // HEROES (Party List / Selector)
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            state.party.forEach { hero ->
                val isTarget = state.combat.currentTargetHeroId == hero.id
                val isActive = state.combat.activeHeroId == hero.id
                
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(enabled = !hero.isDead) { viewModel.selectHero(hero.id) },
                    color = if (isActive) Color(0xFF333300) else Color(0xFF111111),
                    border = androidx.compose.foundation.BorderStroke(
                        if (isTarget) 2.dp else 1.dp,
                        if (isTarget) Color.Red else if (isActive) Color.Yellow else Color.DarkGray
                    ),
                    shape = MaterialTheme.shapes.extraSmall
                ) {
                    Column(modifier = Modifier.padding(4.dp)) {
                        Text(
                            hero.name.uppercase(), 
                            color = if (hero.isDead) Color.Gray else Color.White, 
                            fontSize = 10.sp, 
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        LinearProgressIndicator(
                            progress = { if (hero.maxHp > 0) hero.hp.toFloat() / hero.maxHp else 0f },
                            modifier = Modifier.fillMaxWidth().height(2.dp),
                            color = if (hero.hp < 10) Color.Red else Color(0xFFADFF2F),
                            trackColor = Color.Black
                        )
                    }
                }
            }
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
                CombatButton(stringResource(R.string.combat_btn_attack), Color(0xFF800000), modifier = Modifier.weight(1f)) { viewModel.attack() }
                CombatButton(stringResource(R.string.combat_btn_defend), Color(0xFF444444), modifier = Modifier.weight(1f)) { viewModel.defend() }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val revLabel = if (state.worldStability < 40) stringResource(R.string.combat_btn_revision) + "_v2" else stringResource(R.string.combat_btn_revision)
                CombatButton(revLabel, Color(0xFF004488), modifier = Modifier.weight(1f)) { viewModel.useEchoSkill("REVISION") }
                CombatButton(stringResource(R.string.combat_btn_overwrite), Color(0xFF440088), modifier = Modifier.weight(1f)) { viewModel.useEchoSkill("OVERWRITE") }
            }
        } else {
            Button(
                onClick = { viewModel.exitCombat(onExit) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (state.worldStability < 10) Color.Red else Color(0xFF2A2A2A))
            ) {
                Text(if (state.worldStability < 5) stringResource(R.string.combat_btn_glitch_exit) else stringResource(R.string.combat_btn_exit), color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CombatantRow(name: String, hp: Int, maxHp: Int, isEnemy: Boolean, stability: Int = 100, jitter: Float = 0f) {
    val offset = if (stability < 15) jitter.dp else 0.dp
    Column(modifier = Modifier.offset(x = offset)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(name.uppercase(), color = if (isEnemy) Color.Red else Color(0xFFE0C080), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text("$hp / $maxHp HP", color = if (stability < 10 && hp < 10) Color.Red else Color.White, fontSize = 11.sp)
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
