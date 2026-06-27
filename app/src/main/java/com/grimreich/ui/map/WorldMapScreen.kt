package com.grimreich.ui.map

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimreich.R
import kotlin.random.Random

@Composable
fun WorldMapScreen(viewModel: WorldMapViewModel, onBack: () -> Unit) {
    val state by viewModel.uiState.collectAsState()
    val allCities = state.allCities

    // GLITCH ANIMATION
    val infiniteTransition = rememberInfiniteTransition(label = "map_glitch")
    val jitterX by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(50, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "jitter"
    )

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // BACKGROUND TINT
        if (state.worldStability < 20) {
            Box(modifier = Modifier.fillMaxSize().background(Color(0x33FF0000)))
        }

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            // HEADER
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = if (state.worldStability < 15 && Random.nextFloat() < 0.2f) "KOD_GEOGRAFICZNY_USZKODZONY" else "MAPA GRIMREICH", 
                    color = if (state.worldStability < 20) Color.Red else Color(0xFFE0C080), 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 20.sp,
                    modifier = if (state.worldStability < 10) Modifier.offset(x = jitterX.dp) else Modifier
                )
                Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A2A2A))) {
                    Text("POWRÓT", color = Color(0xFFE0C080), fontSize = 10.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(allCities) { city ->
                    val qCount = state.cityQuestCounts[city.id] ?: 0
                    val isStabilityLow = state.worldStability < 35
                    
                    val name = if (isStabilityLow && Random.nextFloat() < 0.1f) {
                        "SEKTOR_${city.id.uppercase().take(4)}_${Random.nextInt(100, 999)}"
                    } else city.name

                    MapLocationItem(
                        name = name + if (qCount > 0) " ($qCount ZADANIA)" else "",
                        isCurrent = city.id == state.currentLocationId,
                        isSelected = city.id == state.selectedCityId,
                        stability = state.worldStability,
                        onClick = { viewModel.selectCity(city.id) }
                    )
                }
            }

            // TRAVEL PANEL
            state.selectedCityData?.let { city ->
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    color = Color(0xD0000000),
                    shape = MaterialTheme.shapes.medium,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (state.worldStability < 30) Color.Red else Color.Transparent)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(city.name.uppercase(), color = if (state.worldStability < 20) Color.Red else Color(0xFFE0C080), fontWeight = FontWeight.Bold)
                        Text("DOMENA: ${city.phenomenon}", color = Color.LightGray, fontSize = 12.sp)
                        Text("PATRON: ${city.prophet ?: "Nieznany"}", color = Color.LightGray, fontSize = 12.sp)
                        
                        if (state.worldStability < 15) {
                            Text("!!! OSTRZEŻENIE: KOORDYNATY NIESTABILNE !!!", color = Color.Red, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        if (city.id != state.currentLocationId) {
                            Button(
                                onClick = { viewModel.travelToSelected { onBack() } },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = if (state.worldStability < 20) Color(0xFF600000) else MaterialTheme.colorScheme.primary)
                            ) {
                                Text("WYRUSZ W DROGĘ")
                            }
                        } else {
                            Text("JESTEŚ TUTAJ", color = Color.Gray, modifier = Modifier.align(Alignment.CenterHorizontally))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MapLocationItem(name: String, isCurrent: Boolean, isSelected: Boolean, stability: Int = 100, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        color = when {
            isSelected -> if (stability < 30) Color(0xFF4A0000) else Color(0xFF4A4A2A)
            isCurrent -> if (stability < 30) Color(0xFF003000) else Color(0xFF2A4A2A)
            else -> Color(0xFF1A1A1A)
        },
        shape = MaterialTheme.shapes.extraSmall,
        border = if (isSelected && stability < 25) androidx.compose.foundation.BorderStroke(1.dp, Color.Red) else null
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = name, color = if (isCurrent) Color(0xFFADFF2F) else Color.White, fontWeight = FontWeight.Bold)
            if (isCurrent) {
                Spacer(modifier = Modifier.width(8.dp))
                Text("(OBECNA LOKACJA)", color = Color(0xFFADFF2F), fontSize = 10.sp)
            }
        }
    }
}
