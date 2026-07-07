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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.grimreich.R
import com.grimreich.core.LanguageManager
import androidx.compose.ui.tooling.preview.Preview

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
    var showLanguageSelector by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = stringResource(R.string.main_title),
                color = Color.Red,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = stringResource(R.string.main_subtitle),
                color = Color.Gray.copy(alpha = 0.6f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            MenuButton(stringResource(R.string.btn_new_game), onClick = onNewGame)
            
            MenuButton(
                text = if (state.hasSession) stringResource(R.string.btn_continue_adventure) else stringResource(R.string.btn_no_session),
                enabled = state.hasSession,
                onClick = onContinue
            )

            MenuButton(stringResource(R.string.btn_language), onClick = { showLanguageSelector = true })

            MenuButton(stringResource(R.string.btn_exit), color = Color(0xFF4A0000), onClick = onExit)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = stringResource(R.string.dev_label),
                color = Color.DarkGray,
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.End)
                    .offset(x = (-16).dp)
                    .clickable { onDevMenu() }
            )
        }

        if (showLanguageSelector) {
            com.grimreich.ui.settings.LanguageSelector(
                onDismiss = { showLanguageSelector = false },
                onLanguageSelected = { lang ->
                    LanguageManager.setLanguage(lang)
                    // Trigger Activity Recreate to apply language globally
                    (context as? Activity)?.recreate()
                    showLanguageSelector = false
                }
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

@Preview
@Composable
fun MainMenuScreenPreview() {
    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "GRIMREICH",
                    color = Color.Red,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Text(
                    text = "Do Not Attempt to Adjust The Picture. We Are Controlling Transmission.",
                    color = Color.Gray.copy(alpha = 0.6f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                MenuButton("NOWA PRZYGODA", onClick = {})
                MenuButton("KONTYNUACJA (BRAK SESJI)", enabled = false, onClick = {})
                MenuButton("WYJŚCIE", color = Color(0xFF4A0000), onClick = {})
            }
        }
    }
}
