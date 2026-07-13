package com.grimreich.ui.shared

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimreich.R
import com.grimreich.core.Hero

/**
 * Przycisk nawigacyjny w stylu V9 (Gothic Obsidian).
 */
@Composable
fun NavTabV9(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, color: Color = Color(0xFF2E1A1A)) {
    Surface(
        modifier = modifier.fillMaxWidth().height(32.dp).clickable { onClick() },
        color = color,
        border = BorderStroke(1.dp, Color(0xFFC0A060))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = text.uppercase(), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

/**
 * Portret bohatera w dolnym pasku (Command Center V9).
 */
@Composable
fun HeroPortraitV9(hero: Hero, onClick: () -> Unit) {
    Column(
        modifier = Modifier.width(60.dp).clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            Image(
                painter = painterResource(id = R.drawable.ui_frame_portrait_mini),
                contentDescription = null,
                modifier = Modifier.size(50.dp)
            )
            // Diegetyczne HP BAR (Overlay)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(if (hero.maxHp > 0) hero.hp.toFloat() / hero.maxHp else 0f)
                    .height(3.dp)
                    .background(Color.Red)
            )
        }
        Text(
            text = hero.name.uppercase(), 
            color = if (hero.isDead) Color.Red else Color.Gray, 
            fontSize = 8.sp, 
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}
