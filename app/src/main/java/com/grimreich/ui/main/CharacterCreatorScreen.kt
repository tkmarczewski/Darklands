package com.grimreich.ui.main

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimreich.core.Career

@Composable
fun CharacterCreatorScreen(
    onStartGame: (String, Career, Map<String, Int>, List<String>) -> Unit,
    onBack: () -> Unit
) {
    var heroName by remember { mutableStateOf("") }
    var selectedCareer by remember { mutableStateOf<Career?>(null) }
    
    // Starting careers: Page, Apprentice, Squire, Monk, Thief, Guard, Rogue
    val careers = Career.entries.filter { it.minAge <= 14 } 

    Column(
        modifier = Modifier.fillMaxSize().background(Color.Black).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("KREACJA BOHATERA", color = Color(0xFFC0A060), fontSize = 24.sp, fontWeight = FontWeight.Bold)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Hero Name Input
        OutlinedTextField(
            value = heroName,
            onValueChange = { heroName = it },
            label = { Text("IMIĘ BOHATERA", color = Color.Gray) },
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFC0A060),
                unfocusedBorderColor = Color.DarkGray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("WYBIERZ KARIERĘ", color = Color.Gray, fontSize = 12.sp)
        
        // LazyColumn handles scrolling automatically
        LazyColumn(
            modifier = Modifier.weight(1f).padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(careers) { career ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { selectedCareer = career },
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedCareer == career) Color(0xFF302010) else Color(0xFF101010)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedCareer == career) Color(0xFFC0A060) else Color.DarkGray)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(career.displayName, color = Color.White, fontWeight = FontWeight.Bold)
                        Text(career.description, color = Color.LightGray, fontSize = 12.sp)
                    }
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onBack, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)) {
                Text("POWRÓT")
            }
            Button(
                onClick = { selectedCareer?.let { onStartGame(heroName, it, emptyMap(), emptyList()) } },
                modifier = Modifier.weight(1f),
                enabled = heroName.isNotBlank() && selectedCareer != null,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A6000))
            ) {
                Text("ROZPOCZNIJ")
            }
        }
    }
}
