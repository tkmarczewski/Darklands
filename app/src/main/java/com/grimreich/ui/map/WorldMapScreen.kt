package com.grimreich.ui.map

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimreich.R
import com.grimreich.world.CityData

@Composable
fun WorldMapScreen(
    cities: List<CityData>,
    currentCityId: String,
    onCityClick: (String) -> Unit,
    onBack: () -> Unit
) {
    var selectedCity by remember { mutableStateOf(cities.find { it.id == currentCityId }) }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.menu_map),
                    color = Color(0xFFC0A060),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A1A1A)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC0A060)),
                    shape = MaterialTheme.shapes.extraSmall
                ) {
                    Text(stringResource(R.string.btn_back), color = Color(0xFFE0C080), fontSize = 10.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.weight(1f)) {
                // City List
                LazyColumn(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    items(cities) { city ->
                        CityMapCard(
                            city = city,
                            isCurrent = city.id == currentCityId,
                            isSelected = city.id == selectedCity?.id,
                            onClick = { selectedCity = city }
                        )
                    }
                }

                // City Detail / Travel
                Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                    selectedCity?.let { city ->
                        CityMapDetail(
                            city = city,
                            isCurrent = city.id == currentCityId,
                            onTravel = { onCityClick(city.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CityMapDetail(city: CityData, isCurrent: Boolean, onTravel: () -> Unit) {
    Surface(
        color = Color(0xFF111111),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF333333)),
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(city.name.uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(city.loreDescription, color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(vertical = 8.dp))
            
            HorizontalDivider(color = Color(0x33C0A060), modifier = Modifier.padding(vertical = 8.dp))
            
            Text(stringResource(R.string.map_domain_label, city.phenomenon), color = Color.LightGray, fontSize = 12.sp)
            Text(stringResource(R.string.map_patron_label, city.prophet ?: "???"), color = Color.LightGray, fontSize = 12.sp)
            
            Spacer(modifier = Modifier.height(16.dp))
            Text(stringResource(R.string.map_warning_unstable), color = Color.Red, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            
            Spacer(modifier = Modifier.weight(1f))
            
            if (!isCurrent) {
                Button(
                    onClick = onTravel,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC0A060)),
                    shape = MaterialTheme.shapes.extraSmall
                ) {
                    Text(stringResource(R.string.map_btn_travel), color = Color.Black, fontWeight = FontWeight.Bold)
                }
            } else {
                Text(
                    stringResource(R.string.map_label_here), 
                    color = Color.Gray, 
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CityMapCard(city: CityData, isCurrent: Boolean, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        color = if (isSelected) Color(0xFF222222) else Color(0xFF0A0A0A),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) Color(0xFFC0A060) else Color(0xFF222222))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).background(if (isCurrent) Color.Green else Color.DarkGray))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(city.name, color = if (isSelected) Color.White else Color.Gray, fontSize = 14.sp)
                if (isCurrent) {
                    Text(stringResource(R.string.map_label_current_location), color = Color(0xFFADFF2F), fontSize = 10.sp)
                }
            }
        }
    }
}
