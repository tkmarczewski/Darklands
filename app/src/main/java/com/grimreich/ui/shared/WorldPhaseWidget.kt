package com.grimreich.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimreich.core.GameConstants

@Composable
fun WorldPhaseWidget(stability: Int, modifier: Modifier = Modifier) {
    val phaseName = when {
        stability > 80 -> "ERA ZBIEŻNOŚCI"
        stability > 40 -> "ERA MATERII"
        else -> "ERA PĘKNIĘCIA"
    }

    val phaseColor = when {
        stability > 80 -> Color(0xFFE0C080)
        stability > 40 -> Color(0xFF888888)
        else -> Color(0xFFB22222)
    }

    val phaseDescription = when {
        stability > 80 -> "Rzeczywistość jest krystalicznie czysta."
        stability > 40 -> "Świat trzyma się w swoich ryzach."
        else -> "Granice między światami zanikają."
    }

    Surface(
        modifier = modifier
            .border(GameConstants.UI.BORDER_WIDTH, phaseColor.copy(alpha = 0.5f), MaterialTheme.shapes.extraSmall),
        color = Color.Black.copy(alpha = 0.7f),
        shape = MaterialTheme.shapes.extraSmall
    ) {
        Column(
            modifier = Modifier.padding(GameConstants.UI.PADDING_SMALL),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = phaseName,
                color = phaseColor,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                letterSpacing = 2.sp
            )
            Text(
                text = phaseDescription,
                color = Color.Gray,
                fontSize = 9.sp,
                lineHeight = 10.sp
            )
            
            // Mini Stability Bar
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .height(2.dp)
                    .fillMaxWidth(0.5f)
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color.Transparent, phaseColor)
                        )
                    )
            )
        }
    }
}
