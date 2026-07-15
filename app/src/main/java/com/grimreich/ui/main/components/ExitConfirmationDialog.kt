package com.grimreich.ui.main.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ExitConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "۞ PORZUCENIE PARADYGMATU ۞",
                    color = Color.Red,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
                HorizontalDivider(color = Color.Red, thickness = 1.dp, modifier = Modifier.padding(top = 4.dp))
            }
        },
        text = {
            Text(
                text = "Czy chcesz przerwać obecną sesję i wrócić do menu głównego? Twój stan zostanie automatycznie utrwalony w Kronice Świata. Pamiętaj: każda decyzja o opuszczeniu Boreas echo-rezonuje w Szyfrze.",
                color = Color.LightGray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF400000)),
                shape = androidx.compose.foundation.shape.CutCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red)
            ) {
                Text("WYJDŹ DO MENU", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A1A1A)),
                shape = androidx.compose.foundation.shape.CutCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC0A060))
            ) {
                Text("POZOSTAŃ", color = Color(0xFFC0A060))
            }
        },
        containerColor = Color(0xFF050505),
        textContentColor = Color.White,
        titleContentColor = Color.Red
    )
}
