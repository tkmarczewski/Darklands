package com.grimreich.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimreich.R
import com.grimreich.core.Hero

@Composable
fun CharDetailScreen(
    hero: Hero,
    onBack: () -> Unit,
    onUpgrade: (String) -> Unit,
    onRandomize: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050505))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = hero.name.uppercase(),
                    color = Color(0xFFC0A060),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                hero.masteryTrait?.let { mastery ->
                    Text(
                        text = "MISTRZOSTWO: ${mastery.uppercase().replace("_", " ")}",
                        color = Color(0xFF4A6000),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A2A2A))
            ) {
                Text(stringResource(R.string.btn_back), color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.label_stats), color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                
                StatControlRow("STR", hero.strength, hero.attributePoints > 0) { onUpgrade("STR") }
                StatControlRow("AGI", hero.agility, hero.attributePoints > 0) { onUpgrade("AGI") }
                StatControlRow("INT", hero.intelligence, hero.attributePoints > 0) { onUpgrade("INT") }
                StatControlRow("PER", hero.perception, hero.attributePoints > 0) { onUpgrade("PER") }
                StatControlRow("END", hero.endurance, hero.attributePoints > 0) { onUpgrade("END") }
                StatControlRow("CHA", hero.charisma, hero.attributePoints > 0) { onUpgrade("CHA") }
                StatControlRow("PIE", hero.piety, hero.attributePoints > 0) { onUpgrade("PIE") }
            }

            Spacer(modifier = Modifier.width(24.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("STATUS", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(stringResource(R.string.label_hp_current, hero.hp, hero.maxHp), color = Color.White)
                Text(stringResource(R.string.label_level_xp, hero.level, hero.xp, hero.level * 100), color = Color.Gray, fontSize = 12.sp)
                
                if (hero.attributePoints > 0) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onRandomize,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A6000)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            stringResource(R.string.creator_points_label) + ": ${hero.attributePoints} [LOSOWO]",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatControlRow(label: String, value: Int, canUpgrade: Boolean, onUpgrade: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(value.toString(), color = Color(0xFFC0A060), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            if (canUpgrade) {
                IconButton(onClick = onUpgrade, modifier = Modifier.size(24.dp)) {
                    Text("+", color = Color.White)
                }
            }
        }
    }
}
