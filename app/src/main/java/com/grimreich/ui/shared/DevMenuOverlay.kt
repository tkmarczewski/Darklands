package com.grimreich.ui.shared

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.grimreich.core.GameConstants
import com.grimreich.core.Hero
import com.grimreich.ui.main.GameRootViewModel
import com.grimreich.ui.main.GameScreenMode

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DevMenuOverlay(
    root: GameRootViewModel,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        content()

        Text(
            text = if (visible) "[X]" else "[DEV]",
            color = if (visible) Color.Red else Color.Gray,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
            fontSize = 12.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .zIndex(100f)
                .background(Color(0xCC000000))
                .clickable { visible = !visible }
                .padding(horizontal = 6.dp, vertical = 4.dp)
        )

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopStart).zIndex(99f)
        ) {
            Surface(
                color = Color(0xF0050505),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp, start = 4.dp, end = 4.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("SYSTEM_OVERRIDE_V1", color = Color.Red, fontWeight = FontWeight.Bold, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)

                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DevBtn("HUB") { root.setMode(GameScreenMode.HUB); visible = false }
                        DevBtn("MAPA") { root.setMode(GameScreenMode.WORLD_MAP); visible = false }
                        DevBtn("MIASTO") { root.setMode(GameScreenMode.CITY); visible = false }
                        DevBtn("QUESTY") { root.setMode(GameScreenMode.QUESTS); visible = false }
                        DevBtn("+500 G") {
                            root.gameRepository.updateState { state ->
                                state.gold += GameConstants.DEV_GOLD_GIFT
                            }
                        }
                        DevBtn("TP: TWIERDZA") {
                            root.gameRepository.updateState { state ->
                                state.grimCurrentRegion = "twierdza_zakonu"
                            }
                            root.setMode(GameScreenMode.CITY)
                            visible = false
                        }
                        DevBtn("TP: SERCE") {
                            root.gameRepository.updateState { state ->
                                state.grimCurrentRegion = "serce_krainy"
                            }
                            root.setMode(GameScreenMode.CITY)
                            visible = false
                        }
                        DevBtn("GLITCH ON") {
                            root.gameRepository.updateState { it.world.globalStability = 10 }
                            visible = false
                        }
                        DevBtn("GLITCH OFF") {
                            root.gameRepository.updateState { it.world.globalStability = 100 }
                            visible = false
                        }
                        DevBtn("+POTION") {
                            val s = root.gameRepository.currentState()
                            root.gameRepository.updateState { state ->
                                val item = com.grimreich.grimreich.v1.Item(
                                    id = "potion_hp_" + System.currentTimeMillis(),
                                    name = "Mikstura Zdrowia",
                                    type = "potion",
                                    effects = mapOf("heal" to 20)
                                )
                                state.inventory.add(item)
                            }
                        }
                        DevBtn("+100 XP") {
                            root.gameRepository.updateState { state ->
                                state.party.forEach { hero ->
                                    hero.xp += 100
                                    while (hero.xp >= hero.level * 100) {
                                        hero.xp -= hero.level * 100
                                        hero.level++
                                        hero.attributePoints += 2
                                    }
                                }
                            }
                        }
                        DevBtn("ADD_RALWING") {
                            root.gameRepository.updateState { state ->
                                val ralwingExists = state.party.any { it.id == "hero_ralwing" }
                                if (!ralwingExists) {
                                    val ralwing = Hero(
                                        id = "hero_ralwing",
                                        name = "Ralwing",
                                        age = 40,
                                        strength = 18,
                                        agility = 16,
                                        endurance = 15,
                                        perception = 12,
                                        intelligence = 10,
                                        charisma = 10,
                                        piety = 10,
                                        hp = 50,
                                        maxHp = 50,
                                        portraitRes = "port_knight"
                                    )
                                    state.party.add(ralwing)
                                }
                                if (state.activeHeroId == null) {
                                    state.activeHeroId = "hero_ralwing"
                                }
                            }
                        }
                        DevBtn("WALKA") {
                            root.startDevCombat()
                            visible = false
                        }
                        DevBtn("ZGIŃ") {
                            root.gameRepository.updateState { state ->
                                state.party.find { it.id == state.activeHeroId }?.let { hero ->
                                    hero.hp = 0
                                    hero.isDead = true
                                }
                            }
                            visible = false
                        }
                        DevBtn("RITUAL") {
                            val heroId = root.gameRepository.currentState().party.firstOrNull()?.id
                            if (heroId != null) {
                                root.inspectHero(heroId)
                            }
                            visible = false
                        }
                        DevBtn("+HERBS") {
                            root.gameRepository.updateState { state ->
                                state.inventory.add(com.grimreich.grimreich.v1.Item(id = "ing_herb", name = "Zioła", type = "ingredient", effects = emptyMap()))
                                state.inventory.add(com.grimreich.grimreich.v1.Item(id = "ing_herb", name = "Zioła", type = "ingredient", effects = emptyMap()))
                            }
                        }
                        DevBtn("FINAŁ") {
                            root.setMode(GameScreenMode.ENDING)
                            visible = false
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DevBtn(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF200000)),
        shape = MaterialTheme.shapes.extraSmall,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red),
        modifier = Modifier.height(32.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = text, 
            color = Color.Red, 
            fontSize = 10.sp, 
            fontWeight = FontWeight.Bold,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
        )
    }
}
