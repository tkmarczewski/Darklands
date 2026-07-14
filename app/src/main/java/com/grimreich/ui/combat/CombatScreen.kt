package com.grimreich.ui.combat

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border // TO BE CHECKED
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.grimreich.R
import com.grimreich.ui.shared.*
import com.grimreich.ui.effects.glitchEffect
import kotlin.random.Random

@Composable
fun CombatScreen(
    viewModel: CombatViewModel = hiltViewModel(),
    onExit: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize().background(Color.Black).padding(4.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // --- GÓRNY PASEK: STATUS PARADYGMATU ---
            Row(
                modifier = Modifier.fillMaxWidth().height(30.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PROTOKÓŁ WALKI: ${state.ontologicalLevel.displayName.uppercase()}", 
                    color = Color(0xFFC0A060), 
                    fontSize = 11.sp, 
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "STABILNOŚĆ: ${state.worldStability}%", 
                    color = if(state.worldStability < 20) Color.Red else Color.Green, 
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // --- KOKPIT TAKTYCZNY (3 KAFLE V9) ---
            Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                
                // 1. LEWY KAFEL: LOGI BITWNE (TRIBUNAL LOG)
                GothicObsidianCard(modifier = Modifier.weight(0.7f).fillMaxHeight()) {
                    Text(text = "TRIBUNAL_LOG_014", color = Color(0xFFC0A060), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Divider(color = Color(0x33C0A060), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                    
                    val reversedLogs = remember(state.combat.log) { state.combat.log.asReversed() }
                    LazyColumn(modifier = Modifier.fillMaxSize(), reverseLayout = true) {
                        itemsIndexed(reversedLogs) { _, msg ->
                            Text(
                                text = "> $msg", 
                                color = if (state.worldStability < 15) Color.Red else Color.LightGray, 
                                fontSize = 10.sp, 
                                lineHeight = 13.sp,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // 2. ŚRODKOWY KAFEL: ARENA (WIZJA WROGA)
                GothicObsidianCard(modifier = Modifier.weight(1.3f).fillMaxHeight(), headerColor = Color(0xFF800000)) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // WIZUALIZACJA WROGA
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = state.combat.enemyName.uppercase(),
                                color = Color.Red,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            // HP BAR WROGA
                            Box(modifier = Modifier.width(200.dp).height(8.dp).background(Color(0xFF222222))) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(if (state.combat.enemyMaxHp > 0) state.combat.enemyHp.toFloat() / state.combat.enemyMaxHp else 0f)
                                        .background(Color.Red)
                                )
                            }
                            Text(
                                text = "${state.combat.enemyHp} / ${state.combat.enemyMaxHp} HP",
                                color = Color.White,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        
                        // EFEKTY GLITCH
                        if (state.worldStability < 30) {
                            Box(modifier = Modifier.fillMaxSize().glitchEffect(true, 0.2f))
                        }
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // 3. PRAWY KAFEL: MENU AKCJI
                Column(modifier = Modifier.weight(0.7f).fillMaxHeight()) {
                    GothicObsidianCard(modifier = Modifier.weight(1.2f), headerColor = Color(0xFF4527A0)) {
                        Text(text = "UMIEJĘTNOŚCI", color = Color(0xFFC0A060), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Divider(color = Color(0x33C0A060), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                        
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            items(state.availableSkills) { skill ->
                                NavTabV9(
                                    text = skill.name, 
                                    onClick = { viewModel.useSkill(skill.id) },
                                    color = Color(0xFF1A1A1A)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    GothicObsidianCard(modifier = Modifier.weight(0.8f), headerColor = Color(0xFF1B5E20)) {
                        Text(text = "AKCJE", color = Color(0xFFC0A060), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(top = 4.dp)) {
                            if (state.combat.active) {
                                NavTabV9("ATAK", onClick = { viewModel.attack() }, color = Color(0xFF4A0000))
                                NavTabV9("OBRONA", onClick = { viewModel.defend() }, color = Color(0xFF333333))
                                NavTabV9("REVISION", onClick = { viewModel.useEchoSkill("REVISION") }, color = Color(0xFF0D47A1))
                            } else {
                                NavTabV9("ZAKOŃCZ", onClick = { viewModel.exitCombat(onExit) }, color = Color(0xFF400000))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // --- DOLNY PASEK: DRUŻYNA (V9) ---
            GothicObsidianCard(modifier = Modifier.fillMaxWidth().height(80.dp)) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    state.party.forEach { hero ->
                        val isActive = state.combat.activeHeroId == hero.id
                        Box(modifier = Modifier.border(if (isActive) 1.dp else 0.dp, Color.Yellow)) {
                            HeroPortraitV9(hero = hero, onClick = { viewModel.selectHero(hero.id) })
                        }
                    }
                }
            }
        }
    }
}
