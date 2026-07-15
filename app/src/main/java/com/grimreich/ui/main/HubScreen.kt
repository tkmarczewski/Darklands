package com.grimreich.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.*
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimreich.R
import com.grimreich.core.Hero
import com.grimreich.ui.shared.*

/**
 * REDESIGN V9: "GOTHIC COMMAND CENTER"
 * Wszystko pod ręką. Gęsty, kafelkowy układ na czarnym obsydianie ze złotem.
 */
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

    LaunchedEffect(state.worldStability) {
        viewModel.checkForEnding { onEnding() }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black).padding(4.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // --- GÓRNY PASEK: DATA I STATUS ---
            Row(
                modifier = Modifier.fillMaxWidth().height(30.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "${stringResource(R.string.hub_day_label)} ${state.day} | ${state.timeOfDay.uppercase()}", color = Color(0xFFC0A060), fontSize = 12.sp)
                Text(text = state.locationName.uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = "${stringResource(R.string.hub_stability_label)}: ${state.worldStability}%", color = if(state.worldStability < 40) Color.Red else Color.Green, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(4.dp))

            // --- GŁÓWNY KOKPIT (3 KAFLE) ---
            Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                
                // 1. LEWY KAFEL: LOGI (KRONIKA)
                GothicObsidianCard(modifier = Modifier.weight(0.7f).fillMaxHeight()) {
                    Text(text = stringResource(R.string.hub_chronicle_label), color = Color(0xFFC0A060), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    HorizontalDivider(color = Color(0x33C0A060), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(state.latestLogs) { log ->
                            Text(text = "> $log", color = Color.LightGray, fontSize = 11.sp, modifier = Modifier.padding(bottom = 4.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // 2. ŚRODKOWY KAFEL: WIZJA ŚWIATA
                GothicObsidianCard(modifier = Modifier.weight(1.3f).fillMaxHeight(), headerColor = Color(0xFF1A237E)) {
                    val context = LocalContext.current
                    val bgResId = context.resources.getIdentifier(state.hubBackground, "drawable", context.packageName)
                    if (bgResId != 0) {
                        Image(
                            painter = painterResource(id = bgResId),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // 3. PRAWY KAFEL: NAWIGACJA (MAPA + AKCJE)
                Column(modifier = Modifier.weight(0.7f).fillMaxHeight()) {
                    GothicObsidianCard(modifier = Modifier.weight(0.6f)) {
                        ParchmentMinimap(locationName = state.locationName)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    GothicObsidianCard(modifier = Modifier.weight(1.4f), headerColor = Color(0xFF1B5E20)) {
                        Text(text = stringResource(R.string.hub_actions_label), color = Color(0xFFC0A060), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp), 
                            modifier = Modifier.padding(top = 8.dp).verticalScroll(rememberScrollState())
                        ) {
                            NavTabV9(stringResource(R.string.hub_btn_to_city), onClick = onCity)
                            
                            // --- EXPEDITION VISIBILITY FIX ---
                            // Only allow expeditions if there are active quests that require them
                            // or if it's the start of the game (tutorial context).
                            if (state.expeditionQuestsCount > 0 || state.day < 2) {
                                NavTabV9(stringResource(R.string.hub_btn_to_expedition), onClick = onExpedition, color = Color(0xFF3E2723))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // --- DOLNY PASEK: DRUŻYNA (CENTRUM KONTROLI) ---
            GothicObsidianCard(modifier = Modifier.fillMaxWidth().height(100.dp)) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    state.party.forEach { hero ->
                        HeroPortraitV9(hero = hero, onClick = { onCharacter(hero.id) })
                    }
                    // ZŁOTO NA KOŃCU PASKA
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(painter = painterResource(id = R.drawable.ic_currency_gold), contentDescription = null, modifier = Modifier.size(24.dp))
                        Text(text = "${state.gold} gp", color = Color(0xFFC0A060), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // --- QUICK ACTIONS: Expanding Quill Menu ---
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
            ExpandingQuillMenu(
                onMap = onMap,
                onInventory = onInventory,
                onChronicle = onWorldLog,
                onQuests = onQuests
            )
        }
    }
}
