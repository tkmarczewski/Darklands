package com.grimreich.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimreich.R

data class SimpleLogEntry(
    val day: Int,
    val text: String,
    val importance: Int = 1
)

@Composable
fun WorldLogScreen(
    logEntries: List<SimpleLogEntry>,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.log_label_title),
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFFC0A060),
            modifier = Modifier.padding(bottom = 16.dp).align(Alignment.CenterHorizontally)
        )

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (logEntries.isEmpty()) {
                item {
                    Text(
                        stringResource(R.string.log_empty),
                        color = Color.DarkGray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                items(logEntries) { entry ->
                    LogEntryCard(entry)
                }
            }
        }

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().height(50.dp).padding(top = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF400000))
        ) {
            Text(stringResource(R.string.btn_return), color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun LogEntryCard(entry: SimpleLogEntry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0A0A)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF222222))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = stringResource(R.string.log_label_day, entry.day),
                    color = Color(0xFF800000),
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
                if (entry.importance > 1) {
                    Text(
                        text = stringResource(R.string.log_label_important),
                        color = Color(0xFFC0A060),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = entry.text,
                color = Color.LightGray,
                fontSize = 13.sp
            )
        }
    }
}
