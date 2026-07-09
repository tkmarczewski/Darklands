package com.grimreich.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimreich.core.Hero

@Composable
fun CharDetailScreen(
    hero: Hero,
    onUpgrade: (String) -> Unit,
    onRandomize: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(hero.name.uppercase(), color = Color(0xFFE0C080), fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(hero.currentCareer?.displayName ?: "Bez profesji", color = Color(0xFFC0A060), fontSize = 14.sp)
            }
            if (hero.attributePoints > 0) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = onRandomize,
                        modifier = Modifier.height(32.dp).padding(end = 8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A6000))
                    ) {
                        Text("LOSUJ", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Surface(
                        color = Color(0xFFB22222),
                        shape = MaterialTheme.shapes.extraSmall
                    ) {
                        Text(
                            text = "PUNKTY: ${hero.attributePoints}",
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))

        Text("HP: ${hero.hp}/${hero.maxHp}", color = Color.White)
        Text("POZIOM: ${hero.level} | XP: ${hero.xp}/${hero.level * 100}", color = Color.Gray, fontSize = 12.sp)
        
        Spacer(modifier = Modifier.height(16.dp))

        StatRow("SIŁA", hero.strength, "STR", hero.attributePoints > 0, onUpgrade)
        StatRow("ZRĘCZNOŚĆ", hero.agility, "AGI", hero.attributePoints > 0, onUpgrade)
        StatRow("PERCEPCJA", hero.perception, "PER", hero.attributePoints > 0, onUpgrade)
        StatRow("INTELIGENCJA", hero.intelligence, "INT", hero.attributePoints > 0, onUpgrade)
        StatRow("WYTRZYMAŁOŚĆ", hero.endurance, "END", hero.attributePoints > 0, onUpgrade)
        StatRow("CHARYZMA", hero.charisma, "CHA", hero.attributePoints > 0, onUpgrade)
        StatRow("POBOŻNOŚĆ", hero.piety, "PIE", hero.attributePoints > 0, onUpgrade)

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF400000))
        ) {
            Text("POWRÓT", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun StatRow(label: String, value: Int, code: String, canUpgrade: Boolean, onUpgrade: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Color.LightGray, modifier = Modifier.weight(1f), fontSize = 14.sp)
        Text(text = value.toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.width(16.dp))
        
        if (canUpgrade) {
            Button(
                onClick = { onUpgrade(code) },
                modifier = Modifier.height(28.dp).width(40.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A2A2A))
            ) {
                Text("+", color = Color.White)
            }
        } else {
            Spacer(modifier = Modifier.width(40.dp))
        }
    }
}
