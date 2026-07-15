package com.grimreich.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grimreich.core.*
import com.grimreich.systems.AudioEngine
import com.grimreich.systems.CombatSystem
import com.grimreich.systems.ContentError
import com.grimreich.systems.ContentValidator
import com.grimreich.systems.EndingSystem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class GameScreenMode {
    MAIN_MENU, PLAYER_IDENTITY, CHARACTER_CREATOR, WORLD_MAP, CITY, COMBAT, TAVERN, TEMPLE, ALCHEMY, EVENTS, HUB, DIALOGUE, INVENTORY, QUESTS, CHRONICLE, RECRUIT, CHAR_DETAIL, MARKET, DEV_MENU, RITUAL, ENDING, EXPEDITION
}

@HiltViewModel
class GameRootViewModel @Inject constructor(
    val gameRepository: GameRepository,
    private val gameBootstrapper: GameBootstrapper,
    private val combatSystem: CombatSystem,
    private val audioEngine: AudioEngine,
    private val contentValidator: ContentValidator,
    private val endingSystem: EndingSystem,
    val experienceSystem: com.grimreich.systems.ExperienceSystem,
    val characterFactory: CharacterFactory
) : ViewModel() {

    private val _mode = MutableStateFlow(GameScreenMode.MAIN_MENU)
    val mode: StateFlow<GameScreenMode> = _mode.asStateFlow()

    private val _ending = MutableStateFlow(EndingSystem.GameEnding.NONE)
    val ending: StateFlow<EndingSystem.GameEnding> = _ending.asStateFlow()

    private val _contentErrors = MutableStateFlow<List<ContentError>>(emptyList())
    val contentErrors: StateFlow<List<ContentError>> = _contentErrors.asStateFlow()

    private val _showExitConfirmation = MutableStateFlow(false)
    val showExitConfirmation: StateFlow<Boolean> = _showExitConfirmation.asStateFlow()

    fun setExitConfirmationVisible(visible: Boolean) {
        _showExitConfirmation.value = visible
    }

    fun confirmExitToMainMenu() {
        _showExitConfirmation.value = false
        saveGame() // Wymuszenie zapisu przed wyjściem
        setMode(GameScreenMode.MAIN_MENU)
    }

    fun runContentValidation() {
        _contentErrors.value = contentValidator.validateAll()
    }

    private val _inspectedHeroId = MutableStateFlow<String?>(null)
    val inspectedHero: StateFlow<Hero?> = combine(gameRepository.gameState, _inspectedHeroId) { state, id ->
        id?.let { state.party.find { h -> h.id == it } }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    private var _pendingPlayerName: String? = null
    val pendingPlayerName: String? get() = _pendingPlayerName

    init {
        audioEngine.playForRoute("main_menu")

        // Ending observer
        gameRepository.gameState
            .map { endingSystem.checkEndingConditions(it) }
            .distinctUntilChanged()
            .onEach { end ->
                if (end != EndingSystem.GameEnding.NONE) {
                    _ending.value = end
                    setMode(GameScreenMode.ENDING)
                }
            }
            .launchIn(viewModelScope)
    }

    fun setMode(mode: GameScreenMode) {
        if (mode == GameScreenMode.MAIN_MENU) {
            gameRepository.persistCurrentState()
        }
        _mode.value = mode
        val route = when (mode) {
            GameScreenMode.MAIN_MENU        -> "main_menu"
            GameScreenMode.PLAYER_IDENTITY,
            GameScreenMode.CHARACTER_CREATOR -> "character_creator"
            GameScreenMode.HUB             -> "hub"
            GameScreenMode.CITY            -> "city"
            GameScreenMode.WORLD_MAP       -> "map"
            GameScreenMode.COMBAT          -> "combat"
            GameScreenMode.TAVERN          -> "tavern"
            GameScreenMode.MARKET          -> "market"
            GameScreenMode.EXPEDITION      -> "expedition"
            GameScreenMode.EVENTS          -> "events"
            GameScreenMode.RITUAL          -> "ritual"
            GameScreenMode.ENDING          -> "ending"
            GameScreenMode.TEMPLE          -> "ritual"
            GameScreenMode.ALCHEMY         -> "alchemy"
            GameScreenMode.DIALOGUE        -> "dialogue"
            GameScreenMode.QUESTS          -> "hub"
            GameScreenMode.CHRONICLE       -> "hub"
            GameScreenMode.RECRUIT         -> "tavern"
            GameScreenMode.INVENTORY       -> "hub"
            GameScreenMode.CHAR_DETAIL     -> "hub"
            GameScreenMode.DEV_MENU        -> "main_menu"
        }
        audioEngine.playForRoute(route)
    }

    fun startNewGame() {
        gameRepository.clearSessionAndReset()
        setMode(GameScreenMode.PLAYER_IDENTITY)
    }

    fun setPlayerIdentity(name: String) {
        _pendingPlayerName = name
        setMode(GameScreenMode.CHARACTER_CREATOR)
    }

    fun finalizeCharacterCreation(name: String, career: Career, attrs: Map<String, Int>, skills: List<HeroSkill>, trainingCycles: Int = 0) {
        viewModelScope.launch {
            gameBootstrapper.bootstrapFreshWorld()

            gameRepository.updateState { state ->
                state.playerName = _pendingPlayerName ?: "Wędrowiec"
                
                // --- ONTOLOGICAL AUDIT: Anchor Sync ---
                state.persistentMeta.anchorIdentity = state.playerName

                val isRalwing = state.playerName?.trim()?.equals("ralwing", ignoreCase = true) == true || 
                                name.trim().equals("ralwing", ignoreCase = true)
                
                val finalHeroName = if (isRalwing) "Felix Anderson" else name
                state.heroName = finalHeroName

                val hero = if (isRalwing) {
                    // FORTUNA FELIXA
                    state.gold = 3000
                    
                    Hero(
                        id = "hero_main",
                        name = "Felix Anderson",
                        age = 35,
                        currentCareer = Career.SCHOLAR,
                        strength = 8,
                        agility = 10,
                        perception = 18,
                        intelligence = 20,
                        endurance = 10,
                        charisma = 18,
                        piety = 15,
                        hp = 35,
                        maxHp = 35
                    ).apply {
                        this.skills.put("Alchemia", 60)
                        this.skills.put("Czytanie i Pisanie", 80)
                        this.skills.put("Religia", 50)
                    }
                } else {
                    val baseAge = career.minAge
                    characterFactory.createHero(name, baseAge, career, trainingCycles).copy(id = "hero_main").apply {
                        // Override attributes with user's distribution if not ralwing
                        this.strength = attrs["Str"] ?: this.strength
                        this.agility = attrs["Agi"] ?: this.agility
                        this.perception = attrs["Per"] ?: this.perception
                        this.intelligence = attrs["Int"] ?: this.intelligence
                        this.endurance = attrs["End"] ?: this.endurance
                        this.charisma = attrs["Cha"] ?: this.charisma
                        this.piety = attrs["Pie"] ?: this.piety
                        
                        // Add specialized skills with high value
                        skills.forEach { this.skills.put(it.displayName, 45) }
                    }
                }

                state.party.add(hero)
                state.activeHeroId = hero.id
                
                // --- KLIMATYCZNY START SESJI ---
                state.logEntries.add("Niech na świecie zapanuje pokój... choćby na tę jedną chwilę.")
                
                if (isRalwing) {
                    state.logEntries.add("[SYSTEM] Paradygmat Ralwing aktywowany. Witaj, Felixie.")
                }
            }

            gameRepository.persistCurrentState()
            setMode(GameScreenMode.HUB)
        }
    }

    fun restoreSessionIfValid() {
        viewModelScope.launch {
            if (gameRepository.restoreIfAvailable()) {
                setMode(GameScreenMode.HUB)
            }
        }
    }

    fun inspectHero(heroId: String) {
        val hero = gameRepository.currentState().party.find { it.id == heroId }
        _inspectedHeroId.value = heroId
        if (hero != null && hero.isDead) {
            setMode(GameScreenMode.RITUAL)
        } else if (hero != null) {
            setMode(GameScreenMode.CHAR_DETAIL)
        }
    }

    fun upgradeStat(heroId: String, stat: String) {
        gameRepository.updateState { state ->
            val hero = state.party.find { it.id == heroId } ?: return@updateState
            if (hero.attributePoints > 0) {
                val normalizedStat = stat.uppercase()
                var applied = true
                when (normalizedStat) {
                    "STRENGTH", "SIŁA", "STR"           -> hero.strength++
                    "AGILITY", "ZRĘCZNOŚĆ", "AGI"       -> hero.agility++
                    "INTELLIGENCE", "INTELIGENCJA", "INT" -> hero.intelligence++
                    "ENDURANCE", "WYTRZYMAŁOŚĆ", "END"   -> hero.endurance++
                    "PERCEPTION", "PERCEPCJA", "PER"     -> hero.perception++
                    "CHARISMA", "CHARYZMA", "CHA"        -> hero.charisma++
                    "PIETY", "POBOŻNOŚĆ", "PIE"          -> hero.piety++
                    else -> applied = false
                }
                if (applied) {
                    hero.attributePoints--
                    hero.normalize()
                    state.logEntries.add("${hero.name} rozwija swoją naturę: $stat +1.")
                }
            }
        }
    }

    fun randomizeAttributes(heroId: String) {
        gameRepository.updateState { state ->
            val hero = state.party.find { it.id == heroId } ?: return@updateState
            val points = hero.attributePoints
            if (points > 0) {
                val stats = listOf("STR", "AGI", "INT", "END", "PER", "CHA", "PIE")
                repeat(points) {
                    val target = stats.random()
                    when (target) {
                        "STR" -> hero.strength++
                        "AGI" -> hero.agility++
                        "INT" -> hero.intelligence++
                        "END" -> hero.endurance++
                        "PER" -> hero.perception++
                        "CHA" -> hero.charisma++
                        "PIE" -> hero.piety++
                    }
                }
                hero.attributePoints = 0
                hero.normalize()
                state.logEntries.add("${hero.name} poddaje się przeznaczeniu (losowy rozwój).")
            }
        }
    }

    fun saveGame() {
        gameRepository.persistCurrentState()
    }

    fun forceSync() {
        viewModelScope.launch {
            gameRepository.sync()
        }
    }

    fun manualSave(slotId: Int, label: String = "") {
        gameRepository.manualSave(slotId, label)
    }

    fun startDevCombat() {
        val enemy = Bestiary.get(EnemyType.BANDIT)
        combatSystem.startCombat(enemy)
        setMode(GameScreenMode.COMBAT)
    }
}
