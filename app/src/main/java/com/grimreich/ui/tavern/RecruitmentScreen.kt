package com.grimreich.ui.tavern

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.grimreich.core.GameConstants
import com.grimreich.core.Hero

@Composable
fun RecruitmentScreen(
    onBack: () -> Unit,
    viewModel: RecruitmentViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    // Odśwież pulę przy każdym wejściu na ekran
    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Text(
            text = "WERBUNEK",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFFC0A060),
            modifier = Modifier
                .padding(bottom = 4.dp)
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = "Dostępni najemnicy w tej karczmie",
            color = Color(0xFF888888),
            fontSize = 12.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 4.dp)
        )
        Text(
            text = "Złoto: ${state.gold} zł",
            color = Color.Yellow,
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp)
        )

        if (state.availableHeroes.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Wszyscy najemnicy zostali wynajęci.",
                    color = Color(0xFF666666),
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(state.availableHeroes, key = { it.id }) { hero ->
                    val cost = state.hireCosts[hero.id] ?: GameConstants.HIRE_HERO_COST
                    HireableItem(
                        hero = hero,
                        cost = cost,
                        canAfford = state.gold >= cost,
                        onHire = { viewModel.hireHero(hero) }
                    )
                }
            }
        }

        Button(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(top = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF400000))
        ) {
            Text("POWRÓT", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun HireableItem(
    hero: Hero,
    cost: Int,
    canAfford: Boolean,
    onHire: () -> Unit
) {
    val context = LocalContext.current
    val portResId = context.resources.getIdentifier(
        hero.portraitRes.ifBlank { "port_knight" },
        "drawable",
        context.packageName
    )
    val careerName = hero.currentCareer?.displayName ?: "Najemnik"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0A0A)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF333333))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Portret
            Surface(
                modifier = Modifier.size(64.dp),
                color = Color(0xFF1A1A1A),
                shape = MaterialTheme.shapes.extraSmall,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0C080))
            ) {
                if (portResId != 0) {
                    Image(
                        painter = painterResource(id = portResId),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Dane bohatera
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = hero.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = "$careerName · wiek ${hero.age}",
                    color = Color(0xFFA08040),
                    fontSize = 11.sp
                )
                Text(
                    text = "SIŁ:${hero.strength} ZRC:${hero.agility} INT:${hero.intelligence} WYT:${hero.endurance}",
                    color = Color.LightGray,
                    fontSize = 11.sp
                )
                Text(
                    text = "HP: ${hero.maxHp} · PER:${hero.perception} CHA:${hero.charisma} PIE:${hero.piety}",
                    color = Color(0xFF888888),
                    fontSize = 10.sp
                )
                // Wyposażenie
                val weaponId = hero.equipment["weapon"]
                val armorId  = hero.equipment["armor"]
                if (weaponId != null || armorId != null) {
                    Text(
                        text = listOfNotNull(
                            weaponId?.let { "⚔ $it" },
                            armorId?.let  { "🛡 $it" }
                        ).joinToString(" · "),
                        color = Color(0xFF556622),
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Przycisk zatrudnienia
            Button(
                onClick = onHire,
                enabled = canAfford,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (canAfford) Color(0xFF2A4000) else Color(0xFF2A2A2A),
                    disabledContainerColor = Color(0xFF2A2A2A)
                )
            ) {
                Text(
                    text = "$cost zł",
                    color = if (canAfford) Color.White else Color(0xFF666666),
                    fontSize = 12.sp
                )
            }
        }
    }
}
