package com.grimreich.ui.shared

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.grimreich.R

/**
 * Minimalistyczne menu "Pióro" — rozwija się po kliknięciu w wachlarz opcji.
 */
@Composable
fun ExpandingQuillMenu(
    onMap: () -> Unit,
    onInventory: () -> Unit,
    onChronicle: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(contentAlignment = Alignment.BottomEnd) {
        // Wachlarz Opcji
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandIn(expandFrom = Alignment.BottomEnd),
            exit = fadeOut() + shrinkOut(shrinkTowards = Alignment.BottomEnd)
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 60.dp, end = 8.dp)
            ) {
                MenuIcon(R.drawable.ic_item_bow, "Mapa", onMap)
                MenuIcon(R.drawable.ui_panel_inventory, "Ekwipunek", onInventory)
                MenuIcon(R.drawable.ic_item_mace, "Kronika", onChronicle)
            }
        }

        // Główna ikona (Pióro / Sztylet)
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color(0xFF3E2723))
                .clickable { expanded = !expanded }
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_item_dagger), 
                contentDescription = "Menu"
            )
        }
    }
}

@Composable
fun MenuIcon(resId: Int, label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(Color(0xFF5D4037))
            .clickable { onClick() }
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(painter = painterResource(id = resId), contentDescription = label)
    }
}
