package com.grimreich.ui.ritual

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimreich.core.Hero
import com.grimreich.systems.RitualSystem

@Composable
fun RitualScreen(
    hero: Hero,
    gold: Int,
    ritualSystem: RitualSystem,
    onRevived: () -> Unit,
    onSacrificed: () -> Unit
) {
    val canRevive = ritualSystem.canPerformResurrection(hero, gold)
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
            .padding(24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "RYTUAŁ ECHA",
            color = Color.Red,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "${hero.name} przekroczył granicę. Jego dusza dryfuje w Pęknięciu. Czy odważysz się przyciągnąć ją z powrotem?",
            color = Color.LightGray,
            textAlign = TextAlign.Center,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        Surface(
            color = Color(0xFF1A0000),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("KONSEKWENCJE WSKRZESZENIA:", color = Color.Red, fontWeight = FontWeight.Bold)
                Text("- Stabilność Świata: -15 (Era Pęknięcia się zbliża)", color = Color.White, fontSize = 14.sp)
                Text("- Korupcja Bohatera: +20", color = Color.White, fontSize = 14.sp)
                Text("- Sanity Bohatera: -15", color = Color.White, fontSize = 14.sp)
                Text("- Koszt: 100 złota", color = Color.White, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("TWOJE ZŁOTO: $gold", color = if (gold >= 100) Color.Yellow else Color.Red, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (ritualSystem.performResurrection(hero.id)) {
                    onRevived()
                }
            },
            enabled = canRevive,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (canRevive) Color(0xFF400000) else Color.DarkGray
            )
        ) {
            Text("WYKONAJ RYTUAŁ ECHA", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                ritualSystem.sacrificeHero(hero.id)
                onSacrificed()
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A1A1A)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray)
        ) {
            Text("POZWÓL MU ODEJŚĆ (PERMANENTNA ŚMIERĆ)", color = Color.White)
        }
        
        if (!canRevive) {
            val reason = if (gold < 100) "Masz zbyt mało złota (wymagane 100)." else "Nie można przeprowadzić rytuału."
            Text(
                reason,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
