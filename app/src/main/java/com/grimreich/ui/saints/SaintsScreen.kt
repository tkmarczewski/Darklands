package com.grimreich.ui.saints

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimreich.core.GameConstants

@Composable
fun SaintsScreen(viewModel: SaintsViewModel, onExit: () -> Unit) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E))
            .padding(GameConstants.UI.PADDING_MEDIUM)
    ) {
        // HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ŚWIĘCI I KOŚCIÓŁ",
                color = Color(0xFFC8A96E),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = onExit,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A2A2A)),
                shape = MaterialTheme.shapes.extraSmall
            ) {
                Text("POWRÓT", color = Color(0xFFE0C080), fontSize = 10.sp)
            }
        }

        Spacer(modifier = Modifier.height(GameConstants.UI.PADDING_LARGE))

        Row(modifier = Modifier.fillMaxSize()) {
            // LEFT: Status and Actions
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    color = Color(0x40000000),
                    modifier = Modifier.fillMaxWidth().padding(bottom = GameConstants.UI.PADDING_MEDIUM)
                ) {
                    Column(modifier = Modifier.padding(GameConstants.UI.PADDING_SMALL)) {
                        Text("TWOJA DRUŻYNA", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(GameConstants.UI.PADDING_SMALL))
                        Text(text = state.partyStatus, color = Color(0xFFC8A96E), fontSize = 12.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                    }
                }

                Button(
                    onClick = { viewModel.pray() },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3D2B1F)),
                    shape = MaterialTheme.shapes.extraSmall
                ) {
                    Text("MÓDL SIĘ", color = Color(0xFFE0C080), fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(GameConstants.UI.PADDING_SMALL))

                Button(
                    onClick = { viewModel.makeOffering(GameConstants.CHURCH_OFFERING_COST) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A4030)),
                    shape = MaterialTheme.shapes.extraSmall
                ) {
                    Text("ZŁÓŻ OFIARĘ (${GameConstants.CHURCH_OFFERING_COST} zł)", color = Color(0xFFE0C080), fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(GameConstants.UI.PADDING_SMALL))
                
                Button(
                    onClick = { viewModel.cleanse() },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3D2B1F)),
                    shape = MaterialTheme.shapes.extraSmall
                ) {
                    Text("OCZYŚĆ Z MROKU", color = Color(0xFFE0C080), fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.weight(1f))
                
                Button(
                    onClick = onExit,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A2A2A))
                ) {
                    Text("ODEJDŹ SPOD OŁTARZA", color = Color(0xFFE0C080))
                }
            }

            Spacer(modifier = Modifier.width(GameConstants.UI.PADDING_MEDIUM))

            // RIGHT: Saints Catalog and Log
            Column(modifier = Modifier.weight(1.2f)) {
                Surface(
                    color = Color(0x20FFFFFF),
                    modifier = Modifier.fillMaxWidth().weight(1f)
                ) {
                    LazyColumn(modifier = Modifier.padding(GameConstants.UI.PADDING_SMALL)) {
                        item {
                            Text("KATALOG ŚWIĘTYCH", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(GameConstants.UI.PADDING_SMALL))
                            Text(text = state.saintsText, color = Color(0xFFC8A96E), fontSize = 12.sp)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(GameConstants.UI.PADDING_SMALL))
                
                Surface(
                    color = Color(0x60000000),
                    modifier = Modifier.fillMaxWidth().height(60.dp)
                ) {
                    Text(
                        text = state.log,
                        color = Color.White,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}
