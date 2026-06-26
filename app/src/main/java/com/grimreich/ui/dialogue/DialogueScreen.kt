package com.grimreich.ui.dialogue

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

@Composable
fun DialogueScreen(
    viewModel: DialogueViewModel,
    onExit: () -> Unit,
    onMarket: () -> Unit // Transition to Market if requested
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // BACKGROUND
        val bgResId = context.resources.getIdentifier(state.backgroundDrawable, "drawable", context.packageName)
        if (bgResId != 0) {
            Image(
                painter = painterResource(id = bgResId),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.3f
            )
        }

        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            // NPC PORTRAIT & NAME
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
                Surface(
                    modifier = Modifier.size(120.dp),
                    color = Color(0xFF101010),
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFC0A060))
                ) {
                    val portResId = context.resources.getIdentifier(state.npcPortrait, "drawable", context.packageName)
                    if (portResId != 0) {
                        Image(
                            painter = painterResource(id = portResId),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(text = state.npcName.uppercase(), color = Color(0xFFC0A060), fontWeight = FontWeight.Bold, fontSize = 20.sp)
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
                    Text(
                        text = state.currentNode?.text ?: "Cisza... (Sesja utraciła spójność)",
                        color = Color.White,
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
                            DialogueChoiceBtn(choice.text, isEnabled) { 
                                if (isEnabled) {
                                    viewModel.choose(choice)
                                    if (choice.targetNodeId == "end") {
                                        if (state.npcRole.lowercase().contains("kupiec") || state.npcRole.lowercase().contains("merchant")) {
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
private fun DialogueChoiceBtn(text: String, isEnabled: Boolean = true, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(enabled = isEnabled) { onClick() },
        color = if (isEnabled) Color(0xFF151515) else Color(0xFF0A0A0A),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isEnabled) Color(0xFF333333) else Color(0xFF111111))
    ) {
        Text(
            text = if (isEnabled) "> $text" else "[ZABLOKOWANE] $text",
            color = if (isEnabled) Color(0xFFE0C080) else Color.DarkGray,
            modifier = Modifier.padding(12.dp),
            fontSize = 14.sp
        )
    }
}
