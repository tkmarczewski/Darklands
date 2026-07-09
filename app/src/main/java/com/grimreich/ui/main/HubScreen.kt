package com.grimreich.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.res.stringResource
import com.grimreich.R
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
    onExpedition: () -> Unit,
    onEnding: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    // Global Check for Meta-Ending
    LaunchedEffect(state.worldStability) {
        viewModel.checkForEnding { onEnding() }
    }

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
                        text = state.locationNameRes?.let { stringResource(it) } ?: state.locationName.uppercase(),
                        color = Color(0xFFE0C080),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = stringResource(R.string.hub_day, state.day) + " | ${state.timeOfDay.uppercase()}",
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
                // LEFT: Main Navigation Grid (SCROLLABLE)
                val navScrollState = rememberScrollState()
                Column(
                    modifier = Modifier.weight(1.5f).verticalScroll(navScrollState), 
                    verticalArrangement = Arrangement.spacedBy(GameConstants.UI.PADDING_SMALL)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(), 
                        horizontalArrangement = Arrangement.spacedBy(GameConstants.UI.PADDING_SMALL)
                    ) {
                        HubNavButton(stringResource(R.string.menu_city), modifier = Modifier.weight(1f), onClick = onCity)
                        HubNavButton(stringResource(R.string.menu_map), modifier = Modifier.weight(1f), onClick = onMap)
                        HubNavButton(stringResource(R.string.menu_backpack), modifier = Modifier.weight(1f), onClick = onInventory)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(), 
                        horizontalArrangement = Arrangement.spacedBy(GameConstants.UI.PADDING_SMALL)
                    ) {
                        HubNavButton(stringResource(R.string.menu_quests), modifier = Modifier.weight(1f), onClick = onQuests)
                        HubNavButton(
                            text = stringResource(R.string.hub_party), 
                            modifier = Modifier.weight(1f), 
                            color = if (state.hasPendingLevelUp) Color(0xFFADFF2F) else Color(0xFF4A0000), 
                            textColor = if (state.hasPendingLevelUp) Color.Black else Color(0xFFE0C080),
                            onClick = { 
                                state.party.firstOrNull()?.id?.let { onCharacter(it) }
                            }
                        )
                        HubNavButton(stringResource(R.string.journal_title), modifier = Modifier.weight(1f), onClick = onWorldLog)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(), 
                        horizontalArrangement = Arrangement.spacedBy(GameConstants.UI.PADDING_SMALL)
                    ) {
                        val expeditionCount = state.expeditionQuestsCount
                        HubNavButton(
                            text = if (expeditionCount > 0) stringResource(R.string.hub_btn_expedition, expeditionCount) else stringResource(R.string.hub_btn_no_targets),
                            modifier = Modifier.weight(1.5f),
                            color = if (expeditionCount > 0) Color(0xFFADFF2F) else Color(0xFF1A1A1A), 
                            enabled = expeditionCount > 0,
                            onClick = onExpedition,
                            textColor = if (expeditionCount > 0) Color.Black else Color.DarkGray
                        )
                    }

                    Spacer(modifier = Modifier.height(GameConstants.UI.PADDING_MEDIUM))

                    // WORLD STATUS LOG MINI
                    Surface(
                        color = Color(0x40000000),
                        shape = MaterialTheme.shapes.extraSmall,
                        modifier = Modifier.fillMaxWidth().height(100.dp)
                    ) {
                        Column(modifier = Modifier.padding(GameConstants.UI.PADDING_SMALL)) {
                            Text(stringResource(R.string.hub_world_status), color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(GameConstants.UI.PADDING_SMALL))
                            Text(
                                text = stringResource(state.atmosphericMessageRes),
                                color = if (state.worldStability < 40) Color(0xFFB22222) else Color.LightGray,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(GameConstants.UI.PADDING_MEDIUM))

                // RIGHT: World Log Summary (SCROLLABLE)
                Surface(
                    color = Color(0x10FFFFFF),
                    modifier = Modifier.weight(1f).fillMaxHeight()
                ) {
                    val logScrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .padding(GameConstants.UI.PADDING_SMALL)
                            .verticalScroll(logScrollState)
                    ) {
                        Text(stringResource(R.string.hub_news), color = Color.Gray, fontSize = 10.sp)
                        Spacer(modifier = Modifier.height(GameConstants.UI.PADDING_SMALL))
                        
                        if (state.latestLogs.isEmpty()) {
                            Text(stringResource(R.string.hub_news_empty), color = Color.DarkGray, fontSize = 11.sp)
                        } else {
                            // Pokaż więcej logów niż tylko 5, jeśli chcemy przewijania
                            state.latestLogs.forEach { log ->
                                Text("- $log", color = Color.LightGray, fontSize = 11.sp, modifier = Modifier.padding(bottom = 4.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(GameConstants.UI.PADDING_MEDIUM))

            // BOTTOM: Party Strip
            Surface(
                color = Color(0x20FFFFFF),
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                shape = MaterialTheme.shapes.extraSmall
            ) {
                Column(modifier = Modifier.padding(GameConstants.UI.PADDING_SMALL)) {
                    Text(stringResource(R.string.hub_party), color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
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
    }
}

@Composable
fun HubNavButton(
    text: String, 
    modifier: Modifier = Modifier, 
    color: Color = Color(0xFF1A1A1A), 
    enabled: Boolean = true, 
    textColor: Color = Color(0xFFE0C080),
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(GameConstants.UI.BUTTON_HEIGHT_DEFAULT + 10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color, disabledContainerColor = Color(0xFF0F0F0F)),
        shape = MaterialTheme.shapes.extraSmall,
        border = androidx.compose.foundation.BorderStroke(GameConstants.UI.BORDER_WIDTH, Color(0xFF333333))
    ) {
        val finalTextColor = if (enabled) textColor else Color.DarkGray
        Text(text = text, color = finalTextColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
