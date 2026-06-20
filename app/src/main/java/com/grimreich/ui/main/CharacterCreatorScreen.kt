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
    onStartGame: (String, Career, Map<String, Int>, Set<HeroSkill>) -> Unit,
    onBack: () -> Unit,
    viewModel: CharacterCreatorViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var anchorName by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black).padding(16.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "KREATOR KOTWICY",
                color = Color(0xFFC0A060),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                // LEFT: Career & Name
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text("PROFESJA:", color = Color.Gray, fontSize = 12.sp)
                    CareerSelectionGrid(selected = state.selectedCareer, onSelect = { viewModel.selectCareer(it) })
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("IMIĘ KOTWICY:", color = Color.Gray, fontSize = 12.sp)
                    TextField(
                        value = anchorName,
                        onValueChange = { anchorName = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF151515),
                            unfocusedContainerColor = Color(0xFF101010),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Button(onClick = { anchorName = viewModel.randomName() }, modifier = Modifier.fillMaxWidth()) {
                        Text("LOSUJ IMIĘ")
                    }
                }

                // CENTER: Attributes
                Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
                    Text("PUNKTY: ${state.pointsRemaining}", color = Color(0xFFC0A060), fontWeight = FontWeight.Bold)
                    state.attributes.forEach { (key, value) ->
                        AttributeRow(key, value, 
                            onMinus = { viewModel.changeAttr(key, -1) },
                            onPlus = { viewModel.changeAttr(key, 1) }
                        )
                    }
                }

                // RIGHT: Specialized Skills
                Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                    Text("SPECJALIZACJE (${state.specializationPointsRemaining})", color = Color(0xFFC0A060), fontWeight = FontWeight.Bold)
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(state.availableSkills) { skill ->
                            SkillCheckRow(
                                skill = skill,
                                isChecked = state.specializedSkills.contains(skill),
                                onToggle = { viewModel.toggleSkill(skill) }
                            )
                        }
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = { viewModel.randomizeAll(); anchorName = viewModel.randomName() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                ) {
                    Text("AUTO")
                }
                Button(
                    onClick = { onStartGame(anchorName, state.selectedCareer, state.attributes, state.specializedSkills) },
                    enabled = anchorName.isNotBlank() && state.specializationPointsRemaining == 0,
                    modifier = Modifier.weight(2f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A0000))
                ) {
                    Text("ROZPOCZNIJ PRZYGODĘ")
                }
            }
        }
    }
}

@Composable
fun CareerSelectionGrid(selected: Career, onSelect: (Career) -> Unit) {
    val careers = listOf(Career.KNIGHT, Career.ALCHEMIST, Career.GUARD, Career.SCHOLAR)
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        careers.forEach { career ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (selected == career) Color(0xFF300000) else Color(0xFF101010))
                    .border(1.dp, if (selected == career) Color.Red else Color.DarkGray)
                    .clickable { onSelect(career) }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = (selected == career), onClick = { onSelect(career) })
                Text(text = career.name, color = Color.White, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun AttributeRow(name: String, value: Int, onMinus: () -> Unit, onPlus: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Text(name, color = Color.White, modifier = Modifier.width(40.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onMinus, modifier = Modifier.size(24.dp)) { Text("-", color = Color.Red) }
            Text(value.toString(), color = Color.Yellow, modifier = Modifier.padding(horizontal = 8.dp))
            IconButton(onClick = onPlus, modifier = Modifier.size(24.dp)) { Text("+", color = Color.Green) }
        }
    }
}

@Composable
fun SkillCheckRow(skill: HeroSkill, isChecked: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onToggle() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = isChecked, onCheckedChange = { onToggle() })
        Text(skill.displayName, color = Color.LightGray, fontSize = 11.sp)
    }
}
