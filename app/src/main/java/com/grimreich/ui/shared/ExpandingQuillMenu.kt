package com.grimreich.ui.shared

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Minimalistyczne menu "Pióro" — rozwija się po kliknięciu w wachlarz opcji.
 * REDESIGN: Używa stylizowanych liter zamiast brakujących ikon.
 */
@Composable
fun ExpandingQuillMenu(
    onMap: () -> Unit,
    onInventory: () -> Unit,
    onChronicle: () -> Unit,
    onQuests: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.padding(16.dp)) {
        // Wachlarz Opcji
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandIn(expandFrom = Alignment.BottomEnd),
            exit = fadeOut() + shrinkOut(shrinkTowards = Alignment.BottomEnd)
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(bottom = 70.dp)
            ) {
                MenuLabelIcon("Q", "QUESTS", onQuests, Color(0xFF1B5E20))
                MenuLabelIcon("C", "CHRONICLE", onChronicle, Color(0xFFC0A060))
                MenuLabelIcon("I", "INVENTORY", onInventory, Color(0xFF0D47A1))
                MenuLabelIcon("M", "MAP", onMap, Color(0xFF4A0000))
            }
        }

        // Główna ikona (Pióro / Kotwica)
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(if (expanded) Color(0xFF212121) else Color(0xFFC0A060))
                .clickable { expanded = !expanded }
                .padding(2.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Color(0xFF050505)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (expanded) "X" else "۞", 
                    color = Color(0xFFC0A060), 
                    fontSize = 24.sp, 
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun MenuLabelIcon(letter: String, label: String, onClick: () -> Unit, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onClick() }) {
        SurfaceV9(color = Color(0xCC000000), modifier = Modifier.padding(end = 8.dp)) {
            Text(text = label, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
        }
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(color)
                .padding(2.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Color(0xFF050505)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = letter, color = color, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
fun SurfaceV9(color: Color, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(modifier = modifier.background(color).padding(2.dp)) {
        content()
    }
}
