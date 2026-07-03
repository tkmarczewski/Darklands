package com.grimreich.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.grimreich.ui.city.CityScreen
import com.grimreich.ui.city.MarketScreen
import com.grimreich.ui.combat.CombatScreen
import com.grimreich.ui.tavern.TavernScreen
import com.grimreich.ui.tavern.RecruitmentScreen
import com.grimreich.ui.alchemy.AlchemyScreen
import com.grimreich.ui.quests.QuestJournalScreen
import com.grimreich.ui.inventory.InventoryScreen
import com.grimreich.ui.ritual.RitualScreen
import com.grimreich.ui.DevMenuScreen
import com.grimreich.ui.map.WorldMapScreen
import com.grimreich.ui.dialogue.DialogueScreen
import com.grimreich.ui.dialogue.DialogueViewModel
import com.grimreich.ui.alchemy.AlchemyViewModel

sealed class GameRoute(val route: String) {
    object MainMenu : GameRoute("main_menu")
    object PlayerIdentity : GameRoute("player_identity")
    object CharacterCreator : GameRoute("character_creator")
    object Hub : GameRoute("hub")
    object WorldMap : GameRoute("world_map")
    object City : GameRoute("city")
    object Market : GameRoute("market")
    object Alchemy : GameRoute("alchemy")
    object Combat : GameRoute("combat")
    object Tavern : GameRoute("tavern")
    object Dialogue : GameRoute("dialogue")
    object Quests : GameRoute("quests")
    object Recruit : GameRoute("recruit")
    object Inventory : GameRoute("inventory")
    object Chronicle : GameRoute("chronicle")
    object Expedition : GameRoute("expedition")
    object DevMenu : GameRoute("dev_menu")
    object Ritual : GameRoute("ritual")
    object Ending : GameRoute("ending")
    object CharDetail : GameRoute("char_detail")
    // Temple and Events are not yet implemented; they redirect to safe fallbacks.
    object Temple : GameRoute("city")   // fallback: stay in city until TempleScreen lands
    object Events : GameRoute("hub")    // fallback: back to hub until EventsScreen lands
}

@Composable
fun GameNavHost(
    root: GameRootViewModel,
    navController: NavHostController = rememberNavController()
) {
    val mode by root.mode.collectAsState()

    LaunchedEffect(mode) {
        val route = when (mode) {
            GameScreenMode.MAIN_MENU         -> GameRoute.MainMenu.route
            GameScreenMode.PLAYER_IDENTITY   -> GameRoute.PlayerIdentity.route
            GameScreenMode.CHARACTER_CREATOR -> GameRoute.CharacterCreator.route
            GameScreenMode.HUB               -> GameRoute.Hub.route
            GameScreenMode.WORLD_MAP         -> GameRoute.WorldMap.route
            GameScreenMode.CITY              -> GameRoute.City.route
            GameScreenMode.MARKET            -> GameRoute.Market.route
            GameScreenMode.ALCHEMY           -> GameRoute.Alchemy.route
            GameScreenMode.COMBAT            -> GameRoute.Combat.route
            GameScreenMode.TAVERN            -> GameRoute.Tavern.route
            GameScreenMode.DIALOGUE          -> GameRoute.Dialogue.route
            GameScreenMode.QUESTS            -> GameRoute.Quests.route
            GameScreenMode.RECRUIT           -> GameRoute.Recruit.route
            GameScreenMode.INVENTORY         -> GameRoute.Inventory.route
            GameScreenMode.CHRONICLE         -> GameRoute.Chronicle.route
            GameScreenMode.EXPEDITION        -> GameRoute.Expedition.route
            GameScreenMode.DEV_MENU          -> GameRoute.DevMenu.route
            GameScreenMode.RITUAL            -> GameRoute.Ritual.route
            GameScreenMode.ENDING            -> GameRoute.Ending.route
            GameScreenMode.CHAR_DETAIL       -> GameRoute.CharDetail.route
            GameScreenMode.TEMPLE            -> GameRoute.Temple.route   // fallback to city
            GameScreenMode.EVENTS            -> GameRoute.Events.route   // fallback to hub
            else                             -> GameRoute.MainMenu.route
        }
        navController.navigate(route) {
            popUpTo(0)
        }
    }

    NavHost(navController = navController, startDestination = GameRoute.MainMenu.route) {
        composable(GameRoute.MainMenu.route) {
            MainMenuScreen(
                onNewGame = { root.startNewGame() },
                onContinue = { root.restoreSessionIfValid() },
                onExit = { /* exit app */ },
                onDevMenu = { root.setMode(GameScreenMode.DEV_MENU) }
            )
        }

        composable(GameRoute.PlayerIdentity.route) {
            PlayerIdentityScreen(
                onContinue = { name -> root.setPlayerIdentity(name) },
                onBack = { root.setMode(GameScreenMode.MAIN_MENU) },
                viewModel = hiltViewModel()
            )
        }

        composable(GameRoute.CharacterCreator.route) {
            CharacterCreatorScreen(
                onStartGame = { name, career, attrs, skills ->
                    root.finalizeCharacterCreation(name, career, attrs, skills)
                },
                onBack = { root.setMode(GameScreenMode.PLAYER_IDENTITY) },
                viewModel = hiltViewModel()
            )
        }

        composable(GameRoute.Hub.route) {
            HubScreen(
                viewModel = hiltViewModel(),
                onCity = { root.setMode(GameScreenMode.CITY) },
                onMap = { root.setMode(GameScreenMode.WORLD_MAP) },
                onInventory = { root.setMode(GameScreenMode.INVENTORY) },
                onQuests = { root.setMode(GameScreenMode.QUESTS) },
                onWorldLog = { root.setMode(GameScreenMode.CHRONICLE) },
                onCharacter = { root.inspectHero(it) },
                onExpedition = { root.setMode(GameScreenMode.EXPEDITION) },
                onEnding = { root.setMode(GameScreenMode.ENDING) }
            )
        }

        composable(GameRoute.City.route) {
            CityScreen(
                viewModel = hiltViewModel(),
                onMarket = { root.setMode(GameScreenMode.MARKET) },
                onAlchemy = { root.setMode(GameScreenMode.ALCHEMY) },
                onTavern = { root.setMode(GameScreenMode.TAVERN) },
                onTemple = { root.setMode(GameScreenMode.TEMPLE) },
                onRecruit = { root.setMode(GameScreenMode.RECRUIT) },
                onDialogue = { root.setMode(GameScreenMode.DIALOGUE) },
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

        composable(GameRoute.Recruit.route) {
            RecruitmentScreen(
                onBack = { root.setMode(GameScreenMode.TAVERN) },
                viewModel = hiltViewModel()
            )
        }

        composable(GameRoute.Expedition.route) {
            ExpeditionScreen(
                viewModel = hiltViewModel(),
                onBack = { root.setMode(GameScreenMode.HUB) },
                onCombat = { root.setMode(GameScreenMode.COMBAT) }
            )
        }

        composable(GameRoute.Combat.route) {
            CombatScreen(
                viewModel = hiltViewModel(),
                onExit = { root.setMode(GameScreenMode.HUB) }
            )
        }

        composable(GameRoute.Dialogue.route) {
            DialogueScreen(
                viewModel = hiltViewModel<DialogueViewModel>(),
                onExit = { root.setMode(GameScreenMode.CITY) },
                onMarket = { root.setMode(GameScreenMode.MARKET) }
            )
        }

        composable(GameRoute.Quests.route) {
            QuestJournalScreen(
                viewModel = hiltViewModel(),
                onBack = { root.setMode(GameScreenMode.HUB) }
            )
        }

        composable(GameRoute.Inventory.route) {
            InventoryScreen(
                viewModel = hiltViewModel(),
                onBack = { root.setMode(GameScreenMode.HUB) }
            )
        }

        composable(GameRoute.Chronicle.route) {
            ChronicleScreen(
                onBack = { root.setMode(GameScreenMode.HUB) }
            )
        }

        composable(GameRoute.WorldMap.route) {
            WorldMapScreen(
                viewModel = hiltViewModel(),
                onBack = { root.setMode(GameScreenMode.HUB) }
            )
        }

        composable(GameRoute.Market.route) {
            MarketScreen(
                viewModel = hiltViewModel(),
                onBack = { root.setMode(GameScreenMode.CITY) }
            )
        }

        composable(GameRoute.DevMenu.route) {
            DevMenuScreen(
                onBack = { root.setMode(GameScreenMode.HUB) }
            )
        }

        composable(GameRoute.Ritual.route) {
            val ritualVm: com.grimreich.ui.ritual.RitualViewModel = hiltViewModel()
            val hero by ritualVm.deadHero.collectAsState()
            val stability by ritualVm.globalStability.collectAsState()
            hero?.let {
                RitualScreen(
                    hero = it,
                    globalStability = stability,
                    ritualSystem = ritualVm.ritualSystem,
                    onRevived = { root.setMode(GameScreenMode.HUB) },
                    onSacrificed = { root.setMode(GameScreenMode.HUB) }
                )
            }
        }

        composable(GameRoute.Ending.route) {
            EndingScreen(
                viewModel = hiltViewModel(),
                onFinish = { root.setMode(GameScreenMode.MAIN_MENU) }
            )
        }

        composable(GameRoute.Alchemy.route) {
            AlchemyScreen(
                viewModel = hiltViewModel<AlchemyViewModel>(),
                onBack = { root.setMode(GameScreenMode.CITY) }
            )
        }

        composable(GameRoute.CharDetail.route) {
            val hero by root.inspectedHero.collectAsState()
            hero?.let {
                CharDetailScreen(
                    hero = it,
                    onUpgrade = { stat -> root.upgradeStat(it.id, stat) },
                    onBack = { root.setMode(GameScreenMode.HUB) }
                )
            }
        }
    }
}
