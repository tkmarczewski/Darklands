package com.grimreich.ui.main.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

@Composable
fun ExitConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "PORZUCENIE SESJI",
                color = Color.Red,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "Czy chcesz przerwać obecną sesję i wrócić do menu głównego? Twój stan zostanie automatycznie zapisany w Kronice.",
                color = Color.White
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF400000))
            ) {
                Text("TAK, WYJDŹ", color = Color.White)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
            ) {
                Text("ZOSTAŃ", color = Color.White)
            }
        },
        containerColor = Color(0xFF1A1A1A),
        textContentColor = Color.White,
        titleContentColor = Color.Red
    )
}
