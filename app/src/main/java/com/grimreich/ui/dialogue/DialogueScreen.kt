package com.grimreich.ui.dialogue

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.grimreich.systems.DialogueManager
import com.grimreich.grimreich.v1.DialogueNode
import com.grimreich.grimreich.v1.DialogueChoice

@Composable
fun DialogueScreen(viewModel: DialogueViewModel, onExit: () -> Unit) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val node = state.currentNode

    if (node == null) {
        onExit()
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // BACKGROUND
        val bgResId = context.resources.getIdentifier(state.backgroundDrawable, "drawable", context.packageName)
        if (bgResId != 0) {
            Image(
                painter = painterResource(id = bgResId),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        
        Box(modifier = Modifier.fillMaxSize().background(Color(0xD0000000)))

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            // NPC HEADER
            Row(verticalAlignment = Alignment.CenterVertically) {
                val portraitName = DialogueManager.getPortrait(state.npcRole)
                val portResId = context.resources.getIdentifier(portraitName, "drawable", context.packageName)
                
                Surface(
                    modifier = Modifier.size(80.dp),
                    color = Color(0xFF1A1A1A),
                    shape = MaterialTheme.shapes.extraSmall,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0C080))
                ) {
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
                    Text(text = state.npcName, color = Color(0xFFE0C080), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text(text = state.npcRole.uppercase(), color = Color.Gray, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // DIALOGUE TEXT
            Surface(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                color = Color(0x40000000),
                shape = MaterialTheme.shapes.small
            ) {
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    item {
                        Text(
                            text = node.text,
                            color = Color.LightGray,
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Serif
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // CHOICES
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                node.choices.forEach { choice ->
                    Button(
                        onClick = { viewModel.choose(choice) },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A2A2A)),
                        shape = MaterialTheme.shapes.extraSmall
                    ) {
                        Text(text = choice.text, color = Color.White)
                    }
                }
                
                if (node.choices.isEmpty()) {
                    Button(
                        onClick = onExit,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5A1A1A)),
                        shape = MaterialTheme.shapes.extraSmall
                    ) {
                        Text(text = "ODEJDŹ", color = Color.White)
                    }
                }
            }
        }
    }
}
