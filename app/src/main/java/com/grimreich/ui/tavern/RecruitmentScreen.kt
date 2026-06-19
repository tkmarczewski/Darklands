package com.grimreich.ui.tavern

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.grimreich.core.Hero

@Composable
fun RecruitmentScreen(
    onBack: () -> Unit,
    viewModel: RecruitmentViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

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
            modifier = Modifier.padding(bottom = 8.dp).align(Alignment.CenterHorizontally)
        )
        Text(
            text = "Złoto: ${state.gold} zł",
            color = Color.Yellow,
            fontSize = 14.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 16.dp)
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(state.availableHeroes) { hero ->
                HireableItem(hero) {
                    viewModel.hireHero(hero)
                }
            }
        }

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().height(50.dp).padding(top = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF400000))
        ) {
            Text("POWRÓT", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun HireableItem(hero: Hero, onHire: () -> Unit) {
    val context = LocalContext.current
    val portResId = context.resources.getIdentifier(hero.portraitRes ?: "port_rogue", "drawable", context.packageName)

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0A0A)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF222222))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(60.dp),
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

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = hero.name, color = Color.White, fontWeight = FontWeight.Bold)
                Text(text = "HP: ${hero.maxHp} | SIŁ: ${hero.strength}", color = Color.LightGray, fontSize = 12.sp)
            }

            Button(
                onClick = onHire,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A4000))
            ) {
                Text("50 zł", color = Color.White)
            }
        }
    }
}
