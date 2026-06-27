package com.grimreich.ui.dialogue

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimreich.R
import com.grimreich.grimreich.v1.DialogueNode
import com.grimreich.grimreich.v1.DialogueChoice
import kotlin.random.Random

@Composable
fun DialogueScreen(
    viewModel: DialogueViewModel,
    onExit: () -> Unit,
    onMarket: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    // GLITCH ANIMATION
    val infiniteTransition = rememberInfiniteTransition(label = "glitch")
    val jitterX by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(50, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "jitter"
    )

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // BACKGROUND
        val bgResId = context.resources.getIdentifier(state.backgroundDrawable, "drawable", context.packageName)
        if (bgResId != 0) {
            Image(
                painter = painterResource(id = bgResId),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = if (state.worldStability < 30) 0.15f else 0.3f
            )
        }

        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            // NPC PORTRAIT & NAME
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
                Surface(
                    modifier = Modifier.size(120.dp),
                    color = Color(0xFF101010),
                    border = androidx.compose.foundation.BorderStroke(2.dp, if (state.worldStability < 20) Color.Red else Color(0xFFC0A060))
                ) {
                    val portResId = context.resources.getIdentifier(state.npcPortrait, "drawable", context.packageName)
                    if (portResId != 0) {
                        Image(
                            painter = painterResource(id = portResId),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            alpha = if (state.worldStability < 50 && Random.nextFloat() < 0.1f) 0.5f else 1.0f
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = if (state.worldStability < 35 && Random.nextFloat() < 0.2f) "NULL_PTR_EXCEPTION" else state.npcName.uppercase(),
                        color = if (state.worldStability < 20) Color.Red else Color(0xFFC0A060),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = if (state.worldStability < 15) Modifier.offset(x = jitterX.dp) else Modifier
                    )
                    Text(text = state.npcRole.uppercase(), color = Color.Gray, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // DIALOGUE TEXT
            Surface(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                color = Color(0xCC000000),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF222222))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val rawText = state.currentNode?.text ?: "Cisza... (Sesja utraciła spójność)"
                    val displayedText = if (state.worldStability < 25) {
                        rawText.map { if (Random.nextFloat() < 0.05f) '?' else it }.joinToString("")
                    } else rawText

                    Text(
                        text = displayedText,
                        color = if (state.worldStability < 10) Color.Red else Color.White,
                        fontSize = 16.sp,
                        lineHeight = 24.sp
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    if (state.availableChoices.isEmpty()) {
                        Button(
                            onClick = onExit,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF400000))
                        ) {
                            Text("WYJDŹ (BŁĄD PARADYGMATU)")
                        }
                    }
                    
                    // CHOICES with scrolling
                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.availableChoices) { (choice, isEnabled) ->
                            DialogueChoiceBtn(choice.text, isEnabled, state.worldStability) { 
                                if (isEnabled) {
                                    viewModel.choose(choice)
                                    if (choice.targetNodeId == "end") {
                                        // Logic fix: Only open market if it was an explicit trade choice
                                        if (choice.text.uppercase().contains("HANDLUJ") || choice.text.uppercase().contains("RYNEK")) {
                                            onMarket()
                                        } else {
                                            onExit()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DialogueChoiceBtn(text: String, isEnabled: Boolean = true, stability: Int = 100, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(enabled = isEnabled) { onClick() },
        color = if (isEnabled) Color(0xFF151515) else Color(0xFF0A0A0A),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isEnabled) Color(0xFF333333) else Color(0xFF111111))
    ) {
        val label = if (stability < 40 && !isEnabled) "[USZKODZONE]" else if (isEnabled) "> $text" else "[ZABLOKOWANE] $text"
        
        Text(
            text = label,
            color = if (isEnabled) Color(0xFFE0C080) else Color.DarkGray,
            modifier = Modifier.padding(12.dp),
            fontSize = 14.sp
        )
    }
}
