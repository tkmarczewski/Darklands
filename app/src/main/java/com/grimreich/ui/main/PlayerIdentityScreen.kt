package com.grimreich.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerIdentityScreen(
    onContinue: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: MainMenuViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black).padding(32.dp), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.widthIn(max = 400.dp)
        ) {
            Text(
                text = "JAK CIĘ ZWĄ, WĘDROWCZE?",
                color = Color(0xFFC0A060),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "To imię należy do Ciebie, nie do bohatera. Będzie ono zapisane w Kronikach Świata.",
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("TWOJE IMIĘ") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFC0A060),
                    unfocusedBorderColor = Color.DarkGray
                ),
                singleLine = true
            )

            Button(
                onClick = { if (name.isNotBlank()) onContinue(name) },
                enabled = name.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A0000)),
                shape = MaterialTheme.shapes.extraSmall
            ) {
                Text("DALEJ", color = Color.White, fontWeight = FontWeight.Bold)
            }

            TextButton(onClick = onBack) {
                Text("POWRÓT", color = Color.Gray)
            }
        }
    }
}
