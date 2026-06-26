package com.grimreich.ui.alchemy

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grimreich.systems.Recipe
import com.grimreich.core.Hero

@Composable
fun AlchemyScreen(
    viewModel: AlchemyViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0E0A)) // Dark green tint
            .padding(16.dp)
    ) {
        // HEADER
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("KOCIOŁ ALCHEMICZNY", color = Color(0xFFADFF2F), fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.weight(1f)) {
            // LEFT: Recipe List & EXIT
            Column(modifier = Modifier.weight(1f)) {
                Text("RECEPTURY", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.recipes) { recipe ->
                        RecipeItem(recipe, isSelected = state.selectedRecipe?.id == recipe.id) {
                            viewModel.selectRecipe(recipe)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = onBack, 
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF400000)),
                    shape = androidx.compose.material3.MaterialTheme.shapes.extraSmall
                ) {
                    Text("POWRÓT", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // RIGHT: Crafting Bench
            Column(modifier = Modifier.weight(1.2f)) {
                Text("STÓŁ WARSZTATOWY", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                
                Surface(
                    color = Color(0xFF151515),
                    modifier = Modifier.fillMaxSize(),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF222222))
                ) {
                    if (state.selectedRecipe != null) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(state.selectedRecipe!!.id.replace("rec_", "").uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("SKŁADNIKI:", color = Color.Gray, fontSize = 12.sp)
                            state.selectedRecipe!!.ingredients.forEach { (ingId, qty) ->
                                val hasCount = state.inventory.count { it.id == ingId }
                                Text(
                                    "- $ingId: $hasCount / $qty", 
                                    color = if (hasCount >= qty) Color.Green else Color.Red,
                                    fontSize = 12.sp
                                )
                            }
                            
                            Spacer(modifier = Modifier.weight(1f))
                            
                            // Hero Selection
                            Text("KTO WARZY?", color = Color.Gray, fontSize = 10.sp)
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(state.party) { hero ->
                                    HeroChip(hero, isSelected = state.selectedHero?.id == hero.id) {
                                        viewModel.selectHero(hero)
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Button(
                                onClick = { viewModel.craft() },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A4000))
                            ) {
                                Text("WARZ MIKSTURĘ", fontWeight = FontWeight.Bold)
                            }
                            
                            state.statusMessage?.let { 
                                Text(it, color = Color.Yellow, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                            }
                        }
                    } else {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Text("Wybierz recepturę z listy.", color = Color.DarkGray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecipeItem(recipe: Recipe, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        color = if (isSelected) Color(0xFF2A3010) else Color(0xFF111111),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) Color(0xFFADFF2F) else Color(0xFF222222))
    ) {
        Text(
            recipe.id.replace("rec_", " ").replace("_", " ").uppercase(), 
            modifier = Modifier.padding(12.dp),
            color = if (isSelected) Color.White else Color.Gray,
            fontSize = 12.sp
        )
    }
}

@Composable
fun HeroChip(hero: Hero, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        color = if (isSelected) Color(0xFFADFF2F) else Color(0xFF222222),
        shape = MaterialTheme.shapes.extraSmall
    ) {
        Text(
            hero.name.take(1).uppercase(), 
            modifier = Modifier.padding(8.dp),
            color = if (isSelected) Color.Black else Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
