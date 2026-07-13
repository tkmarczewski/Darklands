package com.grimreich.ui.tavern

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimreich.R
import com.grimreich.ui.shared.*

@Composable
fun TavernScreen(viewModel: TavernViewModel, onHire: () -> Unit, onExit: () -> Unit) {
    val state by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Color.Black).padding(4.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // --- GÓRNY PASEK: STATUS ---
            Row(
                modifier = Modifier.fillMaxWidth().height(30.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "LOKALNA TAVERNA", color = Color(0xFFC0A060), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(painter = painterResource(id = R.drawable.ic_currency_gold), contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${state.gold} gp", color = Color.Yellow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // --- KOKPIT TAVERNY (3 KAFLE V9) ---
            Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                
                // 1. LEWY KAFEL: LOGI I PLOTKI
                GothicObsidianCard(modifier = Modifier.weight(0.8f).fillMaxHeight()) {
                    Text(text = "ECHO ROZMÓW", color = Color(0xFFC0A060), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Divider(color = Color(0x33C0A060), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                    
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            Text(
                                text = if (state.log.isEmpty()) "> W powietrzu unosi się zapach dymu i starych opowieści." else "> ${state.log}",
                                color = Color.LightGray,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // 2. ŚRODKOWY KAFEL: ATMOSFERA
                GothicObsidianCard(modifier = Modifier.weight(1.2f).fillMaxHeight(), headerColor = Color(0xFF5D4037)) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("TAVERNA", color = Color(0xFFC0A060), fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("ODPOCZYNEK I REKRUTACJA", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // 3. PRAWY KAFEL: AKCJE
                Column(modifier = Modifier.weight(0.7f).fillMaxHeight()) {
                    GothicObsidianCard(modifier = Modifier.weight(1f), headerColor = Color(0xFF1B5E20)) {
                        Text(text = "INTERAKCJE", color = Color(0xFFC0A060), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Divider(color = Color(0x33C0A060), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                        
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(top = 4.dp)) {
                            NavTabV9("WYNAJMIJ POKÓJ (50 G)", onClick = { viewModel.rest() }, color = Color(0xFF2E1A1A))
                            NavTabV9("SŁUCHAJ PLOTEK", onClick = { viewModel.listenToGossip() })
                            NavTabV9("REKRUTUJ NAJEMNIKÓW", onClick = onHire, color = Color(0xFF0D47A1))
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    GothicObsidianCard(modifier = Modifier.weight(0.5f), headerColor = Color(0xFF400000)) {
                        Text(text = "WYJŚCIE", color = Color(0xFFC0A060), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        NavTabV9("WRÓĆ DO MIASTA", onClick = onExit, color = Color(0xFF400000))
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
                        HeroPortraitV9(hero = hero, onClick = { /* Można tu dodać akcję */ })
                    }
                }
            }
        }
    }
}
