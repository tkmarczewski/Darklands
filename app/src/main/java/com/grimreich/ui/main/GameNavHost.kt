package com.grimreich.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.grimreich.ui.city.CityScreen
import com.grimreich.ui.city.CityViewModel
import com.grimreich.ui.combat.CombatScreen
import com.grimreich.ui.dialogue.DialogueScreen
import com.grimreich.ui.map.WorldMapScreen
import com.grimreich.ui.tavern.TavernScreen
import com.grimreich.ui.saints.SaintsScreen
import com.grimreich.ui.quests.QuestJournalScreen
import com.grimreich.ui.tavern.RecruitmentScreen

sealed class GameRoute(val route: String) {
    object Hub : GameRoute("hub")
    object WorldMap : GameRoute("world_map")
    object City : GameRoute("city")
    object Combat : GameRoute("combat")
    object Tavern : GameRoute("tavern")
    object Temple : GameRoute("temple")
    object Dialogue : GameRoute("dialogue")
    object Quests : GameRoute("quests")
    object Recruit : GameRoute("recruit")
}

@Composable
fun GameNavHost(
    root: GameRootViewModel,
    navController: NavHostController = rememberNavController()
) {
    val mode by root.mode.collectAsState()

    LaunchedEffect(mode) {
        val route = when (mode) {
            GameScreenMode.HUB -> GameRoute.Hub.route
            GameScreenMode.WORLD_MAP -> GameRoute.WorldMap.route
            GameScreenMode.CITY -> GameRoute.City.route
            GameScreenMode.COMBAT -> GameRoute.Combat.route
            GameScreenMode.TAVERN -> GameRoute.Tavern.route
            GameScreenMode.TEMPLE -> GameRoute.Temple.route
            GameScreenMode.DIALOGUE -> GameRoute.Dialogue.route
            GameScreenMode.QUESTS -> GameRoute.Quests.route
            GameScreenMode.RECRUIT -> GameRoute.Recruit.route
            else -> GameRoute.Hub.route
        }
        navController.navigate(route) {
            launchSingleTop = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = GameRoute.Hub.route
    ) {
        composable(GameRoute.Hub.route) {
            HubScreen(
                viewModel = root.hubVM,
                onCity = { root.setMode(GameScreenMode.CITY) },
                onMap = { root.setMode(GameScreenMode.WORLD_MAP) },
                onInventory = { root.setMode(GameScreenMode.INVENTORY) },
                onQuests = { root.setMode(GameScreenMode.QUESTS) },
                onWorldLog = { root.setMode(GameScreenMode.WORLD_LOG) },
                onCharacter = { heroId -> /* Character detail */ }
            )
        }

        composable(GameRoute.City.route) {
            CityScreen(
                viewModel = root.cityVM,
                onMarket = { /* Market */ },
                onTavern = { root.setMode(GameScreenMode.TAVERN) },
                onTemple = { root.setMode(GameScreenMode.TEMPLE) },
                onRecruit = { root.setMode(GameScreenMode.RECRUIT) },
                onNpcClick = { name, role, node ->
                    root.dialogueVM.init(name, role, node)
                    root.setMode(GameScreenMode.DIALOGUE)
                },
                onExit = { root.setMode(GameScreenMode.HUB) }
            )
        }

        composable(GameRoute.Combat.route) {
            CombatScreen(
                viewModel = root.combatVM,
                onExit = { root.setMode(GameScreenMode.HUB) }
            )
        }

        composable(GameRoute.Tavern.route) {
            TavernScreen(
                viewModel = root.tavernVM,
                onHire = { root.setMode(GameScreenMode.RECRUIT) },
                onExit = { root.setMode(GameScreenMode.CITY) }
            )
        }

        composable(GameRoute.Temple.route) {
            SaintsScreen(
                viewModel = root.saintsVM,
                onExit = { root.setMode(GameScreenMode.CITY) }
            )
        }

        composable(GameRoute.Dialogue.route) {
            DialogueScreen(
                viewModel = root.dialogueVM,
                onExit = { root.setMode(GameScreenMode.CITY) }
            )
        }

        composable(GameRoute.WorldMap.route) {
            WorldMapScreen(
                viewModel = root.worldMapVM,
                onBack = { root.setMode(GameScreenMode.HUB) }
            )
        }

        composable(GameRoute.Quests.route) {
            QuestJournalScreen(
                onBack = { root.setMode(GameScreenMode.HUB) }
            )
        }

        composable(GameRoute.Recruit.route) {
            RecruitmentScreen(
                onBack = { root.setMode(GameScreenMode.CITY) }
            )
        }

        composable(GameScreenMode.INVENTORY.name) {
             Text("Ekran Ekwipunku (TBD)", color = Color.White)
        }
        
        composable(GameScreenMode.WORLD_LOG.name) {
             Text("Ekran Kroniki (TBD)", color = Color.White)
        }
    }
}
