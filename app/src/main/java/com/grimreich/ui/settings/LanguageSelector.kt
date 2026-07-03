package com.grimreich.ui.settings

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimreich.core.LanguageManager

@Composable
fun LanguageSelector(
    onDismiss: () -> Unit,
    onLanguageSelected: (LanguageManager.Language) -> Unit
) {
    val currentLanguage = remember { LanguageManager.getSavedLanguage() }
    val languages = remember { LanguageManager.getAvailableLanguages() }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f)),
        onClick = onDismiss,
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "WYBIERZ JĘZYK",
                color = Color(0xFFC0A060),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(languages) { lang ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                LanguageManager.setLanguage(lang)
                                onLanguageSelected(lang)
                            },
                        color = if (lang == currentLanguage)
                            Color(0xFF202020) else Color.Transparent,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (lang == currentLanguage)
                                Color(0xFFC0A060) else Color(0xFF333333)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = lang.displayName,
                                color = if (lang == currentLanguage)
                                    Color(0xFFC0A060) else Color.White,
                                fontSize = 16.sp
                            )
                            if (lang == currentLanguage) {
                                Text(
                                    text = "✓",
                                    color = Color(0xFFC0A060),
                                    fontSize = 18.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A2A2A)),
                shape = MaterialTheme.shapes.extraSmall
            ) {
                Text("ZAMKNIJ", color = Color(0xFFC0A060))
            }
        }
    }
}
