package com.grimreich.ui.main

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun MainMenuScreen(
    onNewGame: () -> Unit,
    onContinue: () -> Unit,
    onExit: () -> Unit,
    onDevMenu: () -> Unit,
    viewModel: MainMenuViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = "GRIMREICH",
                color = Color.Red,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            MenuButton("NOWA PRZYGODA", onClick = onNewGame)
            
            MenuButton(
                text = if (state.hasSession) "KONTYNUUJ PRZYGODĘ" else "KONTYNUACJA (BRAK SESJI)",
                enabled = state.hasSession,
                onClick = onContinue
            )

            MenuButton("WYJŚCIE", color = Color(0xFF4A0000), onClick = onExit)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "DEV",
                color = Color.DarkGray,
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.End)
                    .offset(x = (-16).dp)
                    .clickable { onDevMenu() }
            )
        }
    }
}

@Composable
fun MenuButton(text: String, enabled: Boolean = true, color: Color = Color(0xFF1A1A1A), onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.width(280.dp).height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            disabledContainerColor = Color(0xFF0F0F0F)
        ),
        shape = MaterialTheme.shapes.extraSmall,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF333333))
    ) {
        Text(
            text = text,
            color = if (enabled) Color(0xFFE0C080) else Color.DarkGray,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
