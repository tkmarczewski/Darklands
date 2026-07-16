package com.grimreich.ui.saints

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimreich.R
import com.grimreich.core.GameConstants
import com.grimreich.core.Saint
import com.grimreich.core.Hero

@Composable
fun SaintsScreen(
    saints: List<Saint>,
    party: List<Hero>,
    onPray: (String, String) -> Unit,
    onOffer: (String, String) -> Unit,
    onCleanse: (String, String) -> Unit,
    onBack: () -> Unit
) {
    var selectedSaint by remember { mutableStateOf(saints.firstOrNull()) }
    var selectedHero by remember { mutableStateOf(party.firstOrNull()) }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.menu_saints),
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
                // Saint List
                LazyColumn(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    item { Text(stringResource(R.string.menu_saints), color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                    items(saints) { saint ->
                        SaintCard(
                            saint = saint,
                            isSelected = saint.id == selectedSaint?.id,
                            onClick = { selectedSaint = saint }
                        )
                    }
                }

                // Interaction Area
                Column(modifier = Modifier.weight(1.2f).padding(start = 8.dp)) {
                    Text(stringResource(R.string.hub_party), color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Hero selector for blessing
                    party.forEach { hero ->
                        HeroSmallCard(
                            hero = hero,
                            isSelected = hero.id == selectedHero?.id,
                            onClick = { selectedHero = hero }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    selectedSaint?.let { saint ->
                        selectedHero?.let { hero ->
                            InteractionButtons(
                                saint = saint,
                                hero = hero,
                                onPray = { onPray(hero.id, saint.id) },
                                onOffer = { onOffer(hero.id, saint.id) },
                                onCleanse = { onCleanse(hero.id, saint.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InteractionButtons(
    saint: Saint,
    hero: Hero,
    onPray: () -> Unit,
    onOffer: () -> Unit,
    onCleanse: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(
            onClick = onPray,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF202020)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC0A060)),
            shape = MaterialTheme.shapes.extraSmall
        ) {
            Text(stringResource(R.string.saints_btn_pray), color = Color(0xFFE0C080), fontWeight = FontWeight.Bold)
        }

        Button(
            onClick = onOffer,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF202020)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC0A060)),
            shape = MaterialTheme.shapes.extraSmall
        ) {
            Text(stringResource(R.string.saints_btn_offer, GameConstants.CHURCH_OFFERING_COST), color = Color(0xFFE0C080), fontWeight = FontWeight.Bold)
        }

        if (hero.corruption > 0) {
            Button(
                onClick = onCleanse,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF202020)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC0A060)),
                shape = MaterialTheme.shapes.extraSmall
            ) {
                Text(stringResource(R.string.saints_btn_cleanse), color = Color(0xFFE0C080), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SaintCard(saint: Saint, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        color = if (isSelected) Color(0xFF222222) else Color(0xFF0A0A0A),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) Color(0xFFC0A060) else Color(0xFF222222))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(saint.name, color = if (isSelected) Color.White else Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(saint.domain, color = Color.DarkGray, fontSize = 10.sp)
        }
    }
}

@Composable
fun HeroSmallCard(hero: Hero, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .clickable { onClick() },
        color = if (isSelected) Color(0xFF333333) else Color(0xFF111111),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) Color.Yellow else Color.Transparent)
    ) {
        Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(hero.name, color = Color.White, fontSize = 12.sp)
            Text("PIE: ${hero.piety}", color = Color.Gray, fontSize = 10.sp)
        }
    }
}
