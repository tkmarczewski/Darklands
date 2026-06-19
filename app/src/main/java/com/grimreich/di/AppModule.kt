package com.grimreich.di

import android.content.Context
import com.grimreich.core.*
import com.grimreich.systems.*
import com.grimreich.ui.main.GrimMapActions
import com.grimreich.world.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideStatePersistenceManager(@ApplicationContext context: Context): StatePersistenceManager {
        return StatePersistenceManager(context)
    }

    @Provides
    @Singleton
    fun provideQuestSystem(gameRepository: GameRepository, cityCatalogue: CityCatalogue): QuestSystem {
        return QuestSystem(gameRepository, cityCatalogue)
    }

    @Provides
    @Singleton
    fun provideQuestJournalSystem(questSystem: QuestSystem): QuestJournalSystem {
        return QuestJournalSystem(questSystem)
    }

    @Provides
    @Singleton
    fun provideGrimholdSliceSystem(questSystem: QuestSystem): GrimholdSliceSystem {
        return GrimholdSliceSystem(questSystem)
    }

    @Provides
    @Singleton
    fun provideRegionalSliceSystem(questSystem: QuestSystem): RegionalSliceSystem {
        return RegionalSliceSystem(questSystem)
    }

    @Provides
    @Singleton
    fun provideVisualContentSystem(questSystem: QuestSystem): VisualContentSystem {
        return VisualContentSystem(questSystem)
    }

    @Provides
    @Singleton
    fun provideDialogueManager(questSystem: QuestSystem): DialogueManager {
        return DialogueManager(questSystem)
    }

    @Provides
    @Singleton
    fun provideReligionSystem(gameRepository: GameRepository): ReligionSystem {
        return ReligionSystem(gameRepository)
    }

    @Provides
    @Singleton
    fun provideLootSystem(gameRepository: GameRepository, itemCatalogue: ItemCatalogue): LootSystem {
        return LootSystem(gameRepository, itemCatalogue)
    }

    @Provides
    @Singleton
    fun provideEncounterSystem(lootSystem: LootSystem): EncounterSystem {
        return EncounterSystem(lootSystem)
    }

    @Provides
    @Singleton
    fun provideSkillSystem(gameRepository: GameRepository): SkillSystem {
        return SkillSystem(gameRepository)
    }

    @Provides
    @Singleton
    fun provideExperienceSystem(gameRepository: GameRepository): ExperienceSystem {
        return ExperienceSystem(gameRepository)
    }

    @Provides
    @Singleton
    fun provideInjurySystem(gameRepository: GameRepository): InjurySystem {
        return InjurySystem(gameRepository)
    }

    @Provides
    @Singleton
    fun provideEchoSystem(gameRepository: GameRepository): EchoSystem {
        return EchoSystem(gameRepository)
    }

    @Provides
    @Singleton
    fun provideAdvancedTactics(gameRepository: GameRepository): AdvancedTactics {
        return AdvancedTactics(gameRepository)
    }

    @Provides
    @Singleton
    fun provideAgingSystem(gameRepository: GameRepository): AgingSystem {
        return AgingSystem(gameRepository)
    }

    @Provides
    @Singleton
    fun provideAlchemySystem(gameRepository: GameRepository, itemCatalogue: ItemCatalogue): AlchemySystem {
        return AlchemySystem(gameRepository, itemCatalogue)
    }

    @Provides
    @Singleton
    fun provideCareerChain(): CareerChain {
        return CareerChain()
    }

    @Provides
    @Singleton
    fun provideCharacterFactory(careerChain: CareerChain, agingSystem: AgingSystem): CharacterFactory {
        return CharacterFactory(careerChain, agingSystem)
    }

    @Provides
    @Singleton
    fun provideChronicleSystem(gameRepository: GameRepository): ChronicleSystem {
        return ChronicleSystem(gameRepository)
    }

    @Provides
    @Singleton
    fun provideCollapseAI2_0(gameRepository: GameRepository, chronicleSystem: ChronicleSystem): CollapseAI2_0 {
        return CollapseAI2_0(gameRepository, chronicleSystem)
    }

    @Provides
    @Singleton
    fun provideHistoryEngine(gameRepository: GameRepository, chronicleSystem: ChronicleSystem): HistoryEngine {
        return HistoryEngine(gameRepository, chronicleSystem)
    }

    @Provides
    @Singleton
    fun provideMutationEngine(gameRepository: GameRepository, chronicleSystem: ChronicleSystem): MutationEngine {
        return MutationEngine(gameRepository, chronicleSystem)
    }

    @Provides
    @Singleton
    fun providePhenomenaEngine(gameRepository: GameRepository, chronicleSystem: ChronicleSystem): PhenomenaEngine {
        return PhenomenaEngine(gameRepository, chronicleSystem)
    }

    @Provides
    @Singleton
    fun provideAbsoluteSystem(gameRepository: GameRepository, chronicleSystem: ChronicleSystem): AbsoluteSystem {
        return AbsoluteSystem(gameRepository, chronicleSystem)
    }

    @Provides
    @Singleton
    fun provideWorldMap(): WorldMap {
        return WorldMap()
    }

    @Provides
    @Singleton
    fun provideCityCatalogue(): CityCatalogue {
        return CityCatalogue()
    }

    @Provides
    @Singleton
    fun provideItemCatalogue(): ItemCatalogue {
        return ItemCatalogue()
    }

    @Provides
    @Singleton
    fun provideGrimMapActions(gameRepository: GameRepository): GrimMapActions {
        return GrimMapActions(gameRepository)
    }

    @Provides
    @Singleton
    fun provideExplorationSystem(worldMap: WorldMap): ExplorationSystem {
        return ExplorationSystem(worldMap)
    }

    @Provides
    @Singleton
    fun provideCityEventSystem(cityCatalogue: CityCatalogue): CityEventSystem {
        return CityEventSystem(cityCatalogue)
    }

    @Provides
    @Singleton
    fun provideDemoShellSystem(cityCatalogue: CityCatalogue): DemoShellSystem {
        return DemoShellSystem(cityCatalogue)
    }

    @Provides
    @Singleton
    fun provideExpandedContentSeeder(cityCatalogue: CityCatalogue): ExpandedContentSeeder {
        return ExpandedContentSeeder(cityCatalogue)
    }

    @Provides
    @Singleton
    fun provideProceduralNpcGenerator(cityCatalogue: CityCatalogue): ProceduralNpcGenerator {
        return ProceduralNpcGenerator(cityCatalogue)
    }

    @Provides
    @Singleton
    fun provideMutacjeNPC2_0(): MutacjeNPC2_0 {
        return MutacjeNPC2_0()
    }

    @Provides
    @Singleton
    fun provideMutacjePotworow2_0(): MutacjePotworow2_0 {
        return MutacjePotworow2_0()
    }

    @Provides
    @Singleton
    fun provideMutacjeRegionow2_0(): MutacjeRegionow2_0 {
        return MutacjeRegionow2_0()
    }

    @Provides
    @Singleton
    fun provideTriLayerBoss2_0(): TriLayerBoss2_0 {
        return TriLayerBoss2_0()
    }

    @Provides
    @Singleton
    fun provideTriLayerMonster2_0(): TriLayerMonster2_0 {
        return TriLayerMonster2_0()
    }

    @Provides
    @Singleton
    fun provideGameRepository(
        questSystem: QuestSystem,
        dialogueManager: DialogueManager,
        persistence: StatePersistenceManager,
        cityCatalogue: CityCatalogue,
        itemCatalogue: ItemCatalogue
    ): GameRepository {
        return GameRepository(questSystem, dialogueManager, persistence, cityCatalogue, itemCatalogue)
    }

    @Provides
    @Singleton
    fun provideReputationSystem(gameRepository: GameRepository): ReputationSystem {
        return ReputationSystem(gameRepository)
    }

    @Provides
    @Singleton
    fun providePartyRepository(gameRepository: GameRepository): PartyRepository {
        return PartyRepository(gameRepository)
    }

    @Provides
    @Singleton
    fun provideInventorySystem(gameRepository: GameRepository, partyRepository: PartyRepository): InventorySystem {
        return InventorySystem(gameRepository, partyRepository)
    }

    @Provides
    @Singleton
    fun provideMoraleSystem(): MoraleSystem {
        return MoraleSystem()
    }

    @Provides
    @Singleton
    fun provideCombatRound(moraleSystem: MoraleSystem): CombatRound {
        return CombatRound(moraleSystem)
    }

    @Provides
    @Singleton
    fun provideCombatSystem(
        gameRepository: GameRepository,
        partyRepository: PartyRepository,
        inventorySystem: InventorySystem,
        moraleSystem: MoraleSystem,
        combatRound: CombatRound
    ): CombatSystem {
        return CombatSystem(gameRepository, partyRepository, inventorySystem, moraleSystem, combatRound)
    }

    @Provides
    @Singleton
    fun provideEconomySystem(
        gameRepository: GameRepository,
        reputationSystem: ReputationSystem,
        cityCatalogue: CityCatalogue
    ): EconomySystem {
        return EconomySystem(gameRepository, reputationSystem, cityCatalogue)
    }

    @Provides
    @Singleton
    fun provideChurchSystem(gameRepository: GameRepository): ChurchSystem {
        return ChurchSystem(gameRepository)
    }

    @Provides
    @Singleton
    fun provideStabilitySystem(gameRepository: GameRepository): StabilitySystem {
        return StabilitySystem(gameRepository)
    }

    @Provides
    @Singleton
    fun provideSocialEventSystem(gameRepository: GameRepository): SocialEventSystem {
        return SocialEventSystem(gameRepository)
    }

    @Provides
    @Singleton
    fun provideWorldAIDirector(gameRepository: GameRepository, stabilitySystem: StabilitySystem): WorldAIDirector {
        return WorldAIDirector(gameRepository, stabilitySystem)
    }

    @Provides
    @Singleton
    fun provideWorldSimulation2_0(gameRepository: GameRepository): WorldSimulation2_0 {
        return WorldSimulation2_0(gameRepository)
    }

    @Provides
    @Singleton
    fun provideWorldSimulationCoordinator(
        gameRepository: GameRepository,
        worldSimulation2_0: WorldSimulation2_0,
        aiDirector: WorldAIDirector
    ): WorldSimulationCoordinator {
        return WorldSimulationCoordinator(gameRepository, worldSimulation2_0, aiDirector)
    }

    @Provides
    @Singleton
    fun provideTravelSystem(
        gameRepository: GameRepository,
        worldMap: WorldMap,
        cityCatalogue: CityCatalogue,
        encounterSystem: EncounterSystem
    ): TravelSystem {
        return TravelSystem(gameRepository, worldMap, cityCatalogue, encounterSystem)
    }

    @Provides
    @Singleton
    fun provideQuestResolutionSystem(
        gameRepository: GameRepository,
        questSystem: QuestSystem,
        lootSystem: LootSystem,
        reputationSystem: ReputationSystem
    ): QuestResolutionSystem {
        return QuestResolutionSystem(gameRepository, questSystem, lootSystem, reputationSystem)
    }

    @Provides
    @Singleton
    fun provideQuestTravelFlow(
        questSystem: QuestSystem,
        questResolutionSystem: QuestResolutionSystem,
        travelSystem: TravelSystem
    ): QuestTravelFlow {
        return QuestTravelFlow(questSystem, questResolutionSystem, travelSystem)
    }

    @Provides
    @Singleton
    fun provideGameBootstrapper(
        gameRepository: GameRepository,
        questSystem: QuestSystem,
        dialogueManager: DialogueManager,
        cityCatalogue: CityCatalogue,
        itemCatalogue: ItemCatalogue,
        worldMap: WorldMap
    ): GameBootstrapper {
        return GameBootstrapper(gameRepository, questSystem, dialogueManager, cityCatalogue, itemCatalogue, worldMap)
    }

    @Provides
    @Singleton
    fun provideGameLoopController(
        gameRepository: GameRepository,
        gameBootstrapper: GameBootstrapper,
        questSystem: QuestSystem,
        questResolutionSystem: QuestResolutionSystem,
        travelSystem: TravelSystem,
        cityCatalogue: CityCatalogue
    ): GameLoopController {
        return GameLoopController(gameRepository, gameBootstrapper, questSystem, questResolutionSystem, travelSystem, cityCatalogue)
    }
}
