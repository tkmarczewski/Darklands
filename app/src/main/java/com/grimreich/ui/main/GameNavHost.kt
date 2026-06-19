package com.grimreich.ui.main

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.grimreich.ui.city.CityScreen
import com.grimreich.ui.city.CityViewModel
import com.grimreich.ui.combat.CombatScreen
import com.grimreich.ui.combat.CombatViewModel
import com.grimreich.ui.dialogue.DialogueScreen
import com.grimreich.ui.dialogue.DialogueViewModel
import com.grimreich.ui.inventory.InventoryScreen
import com.grimreich.ui.inventory.InventoryViewModel
import com.grimreich.ui.map.WorldMapScreen
import com.grimreich.ui.map.WorldMapViewModel
import com.grimreich.ui.quests.QuestJournalScreen
import com.grimreich.ui.saints.SaintsScreen
import com.grimreich.ui.saints.SaintsViewModel
import com.grimreich.ui.tavern.TavernScreen
import com.grimreich.ui.tavern.TavernViewModel
import com.grimreich.ui.tavern.RecruitmentScreen

sealed class GameRoute(val route: String) {
    object Hub : GameRoute("hub")
    object WorldMap : GameRoute("map")
    object City : GameRoute("city")
    object Combat : GameRoute("combat")
    object Tavern : GameRoute("tavern")
    object Temple : GameRoute("temple")
    object Dialogue : GameRoute("dialogue")
    object Quests : GameRoute("quests")
    object Recruit : GameRoute("recruit")
    object Inventory : GameRoute("inventory")
}

@Composable
fun GameNavHost(
    root: GameRootViewModel,
    navController: NavHostController = rememberNavController()
) {
    val mode by root.mode.collectAsState()

    LaunchedEffect(mode) {
        val target = when (mode) {
            GameScreenMode.HUB -> GameRoute.Hub.route
            GameScreenMode.WORLD_MAP -> GameRoute.WorldMap.route
            GameScreenMode.CITY -> GameRoute.City.route
            GameScreenMode.COMBAT -> GameRoute.Combat.route
            GameScreenMode.TAVERN -> GameRoute.Tavern.route
            GameScreenMode.TEMPLE -> GameRoute.Temple.route
            GameScreenMode.DIALOGUE -> GameRoute.Dialogue.route
            GameScreenMode.QUESTS -> GameRoute.Quests.route
            GameScreenMode.RECRUIT -> GameRoute.Recruit.route
            GameScreenMode.INVENTORY -> GameRoute.Inventory.route
            else -> GameRoute.Hub.route
        }
        if (navController.currentBackStackEntry?.destination?.route != target) {
            navController.navigate(target) {
                popUpTo(GameRoute.Hub.route) { inclusive = false }
            }
        }
    }

    NavHost(navController = navController, startDestination = GameRoute.Hub.route) {
        composable(GameRoute.Hub.route) {
            HubScreen(
                viewModel = hiltViewModel(),
                onMap = { root.setMode(GameScreenMode.WORLD_MAP) },
                onCity = { root.setMode(GameScreenMode.CITY) },
                onInventory = { root.setMode(GameScreenMode.INVENTORY) },
                onQuests = { root.setMode(GameScreenMode.QUESTS) },
                onWorldLog = { /* root.setMode(GameScreenMode.WORLD_LOG) */ },
                onCharacter = { root.inspectHero(it) }
            )
        }
        composable(GameRoute.WorldMap.route) {
            WorldMapScreen(
                viewModel = hiltViewModel(),
                onBack = { root.setMode(GameScreenMode.HUB) }
            )
        }
        composable(GameRoute.City.route) {
            CityScreen(
                viewModel = hiltViewModel(),
                onMarket = { /* TODO */ },
                onTavern = { root.setMode(GameScreenMode.TAVERN) },
                onTemple = { root.setMode(GameScreenMode.TEMPLE) },
                onRecruit = { root.setMode(GameScreenMode.RECRUIT) },
                onDialogue = { root.setMode(GameScreenMode.DIALOGUE) },
                onExit = { root.setMode(GameScreenMode.HUB) }
            )
        }
        composable(GameRoute.Combat.route) {
            CombatScreen(
                viewModel = hiltViewModel(),
                onExit = { root.setMode(GameScreenMode.HUB) }
            )
        }
        composable(GameRoute.Tavern.route) {
            TavernScreen(
                viewModel = hiltViewModel(),
                onHire = { root.setMode(GameScreenMode.RECRUIT) },
                onExit = { root.setMode(GameScreenMode.CITY) }
            )
        }
        composable(GameRoute.Temple.route) {
            SaintsScreen(
                viewModel = hiltViewModel(),
                onExit = { root.setMode(GameScreenMode.CITY) }
            )
        }
        composable(GameRoute.Dialogue.route) {
            DialogueScreen(
                viewModel = hiltViewModel(),
                onExit = { root.setMode(GameScreenMode.CITY) }
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
        composable(GameRoute.Inventory.route) {
            InventoryScreen(
                viewModel = hiltViewModel(),
                onBack = { root.setMode(GameScreenMode.HUB) }
            )
        }
    }
}
