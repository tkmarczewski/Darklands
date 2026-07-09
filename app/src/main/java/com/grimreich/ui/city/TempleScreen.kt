package com.grimreich.ui.city

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimreich.core.Hero

@Composable
fun TempleScreen(
    viewModel: TempleViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Text("KAPLICA CZYSTEGO ŚWIATŁA", color = Color(0xFFE0C080), fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("WIARA: ${state.faith} | ZŁOTO: ${state.gold} G", color = Color.Gray, fontSize = 12.sp)

        Spacer(modifier = Modifier.height(16.dp))

        if (state.logs.isNotBlank()) {
            Surface(
                color = Color(0xFF1A1A1A),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC0A060))
            ) {
                Text(state.logs, color = Color.White, modifier = Modifier.padding(12.dp), fontSize = 13.sp)
            }
        }

        if (state.isNegotiating) {
            Surface(
                color = Color(0xFF201000),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("TARGOWANIE: Cena spadnie do 150 G, ale konsekwencje dla paradygmatu będą znacznie cięższe. Czy na pewno?", color = Color.Yellow, fontSize = 12.sp)
                    Button(onClick = { viewModel.toggleNegotiation() }, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
                        Text("ANULUJ", color = Color.Gray)
                    }
                }
            }
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(state.party) { hero ->
                HeroTempleCard(
                    hero = hero, 
                    onPray = { viewModel.pray(hero.id) }, 
                    onResurrect = { viewModel.resurrect(hero.id) },
                    canNegotiate = !state.isNegotiating,
                    onToggleNegotiation = { viewModel.toggleNegotiation() }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { viewModel.makeOffering(100) }, modifier = Modifier.weight(1f)) { Text("OFIARA 100G") }
            Button(
                onClick = onBack, 
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF400000))
            ) { 
                Text("WYJDŹ") 
            }
        }
    }
}

@Composable
fun HeroTempleCard(
    hero: Hero, 
    onPray: () -> Unit, 
    onResurrect: () -> Unit,
    canNegotiate: Boolean,
    onToggleNegotiation: () -> Unit
) {
    Surface(
        color = Color(0xFF111111),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (hero.isDead) Color.Red else Color.DarkGray),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(hero.name.uppercase(), color = if (hero.isDead) Color.Red else Color.White, fontWeight = FontWeight.Bold)
                Text(if (hero.isDead) "POLEGŁY (Wymaga ciała)" else "SANITY: ${hero.sanity}/100", color = Color.Gray, fontSize = 11.sp)
            }
            if (hero.isDead) {
                Column(horizontalAlignment = Alignment.End) {
                    Button(
                        onClick = onResurrect,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF600000)),
                        shape = MaterialTheme.shapes.extraSmall
                    ) {
                        Text("WSKRZESZ", fontSize = 10.sp)
                    }
                    if (canNegotiate) {
                        TextButton(onClick = onToggleNegotiation) {
                            Text("TARGUJ SIĘ", color = Color.DarkGray, fontSize = 9.sp)
                        }
                    }
                }
            } else {
                Button(
                    onClick = onPray,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF222222)),
                    shape = MaterialTheme.shapes.extraSmall
                ) {
                    Text("MÓDL SIĘ", fontSize = 10.sp)
                }
            }
        }
    }
}
