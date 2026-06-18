package com.grimreich.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimreich.core.Hero

@Composable
fun CharacterDetailScreen(hero: Hero, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp)
    ) {
        // Header
        Text(
            text = hero.name.uppercase(),
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFFE0C080),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            text = hero.currentCareer?.name ?: "Wędrowiec",
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            item {
                StatItem("ZDROWIE", "${hero.hp}/${hero.maxHp}", Color.Red)
                StatItem("SIŁA", "${hero.strength}", Color.White)
                StatItem("ZRĘCZNOŚĆ", "${hero.agility}", Color.White)
                StatItem("INTELIGENCJA", "${hero.intelligence}", Color.White)
                StatItem("POBOŻNOŚĆ", "${hero.piety}", Color.White)
            }
            
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text("SPECJALIZACJE", color = Color(0xFF800000), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                HorizontalDivider(color = Color(0xFF333333), modifier = Modifier.padding(vertical = 8.dp))
            }
            
            if (hero.skills.isEmpty()) {
                item { Text("Brak unikalnych biegłości.", color = Color.DarkGray, fontSize = 12.sp) }
            } else {
                hero.skills.forEach { (name, value) ->
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = name.uppercase(), color = Color.LightGray, fontSize = 12.sp)
                            Text(text = "$value%", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().height(50.dp).padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF500000))
        ) {
            Text("POWRÓT", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Text(text = value, color = valueColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}
