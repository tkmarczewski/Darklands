package com.grimreich.ui.main

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.grimreich.core.GameBootstrapper
import com.grimreich.core.GameState
import com.grimreich.core.Hero
import com.grimreich.core.Career
import com.grimreich.core.HeroSkill
import com.grimreich.grimreich.v1.Item
import com.grimreich.systems.DialogueManager
import com.grimreich.ui.city.CityScreen
import com.grimreich.ui.city.CityViewModel
import com.grimreich.ui.city.MarketScreen
import com.grimreich.ui.city.MarketViewModel
import com.grimreich.ui.combat.CombatScreen
import com.grimreich.ui.combat.CombatViewModel
import com.grimreich.ui.dialogue.DialogueScreen
import com.grimreich.ui.dialogue.DialogueViewModel
import com.grimreich.ui.inventory.InventoryScreen
import com.grimreich.ui.inventory.InventoryViewModel
import com.grimreich.ui.map.WorldMapScreen
import com.grimreich.ui.map.WorldMapViewModel
import com.grimreich.ui.quests.QuestJournalScreen
import com.grimreich.ui.quests.QuestJournalViewModel
import com.grimreich.ui.saints.SaintsScreen
import com.grimreich.ui.saints.SaintsViewModel
import com.grimreich.ui.tavern.TavernScreen
import com.grimreich.ui.tavern.TavernViewModel
import com.grimreich.ui.tavern.RecruitmentScreen
import com.grimreich.ui.DevMenuScreen
import kotlinx.coroutines.launch
import java.util.*

sealed class GameRoute(val route: String) {
    object MainMenu : GameRoute("main_menu")
    object PlayerIdentity : GameRoute("player_identity")
    object CharacterCreator : GameRoute("character_creator")
    object Hub : GameRoute("hub")
    object WorldMap : GameRoute("map")
    object City : GameRoute("city")
    object Market : GameRoute("market")
    object Combat : GameRoute("combat")
    object Tavern : GameRoute("tavern")
    object Temple : GameRoute("temple")
    object Dialogue : GameRoute("dialogue")
    object Quests : GameRoute("quests")
    object Recruit : GameRoute("recruit")
    object Inventory : GameRoute("inventory")
    object CharDetail : GameRoute("char_detail")
    object Expedition : GameRoute("expedition")
    object DevMenu : GameRoute("dev_menu")
}

@Composable
fun GameNavHost(
    root: GameRootViewModel,
    navController: NavHostController = rememberNavController()
) {
    val mode by root.mode.collectAsState()
    val scope = rememberCoroutineScope()
    LaunchedEffect(mode) {
        val target = when (mode) {
            GameScreenMode.MAIN_MENU -> GameRoute.MainMenu.route
            GameScreenMode.PLAYER_IDENTITY -> GameRoute.PlayerIdentity.route
            GameScreenMode.CHARACTER_CREATOR -> GameRoute.CharacterCreator.route
            GameScreenMode.HUB -> GameRoute.Hub.route
            GameScreenMode.WORLD_MAP -> GameRoute.WorldMap.route
            GameScreenMode.CITY -> GameRoute.City.route
            GameScreenMode.MARKET -> GameRoute.Market.route
            GameScreenMode.COMBAT -> GameRoute.Combat.route
            GameScreenMode.TAVERN -> GameRoute.Tavern.route
            GameScreenMode.TEMPLE -> GameRoute.Temple.route
            GameScreenMode.DIALOGUE -> GameRoute.Dialogue.route
            GameScreenMode.QUESTS -> GameRoute.Quests.route
            GameScreenMode.RECRUIT -> GameRoute.Recruit.route
            GameScreenMode.INVENTORY -> GameRoute.Inventory.route
            GameScreenMode.CHAR_DETAIL -> GameRoute.CharDetail.route
            GameScreenMode.EVENTS -> GameRoute.Expedition.route
            GameScreenMode.DEV_MENU -> GameRoute.DevMenu.route
            else -> null
        }
        
        if (target != null && navController.currentBackStackEntry?.destination?.route != target) {
            navController.navigate(target) {
                launchSingleTop = true
                restoreState = true
                if (mode == GameScreenMode.MAIN_MENU || mode == GameScreenMode.HUB) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = (mode == GameScreenMode.MAIN_MENU) }
                }
            }
        }
    }
    NavHost(navController = navController, startDestination = GameRoute.MainMenu.route) {
        composable(GameRoute.MainMenu.route) {
            val context = LocalContext.current
            MainMenuScreen(
                onNewGame = {
                    root.gameRepository.clearSessionAndReset()
                    root.setMode(GameScreenMode.PLAYER_IDENTITY)
                },
                onContinue = {
                    root.restoreSessionIfValid()
                },
                onExit = { (context as? android.app.Activity)?.finish() },
                onDevMenu = { root.setMode(GameScreenMode.DEV_MENU) }
            )
        }
        composable(GameRoute.PlayerIdentity.route) {
            PlayerIdentityScreen(
                onContinue = { name ->
                    root.gameRepository.currentState().playerName = name
                    root.setMode(GameScreenMode.CHARACTER_CREATOR)
                },
                onBack = { root.setMode(GameScreenMode.MAIN_MENU) }
            )
        }
        composable(GameRoute.CharacterCreator.route) {
            CharacterCreatorScreen(
                onStartGame = { name, career, attrs, skills ->
                    scope.launch {
                        val repo = root.gameRepository
                        val existingPlayerName = repo.currentState().playerName
                        
                        root.gameBootstrapper.bootstrapFreshWorld(seed = 1)
                        val state = repo.currentState()
                        state.playerName = existingPlayerName
                        state.heroName = name
                        
                        val hero = Hero(
                            id = "player_hero_${UUID.randomUUID()}",
                            name = name,
                            age = 25,
                            strength = attrs["Str"] ?: 10,
                            agility = attrs["Agi"] ?: 10,
                            perception = attrs["Per"] ?: 10,
                            intelligence = attrs["Int"] ?: 10,
                            endurance = attrs["End"] ?: 10,
                            charisma = attrs["Cha"] ?: 10,
                            piety = attrs["Pie"] ?: 10,
                            hp = (attrs["End"] ?: 10) * 2 + 20,
                            maxHp = (attrs["End"] ?: 10) * 2 + 20,
                            currentCareer = career
                        )
                        skills.forEach { hero.skills[it.displayName] = 30 }
                        
                        state.party.add(hero)
                        state.activeHeroId = hero.id
                        
                        repo.persistCurrentState()
                        root.setMode(GameScreenMode.HUB)
                    }
                },
                onBack = { root.setMode(GameScreenMode.PLAYER_IDENTITY) }
            )
        }
        composable(GameRoute.Hub.route) {
            HubScreen(
                viewModel = hiltViewModel(),
                onMap = { root.setMode(GameScreenMode.WORLD_MAP) },
                onCity = { root.setMode(GameScreenMode.CITY) },
                onInventory = { root.setMode(GameScreenMode.INVENTORY) },
                onQuests = { root.setMode(GameScreenMode.QUESTS) },
                onWorldLog = { /* root.setMode(GameScreenMode.WORLD_LOG) */ },
                onCharacter = { root.inspectHero(it) },
                onExpedition = { root.setMode(GameScreenMode.EVENTS) }
            )
        }
        composable(GameRoute.Expedition.route) {
            ExpeditionScreen(
                viewModel = hiltViewModel(),
                onBack = { root.setMode(GameScreenMode.HUB) },
                onCombat = { quest ->
                    root.combatSystem.startEncounterForQuest(quest.id)
                    root.setMode(GameScreenMode.COMBAT)
                }
            )
        }
        composable(GameRoute.CharDetail.route) {
            val hero by root.inspectedHero.collectAsState()
            hero?.let {
                CharDetailScreen(
                    hero = it,
                    onBack = { root.setMode(GameScreenMode.HUB) }
                )
            } ?: run {
                root.setMode(GameScreenMode.HUB)
            }
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
                onMarket = { root.setMode(GameScreenMode.MARKET) },
                onTavern = { root.setMode(GameScreenMode.TAVERN) },
                onTemple = { root.setMode(GameScreenMode.TEMPLE) },
                onRecruit = { root.setMode(GameScreenMode.RECRUIT) },
                onDialogue = { root.setMode(GameScreenMode.DIALOGUE) },
                onExit = { root.setMode(GameScreenMode.HUB) }
            )
        }
        composable(GameRoute.Market.route) {
            MarketScreen(
                viewModel = hiltViewModel(),
                onBack = { root.setMode(GameScreenMode.CITY) }
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
                onExit = { root.setMode(GameScreenMode.CITY) },
                onMarket = { root.setMode(GameScreenMode.MARKET) }
            )
        }
        composable(GameRoute.Quests.route) {
            val questVm: QuestJournalViewModel = hiltViewModel()
            QuestJournalScreen(
                viewModel = questVm,
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
        composable(GameRoute.DevMenu.route) {
            DevMenuScreen(
                onBack = { root.setMode(GameScreenMode.HUB) }
            )
        }

    }
}
