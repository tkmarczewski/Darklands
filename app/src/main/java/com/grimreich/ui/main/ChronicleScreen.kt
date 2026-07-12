package com.grimreich.ui.main

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.grimreich.R
import com.grimreich.grimreich.v1.ChronicleEntry

@Composable
fun ChronicleScreen(
    onBack: () -> Unit,
    viewModel: ChronicleViewModel = hiltViewModel()
) {
    val entries by viewModel.unlockedEntries.collectAsState()
    var selectedEntry by remember { mutableStateOf<ChronicleEntry?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050505))
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.chronicle_title),
            color = Color(0xFFC0A060),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 8.dp)
        )

        // --- REALITY DECRYPTION PROGRESS (Project Cipher) ---
        // Using entries.size vs hardcoded target for now
        val progress = if (entries.isNotEmpty()) (entries.size.toFloat() / 50f).coerceAtMost(1f) else 0f
        Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            Text(stringResource(R.string.chronicle_decode_prefix) + "${(progress * 100).toInt()}" + stringResource(R.string.chronicle_decode_suffix), color = Color.Gray, fontSize = 10.sp)
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(4.dp),
                color = if (progress >= 1.0f) Color.Yellow else Color(0xFFC0A060),
                trackColor = Color(0xFF111111)
            )
        }

        Row(modifier = Modifier.weight(1f)) {
            // Left: Entry List
            LazyColumn(modifier = Modifier.weight(0.4f)) {
                items(entries) { entry ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { selectedEntry = entry },
                        color = if (selectedEntry?.id == entry.id) Color(0xFF202020) else Color.Transparent,
                        border = if (selectedEntry?.id == entry.id) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC0A060)) else null
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(entry.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text(entry.category.uppercase(), color = Color.Gray, fontSize = 10.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Right: Content Detail
            Surface(
                modifier = Modifier.weight(0.6f).fillMaxHeight(),
                color = Color(0xFF101010),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF222222))
            ) {
                if (selectedEntry != null) {
                    LazyColumn(modifier = Modifier.padding(16.dp)) {
                        item {
                            Text(selectedEntry!!.title, color = Color(0xFFC0A060), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(selectedEntry!!.fullText, color = Color.LightGray, fontSize = 14.sp, lineHeight = 20.sp)
                        }
                    }
                } else {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text(stringResource(R.string.chronicle_select_hint), color = Color.DarkGray, fontSize = 12.sp)
                    }
                }
            }
        }

        Button(
            onClick = {
                selectedEntry = null // Clear state before back
                onBack()
            },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A2A2A))
        ) {
            Text(stringResource(R.string.btn_back), color = Color(0xFFC0A060))
        }
    }
}
