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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimreich.R
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
            text = stringResource(R.string.ritual_title),
            color = Color.Red,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.ritual_desc, hero.name),
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
                Text(stringResource(R.string.ritual_consequences_title), color = Color.Red, fontWeight = FontWeight.Bold)
                Text(stringResource(R.string.ritual_consequence_stability), color = Color.White, fontSize = 14.sp)
                Text(stringResource(R.string.ritual_consequence_corruption), color = Color.White, fontSize = 14.sp)
                Text(stringResource(R.string.ritual_consequence_sanity), color = Color.White, fontSize = 14.sp)
                Text(stringResource(R.string.ritual_consequence_cost), color = Color.White, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(stringResource(R.string.ritual_gold_status, gold), color = if (gold >= 100) Color.Yellow else Color.Red, fontWeight = FontWeight.Bold)
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
            Text(stringResource(R.string.ritual_btn_perform), fontWeight = FontWeight.Bold)
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
            Text(stringResource(R.string.ritual_btn_sacrifice), color = Color.White)
        }
        
        if (!canRevive) {
            val reason = if (gold < 100) stringResource(R.string.ritual_error_gold) else stringResource(R.string.ritual_error_generic)
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
