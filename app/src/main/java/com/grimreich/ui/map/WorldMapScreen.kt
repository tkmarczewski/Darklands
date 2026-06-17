package com.grimreich.ui.map

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimreich.R
import com.grimreich.world.CityCatalogue

@Composable
fun WorldMapScreen(viewModel: WorldMapViewModel, onBack: () -> Unit) {
    val state by viewModel.uiState.collectAsState()
    val allCities = CityCatalogue.all()

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // BACKGROUND MAP
        Image(
            painter = painterResource(id = R.drawable.bg_world_map),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.6f
        )

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            // HEADER
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("MAPA GRIMREICH", color = Color(0xFFE0C080), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A2A2A))) {
                    Text("POWRÓT", color = Color(0xFFE0C080), fontSize = 10.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // LIST OF LOCATIONS (Replacing RelativeLayout for simplicity in Compose first)
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(allCities) { city ->
                    val isCanonical = setOf("wybrzeze_polnocne", "rowniny_koronne", "serce_krainy", "poludniowe_ruiny", "gory_poludniowe", "pogranicze_stepowe", "ziemie_dzikie")
                    val isVisible = isCanonical.contains(city.id) || state.discoveredLocations.contains(city.id)
                    
                    if (isVisible) {
                        MapLocationItem(
                            name = city.name,
                            isCurrent = city.id == state.currentLocationId,
                            isSelected = city.id == state.selectedCityId,
                            onClick = { viewModel.selectCity(city.id) }
                        )
                    }
                }
            }

            // TRAVEL PANEL
            state.selectedCityId?.let { cityId ->
                val city = CityCatalogue.get(cityId)
                if (city != null) {
                    Surface(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        color = Color(0xD0000000),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(city.name.uppercase(), color = Color(0xFFE0C080), fontWeight = FontWeight.Bold)
                            Text("DOMENA: ${city.phenomenon}", color = Color.LightGray, fontSize = 12.sp)
                            Text("PATRON: ${city.prophet ?: "Nieznany"}", color = Color.LightGray, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            if (city.id != state.currentLocationId) {
                                Button(
                                    onClick = { viewModel.travelToSelected { onBack() } },
                                    modifier = Modifier.fillMaxWidth()
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
}

@Composable
fun MapLocationItem(name: String, isCurrent: Boolean, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        color = when {
            isSelected -> Color(0xFF4A4A2A)
            isCurrent -> Color(0xFF2A4A2A)
            else -> Color(0xFF1A1A1A)
        },
        shape = MaterialTheme.shapes.extraSmall
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
