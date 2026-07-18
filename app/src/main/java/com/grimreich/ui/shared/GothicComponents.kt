package com.grimreich.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimreich.core.QuestCategory
import com.grimreich.core.Hero

@Composable
fun GothicButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = Color(0xFF330000)
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(50.dp),
        enabled = enabled,
        shape = RoundedCornerShape(2.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = Color.White,
            disabledContainerColor = Color(0xFF111111)
        )
    ) {
        Text(text.uppercase(), fontWeight = FontWeight.Bold)
    }
}

@Composable
fun GothicCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.border(1.dp, Color(0xFF444444), RoundedCornerShape(4.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111111)),
        shape = RoundedCornerShape(4.dp)
    ) {
        content()
    }
}

@Composable
fun StatRow(label: String, value: String, color: Color = Color.White) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color.Gray, fontSize = 12.sp)
        Spacer(modifier = Modifier.weight(1f))
        Text(value, color = color, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun CategoryBadge(category: QuestCategory) {
    Box(
        modifier = Modifier
            .background(getQuestCategoryColor(category), RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            category.name.uppercase(),
            fontSize = 10.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun BadgeV9(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.2f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color),
        shape = RoundedCornerShape(2.dp)
    ) {
        Text(
            text = text.uppercase(),
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
            fontSize = 8.sp,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun NavTabV9(text: String, onClick: () -> Unit, color: Color = Color(0xFF1A1A1A), enabled: Boolean = true, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth().heightIn(min = 36.dp).clickable(enabled = enabled) { onClick() },
        color = if (enabled) color else Color.Black,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (enabled) Color(0xFFC0A060) else Color(0xFF111111))
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(8.dp)) {
            Text(text = text.uppercase(), color = if (enabled) Color.White else Color.DarkGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun HeroPortraitV9(hero: Hero, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .border(1.dp, if (hero.isDead) Color.Red else Color(0xFFC0A060))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(hero.name.take(1), color = Color.White)
        if (hero.isDead) {
             Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)))
        }
    }
}

fun getQuestCategoryColor(category: QuestCategory): Color {
    return when (category) {
        QuestCategory.combat, QuestCategory.COMBAT -> Color(0xFFD32F2F)
        QuestCategory.social, QuestCategory.SOCIAL -> Color(0xFF1976D2)
        QuestCategory.investigation, QuestCategory.INVESTIGATION -> Color(0xFF7B1FA2)
        QuestCategory.mixed, QuestCategory.MIXED -> Color(0xFF689F38)
        QuestCategory.meta, QuestCategory.META -> Color(0xFFFFD700)
        QuestCategory.anomaly, QuestCategory.ANOMALY -> Color(0xFF00ACC1)
        QuestCategory.drama, QuestCategory.DRAMA -> Color(0xFFF57C00)
        QuestCategory.beast, QuestCategory.BEAST -> Color(0xFF4E342E)
        QuestCategory.intrigue, QuestCategory.INTRIGUE -> Color(0xFF455A64)
        QuestCategory.expedition, QuestCategory.EXPEDITION -> Color(0xFF004D40)
        QuestCategory.dialogue, QuestCategory.DIALOGUE -> Color(0xFF0D47A1)
        QuestCategory.ritual, QuestCategory.RITUAL -> Color(0xFF311B92)
        QuestCategory.bounty, QuestCategory.BOUNTY -> Color(0xFFBF360C)
        else -> Color.Gray
    }
}
