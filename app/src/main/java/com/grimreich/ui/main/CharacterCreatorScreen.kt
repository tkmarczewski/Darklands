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
        modifier = Modifier.fillMaxSize().background(Color.Black).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("KREACJA BOHATERA", color = Color(0xFFC0A060), fontSize = 24.sp, fontWeight = FontWeight.Bold)
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Progress Indicator
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            ProgressDot("KARIERA", state.stage == CreatorStage.CAREER)
            ProgressDot("CECHY", state.stage == CreatorStage.ATTRIBUTES)
            ProgressDot("SPECJALIZACJE", state.stage == CreatorStage.SKILLS)
        }

        Spacer(modifier = Modifier.height(24.dp))

        when (state.stage) {
            CreatorStage.CAREER -> {
                CareerSelectionStage(
                    heroName = heroName,
                    onNameChange = { heroName = it },
                    selectedCareer = state.selectedCareer,
                    onCareerSelect = { viewModel.selectCareer(it) }
                )
            }
            CreatorStage.ATTRIBUTES -> {
                AttributeStage(
                    attributes = state.attributes,
                    pointsRemaining = state.pointsRemaining,
                    onAttrChange = { k, d -> viewModel.changeAttr(k, d) }
                )
            }
            CreatorStage.SKILLS -> {
                SkillStage(
                    skills = state.availableSkills,
                    selected = state.specializedSkills,
                    pointsRemaining = state.specializationPointsRemaining,
                    onToggle = { viewModel.toggleSkill(it) }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // NAVIGATION BUTTONS
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { if (state.stage == CreatorStage.CAREER) onBack() else viewModel.prevStage() },
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
                enabled = if (state.stage == CreatorStage.CAREER) heroName.isNotBlank() else true,
                colors = ButtonDefaults.buttonColors(containerColor = if (state.stage == CreatorStage.SKILLS) Color(0xFF4A6000) else Color(0xFF333333))
            ) {
                Text(if (state.stage == CreatorStage.SKILLS) "ZAKOŃCZ" else "DALEJ")
            }
        }
    }
}

@Composable
private fun CareerSelectionStage(
    heroName: String,
    onNameChange: (String) -> Unit,
    selectedCareer: Career,
    onCareerSelect: (Career) -> Unit
) {
    val careers = Career.entries.filter { it.minAge <= 14 }

    Column {
        OutlinedTextField(
            value = heroName,
            onValueChange = onNameChange,
            label = { Text("IMIĘ BOHATERA", color = Color.Gray) },
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFC0A060), unfocusedBorderColor = Color.DarkGray)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("PROFESJA:", color = Color.Gray, fontSize = 12.sp)
        LazyColumn(modifier = Modifier.height(300.dp).padding(vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(careers) { career ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onCareerSelect(career) },
                    colors = CardDefaults.cardColors(containerColor = if (selectedCareer == career) Color(0xFF302010) else Color(0xFF101010)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedCareer == career) Color(0xFFC0A060) else Color.DarkGray)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(career.displayName, color = Color.White, fontWeight = FontWeight.Bold)
                        Text(career.description, color = Color.LightGray, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun AttributeStage(attributes: Map<String, Int>, pointsRemaining: Int, onAttrChange: (String, Int) -> Unit) {
    Column {
        Text("PUNKTY CECH: $pointsRemaining", color = Color.Yellow, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        attributes.forEach { (key, value) ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(key.uppercase(), color = Color.White, modifier = Modifier.width(60.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { onAttrChange(key, -1) }) { Text("-", color = Color.Red) }
                    Text(value.toString(), color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp))
                    IconButton(onClick = { onAttrChange(key, 1) }) { Text("+", color = Color.Green) }
                }
            }
        }
    }
}

@Composable
private fun SkillStage(skills: List<HeroSkill>, selected: Set<HeroSkill>, pointsRemaining: Int, onToggle: (HeroSkill) -> Unit) {
    Column {
        Text("SPECJALIZACJE: $pointsRemaining", color = Color.Yellow, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(modifier = Modifier.height(400.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            items(skills) { skill ->
                val isSelected = selected.contains(skill)
                Surface(
                    modifier = Modifier.fillMaxWidth().clickable { onToggle(skill) },
                    color = if (isSelected) Color(0xFF1A3010) else Color(0xFF111111),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) Color.Green else Color.DarkGray)
                ) {
                    Text(skill.displayName, color = if (isSelected) Color.White else Color.Gray, modifier = Modifier.padding(12.dp))
                }
            }
        }
    }
}

@Composable
private fun ProgressDot(label: String, active: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(10.dp).background(if (active) Color(0xFFC0A060) else Color.DarkGray))
        Text(label, color = if (active) Color.White else Color.Gray, fontSize = 8.sp)
    }
}
