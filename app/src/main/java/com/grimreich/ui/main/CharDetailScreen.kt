package com.grimreich.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimreich.core.Hero

@Composable
fun CharDetailScreen(
    hero: Hero,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(hero.name.uppercase(), color = Color(0xFFE0C080), fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(hero.currentCareer?.name ?: "Bez profesji", color = Color(0xFFC0A060), fontSize = 14.sp)
        Spacer(modifier = Modifier.height(12.dp))

        Text("HP: ${hero.hp}/${hero.maxHp}", color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))

        Text("SIŁA: ${hero.strength}", color = Color.LightGray)
        Text("ZRĘCZNOŚĆ: ${hero.agility}", color = Color.LightGray)
        Text("PERCEPCJA: ${hero.perception}", color = Color.LightGray)
        Text("INTELIGENCJA: ${hero.intelligence}", color = Color.LightGray)
        Text("WYTRZYMAŁOŚĆ: ${hero.endurance}", color = Color.LightGray)
        Text("CHARYZMA: ${hero.charisma}", color = Color.LightGray)
        Text("POBOŻNOŚĆ: ${hero.piety}", color = Color.LightGray)

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF400000))
        ) {
            Text("POWRÓT")
        }
    }
}
