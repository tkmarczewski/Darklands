package com.grimreich.ui.tavern

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimreich.core.GameRepository
import com.grimreich.core.Hero
import com.grimreich.world.ProceduralNpcGenerator
import com.grimreich.core.Career
import com.grimreich.systems.DialogueManager
import java.util.UUID

@Composable
fun RecruitmentScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var gold by remember { mutableStateOf(GameRepository.state.gold) }
    val hireables = remember { GameRepository.state.hireableHeroes }

    // Seed if empty
    LaunchedEffect(Unit) {
        if (hireables.isEmpty()) {
            repeat(4) {
                val name = ProceduralNpcGenerator.generateName()
                val age = 18 + (Math.random() * 40).toInt()
                val career = Career.values().filter { it.minAge <= age }.random()
                
                hireables.add(Hero(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    age = age,
                    currentCareer = career,
                    hp = 25 + (Math.random() * 15).toInt(),
                    maxHp = 40,
                    portraitRes = DialogueManager.getPortrait(career.name)
                ))
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp)
    ) {
        Text(
            text = "REKRUTACJA NAJEMNIKÓW",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFFC0A060),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Text(
            text = "TWOJE ZŁOTO: $gold G",
            color = Color.Green,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp).align(Alignment.CenterHorizontally)
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(hireables) { hero ->
                HireableItem(hero) {
                    if (GameRepository.state.gold >= 100) {
                        if (GameRepository.state.party.size < 4) {
                            GameRepository.state.gold -= 100
                            gold = GameRepository.state.gold
                            hireables.remove(hero)
                            GameRepository.state.party.add(hero)
                            Toast.makeText(context, "${hero.name} dołączył do drużyny!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Drużyna jest pełna!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Brak złota!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF400000))
        ) {
            Text("POWRÓT", color = Color.White)
        }
    }
}

@Composable
private fun HireableItem(hero: Hero, onHire: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = hero.name, color = Color(0xFFC0A060), fontWeight = FontWeight.Bold)
                Text(text = hero.currentCareer?.name ?: "Wędrowiec", color = Color.Gray, fontSize = 12.sp)
                Text(text = "HP: ${hero.hp}/${hero.maxHp}", color = Color.LightGray, fontSize = 10.sp)
            }
            Button(
                onClick = onHire,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A4A4A))
            ) {
                Text("WYNAJMIJ (100 G)", fontSize = 10.sp)
            }
        }
    }
}
