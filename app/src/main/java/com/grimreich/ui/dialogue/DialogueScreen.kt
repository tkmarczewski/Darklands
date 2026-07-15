package com.grimreich.ui.dialogue

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimreich.R
import com.grimreich.ui.shared.*
import com.grimreich.ui.effects.glitchEffect
import kotlin.random.Random

@Composable
fun DialogueScreen(
    viewModel: DialogueViewModel,
    onExit: () -> Unit,
    onMarket: () -> Unit,
    onCombat: () -> Unit,
    onRitual: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize().background(Color.Black).padding(4.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // --- GÓRNY PASEK: NPC I STATUS ---
            Row(
                modifier = Modifier.fillMaxWidth().height(30.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "INTERAKCJA: ${state.npcName.uppercase()}", color = Color(0xFFC0A060), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(text = "ROLA: ${state.npcRole.uppercase()}", color = Color.Gray, fontSize = 11.sp)
            }

            Spacer(modifier = Modifier.height(4.dp))

            // --- KOKPIT DIALOGOWY (3 KAFLE V9) ---
            Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                
                // 1. LEWY KAFEL: LOGI I KRONIKA SPOTKANIA
                GothicObsidianCard(modifier = Modifier.weight(0.7f).fillMaxHeight()) {
                    Text(text = "KRONIKA SPOTKANIA", color = Color(0xFFC0A060), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    HorizontalDivider(color = Color(0x33C0A060), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                    
                    Text(
                        text = "> Każde słowo zostaje zapisane. Trybunał obserwuje Twoje wybory.",
                        color = Color.DarkGray,
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // 2. ŚRODKOWY KAFEL: WIDOK NPC I TEKST
                GothicObsidianCard(modifier = Modifier.weight(1.3f).fillMaxHeight(), headerColor = Color(0xFF4A0000)) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // PORTRET NPC
                        Box(modifier = Modifier.fillMaxWidth().weight(0.6f)) {
                            val portResId = context.resources.getIdentifier(state.npcPortrait, "drawable", context.packageName)
                            if (portResId != 0) {
                                Image(
                                    painter = painterResource(id = portResId),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize().glitchEffect(state.worldStability < 40, 0.1f),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }
                        
                        HorizontalDivider(color = Color(0x33C0A060), thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
                        
                        // TEKST DIALOGU
                        val rawText = state.currentNode?.text ?: "Cisza..."
                        Text(
                            text = rawText,
                            color = if (state.worldStability < 15) Color.Red else Color.White,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            modifier = Modifier.weight(0.4f).padding(horizontal = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // 3. PRAWY KAFEL: WYBORY
                Column(modifier = Modifier.weight(0.7f).fillMaxHeight()) {
                    GothicObsidianCard(modifier = Modifier.weight(1f), headerColor = Color(0xFF1B5E20)) {
                        Text(text = "WYBORY", color = Color(0xFFC0A060), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        HorizontalDivider(color = Color(0x33C0A060), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                        
                        if (state.availableChoices.isEmpty() || state.currentNode == null) {
                            NavTabV9("ZAKOŃCZ ROZMOWĘ", onClick = onExit, color = Color(0xFF400000))
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(state.availableChoices) { info ->
                                    val choiceColor = when {
                                        info.choice.isCombatTrigger -> Color(0xFF4A0000)
                                        !info.isEnabled -> Color(0xFF0A0A0A)
                                        else -> Color(0xFF1A1A1A)
                                    }
                                    NavTabV9(
                                        text = info.choice.text,
                                        onClick = { viewModel.choose(info.choice, onExit, onCombat, onMarket, onRitual) },
                                        color = choiceColor,
                                        enabled = info.isEnabled
                                    )
                                }
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
                    // Pasek drużyny dla kontekstu podczas dialogów
                    Text("DRUŻYNA ŚWIADKÓW", color = Color.DarkGray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun NavTabV9(text: String, onClick: () -> Unit, color: Color = Color(0xFF1A1A1A), enabled: Boolean = true, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth().heightIn(min = 36.dp).clickable(enabled = enabled) { onClick() },
        color = if (enabled) color else Color.Black,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (enabled) Color(0xFFC0A060) else Color(0xFF111111))
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(8.dp)) {
            Text(text = text.uppercase(), color = if (enabled) Color.White else Color.DarkGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}
