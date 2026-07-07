package com.grimreich.ui.tavern

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.ui.res.stringResource
import com.grimreich.R

@Composable
fun TavernScreen(viewModel: TavernViewModel, onHire: () -> Unit, onExit: () -> Unit) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A0E08))
            .padding(16.dp)
    ) {
        // HEADER
        Text(
            text = stringResource(R.string.tavern_title),
            color = Color(0xFFE0C080),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxSize()) {
            // LEFT: Actions
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TavernActionButton(stringResource(R.string.tavern_btn_rest), onClick = { viewModel.rest() })
                TavernActionButton(stringResource(R.string.tavern_btn_gossip), onClick = { viewModel.listenToGossip() })
                TavernActionButton(stringResource(R.string.tavern_btn_hire), onClick = onHire)
                
                Spacer(modifier = Modifier.weight(1f))
                
                Button(
                    onClick = onExit,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A2A2A))
                ) {
                    Text(stringResource(R.string.tavern_btn_exit), color = Color(0xFFE0C080))
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // RIGHT: Log and Status
            Column(modifier = Modifier.weight(1.5f)) {
                Surface(
                    color = Color(0x40000000),
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth().weight(1f)
                ) {
                    Text(
                        text = state.log,
                        color = Color.LightGray,
                        fontSize = 14.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = stringResource(R.string.tavern_gold, state.gold),
                    color = Color(0xFFE0C080),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
fun TavernActionButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3D2B1F)),
        shape = MaterialTheme.shapes.extraSmall
    ) {
        Text(text = text, color = Color(0xFFE0C080), fontWeight = FontWeight.Bold)
    }
}
