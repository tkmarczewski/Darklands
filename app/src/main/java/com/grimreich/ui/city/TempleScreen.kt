package com.grimreich.ui.city

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimreich.R
import com.grimreich.core.Hero
import com.grimreich.ui.shared.*

@Composable
fun TempleScreen(
    viewModel: TempleViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Color.Black).padding(4.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // --- GÓRNY PASEK: STATUS WIARY ---
            Row(
                modifier = Modifier.fillMaxWidth().height(30.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "KAPLICA CZYSTEGO ŚWIATŁA", color = Color(0xFFC0A060), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "WIARA: ${state.faith}", color = Color(0xFFADFF2F), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(16.dp))
                    Image(painter = painterResource(id = R.drawable.ic_currency_gold), contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${state.gold} gp", color = Color.Yellow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // --- KOKPIT ŚWIĄTYNI (3 KAFLE V9) ---
            Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                
                // 1. LEWY KAFEL: LOGI I OBJAWIENIA
                GothicObsidianCard(modifier = Modifier.weight(0.8f).fillMaxHeight()) {
                    Text(text = "DZIENNIK DUSZY", color = Color(0xFFC0A060), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    HorizontalDivider(color = Color(0x33C0A060), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                    
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            Text(
                                text = if (state.logs.isEmpty()) "> Cisza kaplicy koi zmęczone umysły." else "> ${state.logs}",
                                color = Color.LightGray,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                        if (state.isNegotiating) {
                            item {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "OSTRZEŻENIE: Targowanie się z siłami wyższymi narusza stabilność rzeczywistości.",
                                    color = Color.Yellow,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // 2. ŚRODKOWY KAFEL: DRUŻYNA (POSŁUGA)
                GothicObsidianCard(modifier = Modifier.weight(1.2f).fillMaxHeight(), headerColor = Color(0xFF1A237E)) {
                    Text(text = "STAN DUCHOWY", color = Color(0xFFC0A060), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    HorizontalDivider(color = Color(0x33C0A060), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                    
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(state.party) { hero ->
                            HeroTempleCardV9(
                                hero = hero, 
                                onPray = { viewModel.pray(hero.id) }, 
                                onResurrect = { viewModel.resurrect(hero.id) },
                                canNegotiate = !state.isNegotiating,
                                onToggleNegotiation = { viewModel.toggleNegotiation() }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // 3. PRAWY KAFEL: RYTUAŁY
                Column(modifier = Modifier.weight(0.7f).fillMaxHeight()) {
                    GothicObsidianCard(modifier = Modifier.weight(1f), headerColor = Color(0xFF4A148C)) {
                        Text(text = "RYTUAŁY", color = Color(0xFFC0A060), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        HorizontalDivider(color = Color(0x33C0A060), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                        
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(top = 4.dp)) {
                            NavTabV9("ZŁÓŻ OFIARĘ (100 G)", onClick = { viewModel.makeOffering(100) }, color = Color(0xFF2E1A1A))
                            if (state.isNegotiating) {
                                NavTabV9("ANULUJ TARG", onClick = { viewModel.toggleNegotiation() }, color = Color(0xFF400000))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    GothicObsidianCard(modifier = Modifier.weight(0.5f), headerColor = Color(0xFF400000)) {
                        Text(text = "POWRÓT", color = Color(0xFFC0A060), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        NavTabV9("WYJDŹ Z KAPLICY", onClick = onBack, color = Color(0xFF400000))
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
                        HeroPortraitV9(hero = hero, onClick = { /* No action here */ })
                    }
                }
            }
        }
    }
}

@Composable
fun HeroTempleCardV9(
    hero: Hero, 
    onPray: () -> Unit, 
    onResurrect: () -> Unit,
    canNegotiate: Boolean,
    onToggleNegotiation: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF0F0F0F),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (hero.isDead) Color.Red else Color(0xFF333333))
    ) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = hero.name.uppercase(), color = if (hero.isDead) Color.Red else Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Text(text = if (hero.isDead) "POLEGŁY" else "POCZYTALNOŚĆ: ${hero.sanity}%", color = Color.Gray, fontSize = 9.sp)
            }
            if (hero.isDead) {
                Column(horizontalAlignment = Alignment.End) {
                    NavTabV9("WSKRZESZ", onClick = onResurrect, modifier = Modifier.width(80.dp), color = Color(0xFF600000))
                    if (canNegotiate) {
                        Text(
                            text = "TARGUJ SIĘ", 
                            color = Color.DarkGray, 
                            fontSize = 8.sp, 
                            modifier = Modifier.clickable { onToggleNegotiation() }.padding(top = 2.dp)
                        )
                    }
                }
            } else {
                NavTabV9("MÓDL SIĘ", onClick = onPray, modifier = Modifier.width(80.dp), color = Color(0xFF1A1A1A))
            }
        }
    }
}
