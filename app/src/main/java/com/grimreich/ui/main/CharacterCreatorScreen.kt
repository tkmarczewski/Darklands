package com.grimreich.ui.main

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.grimreich.core.Career
import com.grimreich.core.HeroSkill

@Composable
fun CharacterCreatorScreen(
    onStartGame: (String, Career, Map<String, Int>, List<HeroSkill>) -> Unit,
    onBack: () -> Unit,
    viewModel: CharacterCreatorViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var heroName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Reduced header size
        Text(
            text = "KREACJA BOHATERA",
            color = Color(0xFFC0A060),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Progress
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ProgressItem("PROFESJA", active = state.stage == CreatorStage.CAREER)
            ProgressItem("CECHY", active = state.stage == CreatorStage.ATTRIBUTES)
            ProgressItem("SPECJALIZACJE", active = state.stage == CreatorStage.SKILLS)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp), horizontalArrangement = Arrangement.End) {
            Button(
                onClick = { 
                    viewModel.randomizeAll() 
                    heroName = viewModel.randomName()
                },
                modifier = Modifier.height(32.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A1A1A)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray)
            ) {
                Text("LOSUJ WSZYSTKO", fontSize = 9.sp)
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            when (state.stage) {
                CreatorStage.CAREER -> {
                    ProfessionStage(
                        heroName = heroName,
                        onNameChange = { heroName = it },
                        selectedCareer = state.selectedCareer,
                        onSelect = { viewModel.selectCareer(it) }
                    )
                }
                CreatorStage.ATTRIBUTES -> {
                    AttributesStage(
                        attributes = state.attributes,
                        pointsRemaining = state.pointsRemaining,
                        onUpdate = { key, delta -> viewModel.changeAttr(key, delta) }
                    )
                }
                CreatorStage.SKILLS -> {
                    SkillsStage(
                        availableSkills = state.availableSkills,
                        selectedSkills = state.specializedSkills,
                        pointsRemaining = state.specializationPointsRemaining,
                        onToggle = { viewModel.toggleSkill(it) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bottom Navigation
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    if (state.stage == CreatorStage.CAREER) onBack()
                    else viewModel.prevStage()
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
            ) {
                Text("POWRÓT")
            }
            Button(
                onClick = {
                    if (state.stage == CreatorStage.SKILLS) {
                        onStartGame(heroName, state.selectedCareer, state.attributes, state.specializedSkills.toList())
                    } else {
                        viewModel.nextStage()
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = (state.stage != CreatorStage.CAREER || heroName.isNotBlank()),
                colors = ButtonDefaults.buttonColors(containerColor = if (state.stage == CreatorStage.SKILLS) Color(0xFF4A6000) else Color(0xFF2A2A2A))
            ) {
                Text(if (state.stage == CreatorStage.SKILLS) "ZAKOŃCZ" else "DALEJ")
            }
        }
    }
}

@Composable
fun ProgressItem(label: String, active: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(if (active) Color(0xFFC0A060) else Color.DarkGray)
        )
        Text(label, color = if (active) Color.White else Color.Gray, fontSize = 8.sp)
    }
}

@Composable
fun ProfessionStage(
    heroName: String,
    onNameChange: (String) -> Unit,
    selectedCareer: Career,
    onSelect: (Career) -> Unit
) {
    val startingCareers = Career.entries.filter { it.minAge <= 14 }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = heroName,
            onValueChange = onNameChange,
            label = { Text("IMIĘ BOHATERA", color = Color.Gray, fontSize = 10.sp) },
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 14.sp),
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFC0A060),
                unfocusedBorderColor = Color.DarkGray
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("WYBIERZ DROGĘ:", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        
        LazyColumn(
            modifier = Modifier.weight(1f).padding(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(startingCareers) { career ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(career) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedCareer == career) Color(0xFF302010) else Color(0xFF101010)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (selectedCareer == career) Color(0xFFC0A060) else Color.Transparent
                    )
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(career.displayName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(career.description, color = Color.Gray, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun AttributesStage(
    attributes: Map<String, Int>,
    pointsRemaining: Int,
    onUpdate: (String, Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "DOSTĘPNE PUNKTY: $pointsRemaining",
            color = Color.Yellow,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(attributes.toList()) { (key, value) ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(key.uppercase(), color = Color.White, modifier = Modifier.width(80.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { onUpdate(key, -1) }) {
                            Text("-", color = Color.Red, fontSize = 24.sp)
                        }
                        Text(
                            value.toString(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        IconButton(onClick = { onUpdate(key, 1) }) {
                            Text("+", color = Color.Green, fontSize = 24.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SkillsStage(
    availableSkills: List<HeroSkill>,
    selectedSkills: Set<HeroSkill>,
    pointsRemaining: Int,
    onToggle: (HeroSkill) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "PUNKTY SPECJALIZACJI: $pointsRemaining",
            color = Color.Yellow,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            items(availableSkills) { skill ->
                val isSelected = selectedSkills.contains(skill)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggle(skill) },
                    color = if (isSelected) Color(0xFF1A3010) else Color(0xFF111111),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) Color.Green else Color.DarkGray
                    )
                ) {
                    Text(
                        skill.displayName,
                        color = if (isSelected) Color.White else Color.Gray,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}
