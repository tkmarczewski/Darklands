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
import com.grimreich.ui.effects.glitchEffect

import androidx.compose.ui.res.stringResource
import com.grimreich.R
import com.grimreich.ui.effects.glitchEffect

@Composable
fun EndingScreen(
    viewModel: EndingViewModel,
    onFinish: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(32.dp)
            .glitchEffect(active = state.stability < 10, intensity = 3f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.ending_title),
            color = Color(0xFFC0A060),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = state.summary,
            color = Color.LightGray,
            fontSize = 14.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth().background(Color(0xFF0A0A0A)).padding(16.dp),
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = stringResource(R.string.ending_decision_label),
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        EndingButton(
            stringResource(R.string.ending_btn_ascend), 
            stringResource(R.string.ending_desc_ascend)
        ) {
            viewModel.ascend()
            onFinish()
        }

        Spacer(modifier = Modifier.height(12.dp))

        EndingButton(
            stringResource(R.string.ending_btn_reboot), 
            stringResource(R.string.ending_desc_reboot)
        ) {
            viewModel.reboot()
            onFinish()
        }

        Spacer(modifier = Modifier.height(12.dp))

        EndingButton(
            stringResource(R.string.ending_btn_delete), 
            stringResource(R.string.ending_desc_delete), 
            Color.Red
        ) {
            viewModel.delete()
            onFinish()
        }
    }
}

@Composable
private fun EndingButton(
    title: String,
    description: String,
    titleColor: Color = Color(0xFFC0A060),
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(64.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF151515)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF222222)),
        shape = MaterialTheme.shapes.extraSmall
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, color = titleColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(description, color = Color.Gray, fontSize = 10.sp)
        }
    }
}
