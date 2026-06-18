package com.grimreich.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimreich.R
import com.grimreich.core.Hero

@Composable
fun HubScreen(
    viewModel: HubViewModel,
    onCity: () -> Unit,
    onMap: () -> Unit,
    onInventory: () -> Unit,
    onQuests: () -> Unit,
    onWorldLog: () -> Unit,
    onCharacter: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // BACKGROUND
        Image(
            painter = painterResource(id = R.drawable.bg_party_castle),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.4f
        )

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            // HEADER: Info Bar
            Row(
                modifier = Modifier.fillMaxWidth().background(Color(0x80000000)).padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = state.locationName, color = Color(0xFFE0C080), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(text = "DZIEŃ ${state.day} | ${state.timeOfDay.uppercase()}", color = Color.LightGray, fontSize = 12.sp)
                Text(text = "${state.gold} G", color = Color(0xFFE0C080), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.weight(1f)) {
                // LEFT: Main Navigation Grid
                Column(modifier = Modifier.weight(1.5f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        HubNavButton("MIASTO", modifier = Modifier.weight(1f), onClick = onCity)
                        HubNavButton("MAPA", modifier = Modifier.weight(1f), onClick = onMap)
                        HubNavButton("PLECAK", modifier = Modifier.weight(1f), onClick = onInventory)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        HubNavButton("ZADANIA", modifier = Modifier.weight(1f), onClick = onQuests)
                        HubNavButton("DRUŻYNA", modifier = Modifier.weight(1f), color = Color(0xFF4A0000), onClick = { /* Logic for team view */ })
                        HubNavButton("KRONIKA", modifier = Modifier.weight(1f), onClick = onWorldLog)
                    }
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val expeditionText = if (state.activeQuestsCount > 0) "EKSPEDYCJA (${state.activeQuestsCount})" else "BRAK WYPRAW"
                        HubNavButton(
                            text = expeditionText,
                            modifier = Modifier.weight(1.5f),
                            color = if (state.activeQuestsCount > 0) Color(0xFFADFF2F) else Color(0xFF1A1A1A),
                            enabled = state.activeQuestsCount > 0,
                            onClick = { 
                                val firstQuest = com.grimreich.core.GameRepository.state.quest.activeQuests.firstOrNull()
                                if (firstQuest != null) {
                                    com.grimreich.core.GameRepository.state.pendingQuestId = firstQuest
                                    context.startActivity(android.content.Intent(context, com.grimreich.ui.CombatActivity::class.java))
                                }
                            }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // WORLD STATUS LOG MINI
                    Surface(
                        color = Color(0x40000000),
                        shape = MaterialTheme.shapes.extraSmall,
                        modifier = Modifier.fillMaxWidth().weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("AKTYWNE WYDARZENIA", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Kroniki pękniętego świata...", color = Color.DarkGray, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // RIGHT: World Log Summary
                Surface(
                    color = Color(0x20FFFFFF),
                    modifier = Modifier.weight(1f).fillMaxHeight()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("OSTATNIE WIEŚCI:", color = Color.Gray, fontSize = 10.sp)
                        // ...
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // BOTTOM: Party Strip
            Text("TWOJA DRUŻYNA", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.party) { hero ->
                    PartyMemberCard(hero) { onCharacter(hero.id) }
                }
            }
        }
    }
}

@Composable
fun HubNavButton(text: String, modifier: Modifier = Modifier, color: Color = Color(0xFF1A1A1A), enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(60.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color, disabledContainerColor = color.copy(alpha = 0.5f)),
        shape = MaterialTheme.shapes.extraSmall,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF333333))
    ) {
        Text(text = text, color = if (enabled) Color(0xFFE0C080) else Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun PartyMemberCard(hero: Hero, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.width(120.dp).clickable { onClick() },
        color = Color(0xFF1A1A1A),
        shape = MaterialTheme.shapes.extraSmall,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF444444))
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = hero.name, color = Color(0xFFE0C080), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            LinearProgressIndicator(
                progress = if (hero.maxHp > 0) hero.hp.toFloat() / hero.maxHp else 0f,
                modifier = Modifier.fillMaxWidth().height(4.dp).padding(vertical = 4.dp),
                color = Color(0xFFB22222),
                trackColor = Color(0xFF333333)
            )
            Text(text = "${hero.hp}/${hero.maxHp} HP", color = Color.Gray, fontSize = 10.sp)
        }
    }
}
