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
    val contentErrors by root.contentErrors.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        content()

        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .zIndex(100f),
            horizontalAlignment = Alignment.End
        ) {
            if (contentErrors.isNotEmpty()) {
                val criticalCount = contentErrors.count { it.severity == com.grimreich.systems.ErrorSeverity.CRITICAL }
                Text(
                    text = "![${contentErrors.size} ERR / $criticalCount CRIT]!",
                    color = if (criticalCount > 0) Color.Red else Color.Yellow,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.8f))
                        .padding(4.dp)
                        .clickable { visible = true }
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            Text(
                text = if (visible) "[X]" else "[DEV]",
                color = if (visible) Color.Red else Color.Gray,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                fontSize = 12.sp,
                modifier = Modifier
                    .background(Color(0xCC000000))
                    .clickable { visible = !visible }
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            )
        }

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

                    if (contentErrors.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = Color.Red.copy(alpha = 0.1f),
                            modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp)
                        ) {
                            androidx.compose.foundation.lazy.LazyColumn(modifier = Modifier.padding(8.dp)) {
                                item { Text("CONTENT_ERRORS_DETECTED:", color = Color.Red, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold) }
                                items(contentErrors.size) { index ->
                                    val error = contentErrors[index]
                                    Text(
                                        text = "> ${error.message}",
                                        color = if (error.severity == com.grimreich.systems.ErrorSeverity.CRITICAL) Color.Red else Color.Yellow,
                                        fontSize = 10.sp,
                                        lineHeight = 12.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DevBtn("VALIDATE") { root.runContentValidation() }
                        DevBtn("HUB") { root.setMode(GameScreenMode.hub); visible = false }
                        DevBtn("MAPA") { root.setMode(GameScreenMode.world_map); visible = false }
                        DevBtn("MIASTO") { root.setMode(GameScreenMode.city); visible = false }
                        DevBtn("QUESTY") { root.setMode(GameScreenMode.quests); visible = false }
                        DevBtn("+500 G") {
                            root.gameRepository.updateState { state ->
                                state.gold += GameConstants.DEV_GOLD_GIFT
                            }
                        }
                        DevBtn("FORCE_SYNC") {
                            root.forceSync()
                        }
                        DevBtn("GIVE HERO") {
                            root.gameRepository.updateState { state ->
                                if (state.party.isEmpty()) {
                                    val hero = root.characterFactory.createHero("DevHero", 20, com.grimreich.core.Career.MERCENARY)
                                    state.party.add(hero)
                                    state.activeHeroId = hero.id
                                }
                            }
                        }
                        DevBtn("TEST FLOW") {
                            root.forceSync()
                            root.gameRepository.updateState { state ->
                                if (state.party.isEmpty()) {
                                    val hero = root.characterFactory.createHero("DevHero", 20, com.grimreich.core.Career.MERCENARY)
                                    state.party.add(hero)
                                    state.activeHeroId = hero.id
                                }
                                state.world.locationId = "wybrzeze_polnocne"
                                state.gold += 1000
                            }
                            root.setMode(GameScreenMode.hub)
                            visible = false
                        }
                        DevBtn("DUMP STATE") {
                            val state = root.gameRepository.currentState()
                            android.util.Log.e("DUMP", "CITY: ${state.world.locationId}")
                            android.util.Log.e("DUMP", "PARTY SIZE: ${state.party.size}")
                            state.party.forEach { android.util.Log.e("DUMP", "  - ${it.name} (${it.id})") }
                            android.util.Log.e("DUMP", "ACTIVE QUESTS: ${state.quest.activeQuestIds}")
                        }
                        DevBtn("TP: NORTH") {
                            root.gameRepository.updateState { state ->
                                state.world.locationId = "wybrzeze_polnocne"
                            }
                            root.setMode(GameScreenMode.city)
                            visible = false
                        }
                        DevBtn("TP: TWIERDZA") {
                            root.gameRepository.updateState { state ->
                                state.world.locationId = "twierdza_zakonu"
                            }
                            root.setMode(GameScreenMode.city)
                            visible = false
                        }
                        DevBtn("TP: SERCE") {
                            root.gameRepository.updateState { state ->
                                state.world.locationId = "serce_krainy"
                            }
                            root.setMode(GameScreenMode.city)
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
                            root.gameRepository.updateState { state ->
                                val item = com.grimreich.grimreich.v1.Item(
                                    instanceId = "potion_hp_" + System.currentTimeMillis(),
                                    templateId = "pot_heal",
                                    name = "Mikstura Zdrowia",
                                    type = "potion",
                                    effects = mapOf("heal" to 20)
                                )
                                state.inventory.add(item)
                            }
                        }
                        DevBtn("+100 XP") {
                            root.gameRepository.updateState { state ->
                                root.experienceSystem.addPartyXpDirect(state, 100)
                            }
                        }
                        DevBtn("ADD_COMPANION") {
                            root.gameRepository.updateState { state ->
                                val companionId = "hero_companion_" + System.currentTimeMillis()
                                val companion = Hero(
                                    id = companionId,
                                    name = "Najemnik",
                                    age = 30,
                                    strength = 15,
                                    agility = 12,
                                    endurance = 14,
                                    perception = 10,
                                    intelligence = 10,
                                    charisma = 10,
                                    piety = 10,
                                    hp = 40,
                                    maxHp = 40,
                                    portraitRes = "port_knight"
                                )
                                state.party.add(companion)
                                if (state.activeHeroId == null) {
                                    state.activeHeroId = companionId
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
                                    state.logEntries.add("DEV: Bohater ${hero.name} poległ.")
                                }
                            }
                            root.setMode(GameScreenMode.ritual)
                            visible = false
                        }
                        DevBtn("RITUAL") {
                            val heroId = root.gameRepository.currentState().activeHeroId
                            if (heroId != null) {
                                root.inspectHero(heroId)
                                root.setMode(GameScreenMode.ritual)
                            }
                            visible = false
                        }
                        DevBtn("+HERBS") {
                            root.gameRepository.updateState { state ->
                                state.inventory.add(com.grimreich.grimreich.v1.Item(instanceId = "ing_herb_1", templateId = "ing_herb", name = "Zioła", type = "ingredient", effects = emptyMap()))
                                state.inventory.add(com.grimreich.grimreich.v1.Item(instanceId = "ing_herb_2", templateId = "ing_herb", name = "Zioła", type = "ingredient", effects = emptyMap()))
                            }
                        }
                        DevBtn("FINAŁ") {
                            root.setMode(GameScreenMode.ending)
                            visible = false
                        }
                        DevBtn("START BLOOD") {
                            root.startQuest("q_blood_icon")
                            visible = false
                        }
                        DevBtn("ADVANCE") {
                            root.gameRepository.updateState { state ->
                                root.gameRepository.questEngine.advanceStepDirect(state, "q_blood_icon")
                            }
                            visible = false
                        }
                        DevBtn("COMPLETE") {
                            root.gameRepository.updateState { state ->
                                root.gameRepository.questEngine.completeQuestDirect(state, "q_blood_icon")
                            }
                            visible = false
                        }
                        DevBtn("MAIN MENU") {
                            root.setMode(GameScreenMode.main_menu)
                            visible = false
                        }
                        DevBtn("QUICK START") {
                            root.finalizeCharacterCreation("Felix Anderson", com.grimreich.core.Career.SCHOLAR, emptyMap(), emptyList())
                            root.gameRepository.updateState { it.gold = 5000 }
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
