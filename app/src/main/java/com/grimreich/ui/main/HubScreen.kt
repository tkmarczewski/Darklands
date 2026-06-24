package com.grimreich.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.grimreich.core.GameConstants
import com.grimreich.core.Hero
import com.grimreich.ui.shared.WorldPhaseWidget

@Composable
fun HubScreen(
    viewModel: HubViewModel,
    onCity: () -> Unit,
    onMap: () -> Unit,
    onInventory: () -> Unit,
    onQuests: () -> Unit,
    onWorldLog: () -> Unit,
    onCharacter: (String) -> Unit,
    onExpedition: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // BACKGROUND
        val context = LocalContext.current
        val bgResId = context.resources.getIdentifier(state.hubBackground, "drawable", context.packageName)
        
        if (bgResId != 0) {
            Image(
                painter = painterResource(id = bgResId),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.4f
            )
        }
        
        // Dynamic Tint based on World Stability
        Box(modifier = Modifier.fillMaxSize().background(state.hubTintColor))

        Column(modifier = Modifier.fillMaxSize().padding(GameConstants.UI.PADDING_MEDIUM)) {
            // HEADER: Info Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x80000000))
                    .padding(GameConstants.UI.PADDING_SMALL),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = state.locationName.uppercase(),
                        color = Color(0xFFE0C080),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "DZIEŃ ${state.day} | ${state.timeOfDay.uppercase()}",
                        color = Color.Gray,
                        fontSize = 10.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${state.gold} G",
                        color = Color(0xFFFFD700),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(GameConstants.UI.PADDING_MEDIUM))
                    WorldPhaseWidget(stability = state.worldStability)
                }
            }

            Spacer(modifier = Modifier.height(GameConstants.UI.PADDING_MEDIUM))

            Row(modifier = Modifier.weight(1f)) {
                // LEFT: Main Navigation Grid
                Column(
                    modifier = Modifier.weight(1.5f), 
                    verticalArrangement = Arrangement.spacedBy(GameConstants.UI.PADDING_SMALL)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(), 
                        horizontalArrangement = Arrangement.spacedBy(GameConstants.UI.PADDING_SMALL)
                    ) {
                        HubNavButton("MIASTO", modifier = Modifier.weight(1f), onClick = onCity)
                        HubNavButton("MAPA", modifier = Modifier.weight(1f), onClick = onMap)
                        HubNavButton("PLECAK", modifier = Modifier.weight(1f), onClick = onInventory)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(), 
                        horizontalArrangement = Arrangement.spacedBy(GameConstants.UI.PADDING_SMALL)
                    ) {
                        HubNavButton("ZADANIA", modifier = Modifier.weight(1f), onClick = onQuests)
                        HubNavButton("DRUŻYNA", modifier = Modifier.weight(1f), color = Color(0xFF4A0000), onClick = { 
                            state.party.firstOrNull()?.id?.let { onCharacter(it) }
                        })
                        HubNavButton("KRONIKA", modifier = Modifier.weight(1f), onClick = onWorldLog)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(), 
                        horizontalArrangement = Arrangement.spacedBy(GameConstants.UI.PADDING_SMALL)
                    ) {
                        val expeditionCount = state.expeditionQuestsCount
                        HubNavButton(
                            text = if (expeditionCount > 0) "EKSPEDYCJA ($expeditionCount)" else "BRAK WYPRAW",
                            modifier = Modifier.weight(1.5f),
                            color = if (expeditionCount > 0) Color(0xFF4A6000) else Color(0xFF1A1A1A),
                            enabled = expeditionCount > 0,
                            onClick = onExpedition
                        )
                    }

                    Spacer(modifier = Modifier.height(GameConstants.UI.PADDING_MEDIUM))

                    // WORLD STATUS LOG MINI
                    Surface(
                        color = Color(0x40000000),
                        shape = MaterialTheme.shapes.extraSmall,
                        modifier = Modifier.fillMaxWidth().weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(GameConstants.UI.PADDING_SMALL)) {
                            Text("STATUS ŚWIATA", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(GameConstants.UI.PADDING_SMALL))
                            Text(
                                text = state.atmosphericMessage,
                                color = if (state.worldStability < 40) Color(0xFFB22222) else Color.LightGray,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(GameConstants.UI.PADDING_MEDIUM))

                // RIGHT: World Log Summary
                Surface(
                    color = Color(0x10FFFFFF),
                    modifier = Modifier.weight(1f).fillMaxHeight()
                ) {
                    Column(modifier = Modifier.padding(GameConstants.UI.PADDING_SMALL)) {
                        Text("OSTATNIE WIEŚCI:", color = Color.Gray, fontSize = 10.sp)
                        Spacer(modifier = Modifier.height(GameConstants.UI.PADDING_SMALL))
                        Text("Mieszkańcy szepczą o powrocie Proroka Aeliona...", color = Color.LightGray, fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(GameConstants.UI.PADDING_MEDIUM))

            // BOTTOM: Party Strip
            Text("TWOJA DRUŻYNA", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(GameConstants.UI.PADDING_SMALL)
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
        modifier = modifier.height(GameConstants.UI.BUTTON_HEIGHT_DEFAULT + 10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color, disabledContainerColor = Color(0xFF0F0F0F)),
        shape = MaterialTheme.shapes.extraSmall,
        border = androidx.compose.foundation.BorderStroke(GameConstants.UI.BORDER_WIDTH, Color(0xFF333333))
    ) {
        Text(text = text, color = if (enabled) Color(0xFFE0C080) else Color.DarkGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun PartyMemberCard(hero: Hero, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.width(140.dp).clickable { onClick() },
        color = Color(0xFF151515),
        shape = MaterialTheme.shapes.extraSmall,
        border = androidx.compose.foundation.BorderStroke(GameConstants.UI.BORDER_WIDTH, Color(0xFF444444))
    ) {
        Column(modifier = Modifier.padding(GameConstants.UI.PADDING_SMALL)) {
            Text(text = hero.name, color = Color(0xFFE0C080), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            LinearProgressIndicator(
            progress = { if (hero.maxHp > 0) hero.hp.toFloat() / hero.maxHp else 0f },
            modifier = Modifier.fillMaxWidth().height(4.dp).padding(vertical = 6.dp),
            color = Color(0xFFB22222),
            trackColor = Color(0xFF222222)
        )
            Text(text = "${hero.hp}/${hero.maxHp} HP", color = Color.Gray, fontSize = 10.sp)
        }
    }
}
