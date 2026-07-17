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
    object main_menu : GameRoute("main_menu")
    object player_identity : GameRoute("player_identity")
    object character_creator : GameRoute("character_creator")
    object hub : GameRoute("hub")
    object world_map : GameRoute("world_map")
    object city : GameRoute("city")
    object market : GameRoute("market")
    object alchemy : GameRoute("alchemy")
    object combat : GameRoute("combat")
    object tavern : GameRoute("tavern")
    object dialogue : GameRoute("dialogue")
    object quests : GameRoute("quests")
    object recruit : GameRoute("recruit")
    object inventory : GameRoute("inventory")
    object chronicle : GameRoute("chronicle")
    object expedition : GameRoute("expedition")
    object dev_menu : GameRoute("dev_menu")
    object ritual : GameRoute("ritual")
    object ending : GameRoute("ending")
    object char_detail : GameRoute("char_detail")
    object temple : GameRoute("temple")
    object events : GameRoute("events")
}

@Composable
fun GameNavHost(
    root: GameRootViewModel,
    navController: NavHostController = rememberNavController()
) {
    val mode by root.mode.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(mode) {
        val route = when (mode) {
            GameScreenMode.main_menu         -> GameRoute.main_menu.route
            GameScreenMode.player_identity   -> GameRoute.player_identity.route
            GameScreenMode.character_creator -> GameRoute.character_creator.route
            GameScreenMode.hub               -> GameRoute.hub.route
            GameScreenMode.world_map         -> GameRoute.world_map.route
            GameScreenMode.city              -> GameRoute.city.route
            GameScreenMode.market            -> GameRoute.market.route
            GameScreenMode.alchemy           -> GameRoute.alchemy.route
            GameScreenMode.combat            -> GameRoute.combat.route
            GameScreenMode.tavern            -> GameRoute.tavern.route
            GameScreenMode.dialogue          -> GameRoute.dialogue.route
            GameScreenMode.quests            -> GameRoute.quests.route
            GameScreenMode.recruit           -> GameRoute.recruit.route
            GameScreenMode.inventory         -> GameRoute.inventory.route
            GameScreenMode.chronicle         -> GameRoute.chronicle.route
            GameScreenMode.expedition        -> GameRoute.expedition.route
            GameScreenMode.dev_menu          -> GameRoute.dev_menu.route
            GameScreenMode.ritual            -> GameRoute.ritual.route
            GameScreenMode.ending            -> GameRoute.ending.route
            GameScreenMode.char_detail       -> GameRoute.char_detail.route
            GameScreenMode.temple            -> GameRoute.temple.route   // fallback to city
            GameScreenMode.events            -> GameRoute.events.route   // fallback to hub
        }
        navController.navigate(route) {
            launchSingleTop = true
        }
    }

    NavHost(navController = navController, startDestination = GameRoute.main_menu.route) {
        composable(GameRoute.main_menu.route) {
            MainMenuScreen(
                onNewGame = { root.startNewGame() },
                onContinue = { root.restoreSessionIfValid() },
                onExit = { (context as? android.app.Activity)?.finish() },
                onDevMenu = { root.setMode(GameScreenMode.dev_menu) }
            )
        }

        composable(GameRoute.player_identity.route) {
            PlayerIdentityScreen(
                onContinue = { name -> root.setPlayerIdentity(name) },
                onBack = { root.setMode(GameScreenMode.main_menu) },
                viewModel = hiltViewModel()
            )
        }

        composable(GameRoute.character_creator.route) {
            val isRalwing = root.pendingPlayerName?.trim()?.equals("ralwing", ignoreCase = true) == true
            CharacterCreatorScreen(
                onStartGame = { name, career, attrs, skills, cycles ->
                    root.finalizeCharacterCreation(name, career, attrs, skills, cycles)
                },
                onBack = { root.setMode(GameScreenMode.player_identity) },
                initialHeroName = if (isRalwing) "Felix Anderson" else "",
                viewModel = hiltViewModel()
            )
        }

        composable(GameRoute.hub.route) {
            HubScreen(
                viewModel = hiltViewModel(),
                onCity = { root.setMode(GameScreenMode.city) },
                onMap = { root.setMode(GameScreenMode.world_map) },
                onInventory = { root.setMode(GameScreenMode.inventory) },
                onQuests = { root.setMode(GameScreenMode.quests) },
                onWorldLog = { root.setMode(GameScreenMode.chronicle) },
                onCharacter = { root.inspectHero(it) },
                onExpedition = { root.setMode(GameScreenMode.expedition) },
                onEnding = { root.setMode(GameScreenMode.ending) }
            )
        }

        composable(GameRoute.city.route) {
            CityScreen(
                viewModel = hiltViewModel(),
                onMarket = { root.setMode(GameScreenMode.market) },
                onAlchemy = { root.setMode(GameScreenMode.alchemy) },
                onTavern = { root.setMode(GameScreenMode.tavern) },
                onTemple = { root.setMode(GameScreenMode.temple) },
                onRecruit = { root.setMode(GameScreenMode.recruit) },
                onDialogue = { name, role, node -> 
                    root.initiateDialogue(name, role, node)
                },
                onMap = { root.setMode(GameScreenMode.world_map) },
                onInventory = { root.setMode(GameScreenMode.inventory) },
                onChronicle = { root.setMode(GameScreenMode.chronicle) },
                onQuests = { root.setMode(GameScreenMode.quests) },
                onExit = { root.setMode(GameScreenMode.hub) }
            )
        }

        composable(GameRoute.tavern.route) {
            TavernScreen(
                viewModel = hiltViewModel(),
                onHire = { root.setMode(GameScreenMode.recruit) },
                onExit = { root.setMode(GameScreenMode.city) }
            )
        }

        composable(GameRoute.recruit.route) {
            RecruitmentScreen(
                onBack = { root.setMode(GameScreenMode.tavern) },
                viewModel = hiltViewModel()
            )
        }

        composable(GameRoute.expedition.route) {
            ExpeditionScreen(
                viewModel = hiltViewModel(),
                onBack = { root.setMode(GameScreenMode.hub) },
                onCombat = { root.setMode(GameScreenMode.combat) },
                onDialogue = { root.setMode(GameScreenMode.dialogue) },
                onMap = { root.setMode(GameScreenMode.world_map) },
                onInventory = { root.setMode(GameScreenMode.inventory) },
                onChronicle = { root.setMode(GameScreenMode.chronicle) },
                onQuests = { root.setMode(GameScreenMode.quests) }
            )
        }

        composable(GameRoute.combat.route) {
            CombatScreen(
                viewModel = hiltViewModel(),
                onExit = { root.setMode(GameScreenMode.hub) }
            )
        }

        composable(GameRoute.dialogue.route) {
            DialogueScreen(
                viewModel = hiltViewModel<DialogueViewModel>(),
                onExit = { 
                    val state = root.gameRepository.currentState()
                    if (state.isExpeditionActive) {
                        root.setMode(GameScreenMode.expedition)
                    } else if (state.world.locationId.isNotBlank()) {
                        root.setMode(GameScreenMode.city)
                    } else {
                        root.setMode(GameScreenMode.hub)
                    }
                },
                onMarket = { root.setMode(GameScreenMode.market) },
                onCombat = { root.setMode(GameScreenMode.combat) },
                onRitual = { root.setMode(GameScreenMode.ritual) }
            )
        }

        composable(GameRoute.quests.route) {
            QuestJournalScreen(
                viewModel = hiltViewModel(),
                onBack = { root.setMode(GameScreenMode.hub) }
            )
        }

        composable(GameRoute.inventory.route) {
            InventoryScreen(
                viewModel = hiltViewModel(),
                onBack = { root.setMode(GameScreenMode.hub) }
            )
        }

        composable(GameRoute.chronicle.route) {
            ChronicleScreen(
                onBack = { root.setMode(GameScreenMode.hub) }
            )
        }

        composable(GameRoute.world_map.route) {
            WorldMapScreen(
                viewModel = hiltViewModel(),
                onBack = { root.setMode(GameScreenMode.hub) }
            )
        }

        composable(GameRoute.market.route) {
            MarketScreen(
                viewModel = hiltViewModel(),
                onBack = { root.setMode(GameScreenMode.city) }
            )
        }

        composable(GameRoute.temple.route) {
            com.grimreich.ui.city.TempleScreen(
                viewModel = hiltViewModel(),
                onBack = { root.setMode(GameScreenMode.city) }
            )
        }

        composable(GameRoute.dev_menu.route) {
            DevMenuScreen(
                onBack = { root.setMode(GameScreenMode.hub) }
            )
        }

        composable(GameRoute.ritual.route) {
            val ritualVm: com.grimreich.ui.ritual.RitualViewModel = hiltViewModel()
            val hero by ritualVm.deadHero.collectAsState()
            val gold by ritualVm.gold.collectAsState()
            hero?.let {
                RitualScreen(
                    hero = it,
                    gold = gold,
                    ritualSystem = ritualVm.ritualSystem,
                    onRevived = { root.setMode(GameScreenMode.hub) },
                    onSacrificed = { root.setMode(GameScreenMode.hub) }
                )
            }
        }

        composable(GameRoute.ending.route) {
            EndingScreen(
                viewModel = hiltViewModel(),
                root = root,
                onFinish = { root.setMode(GameScreenMode.main_menu) }
            )
        }

        composable(GameRoute.alchemy.route) {
            AlchemyScreen(
                viewModel = hiltViewModel<AlchemyViewModel>(),
                onBack = { root.setMode(GameScreenMode.city) }
            )
        }

        composable(GameRoute.char_detail.route) {
            val hero by root.inspectedHero.collectAsState()
            hero?.let {
                CharDetailScreen(
                    hero = it,
                    onUpgrade = { stat -> root.upgradeStat(it.id, stat) },
                    onRandomize = { root.randomizeAttributes(it.id) },
                    onBack = { root.setMode(GameScreenMode.hub) }
                )
            }
        }
    }
}
