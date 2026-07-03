package com.grimreich.ui.ritual

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
    globalStability: Int,
    ritualSystem: RitualSystem,
    onRevived: () -> Unit,
    onSacrificed: () -> Unit
) {
    val canRevive = ritualSystem.canPerformResurrection(hero, globalStability)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "RYTUAŁ ECHA",
            color = Color.Red,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "${hero.name} przekroczył granicę. Jego dusza dryfuje w Pęknięciu. Czy odważysz się przyciągnąć ją z powroce?",
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
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

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
            Text("PERFORM RITUAL OF ECHOES", fontWeight = FontWeight.Bold)
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
            Text(
                "Stabilność świata jest zbyt niska, by przeprowadzić rytuał.",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
