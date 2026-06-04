package com.darklandsmobile.ui.expedition

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.darklandsmobile.core.GrimGameRepository
import com.darklandsmobile.core.GrimSeed
import com.darklandsmobile.grimreich.v1.*

data class OtherSideUiState(
    val name: String,
    val logicalLayer: String,
    val symmetricLayer: String,
    val zeroLayer: String,
    val enemies: List<String>,
    val rewards: List<String>,
    val regionImpact: String,
    val endingImpact: String,
    val npcStates: List<OtherSideNpcState>,
    val rewardSummary: OtherSideReward
)

class OtherSideViewModel : ViewModel() {
    private val _state = MutableLiveData<OtherSideUiState?>()
    val state: LiveData<OtherSideUiState?> get() = _state

    init {
        if (GrimGameRepository.state.grimEngine.query.getRegionSnapshot("Schwarzwald") == null) {
            GrimSeed.initialize()
        }
    }

    fun startForCurrentRegion() {
        val grimState = GrimGameRepository.state
        val region = grimState.currentRegion
        val difficultyTier = if (region == "Regensburg") 2 else 1
        val baseRewards = listOf("mist_shard", "blood_seal")
        val expedition = OtherSideExpedition(
            expeditionName = "Other Side: $region",
            logicalLayer = "Logic-$region",
            symmetricLayer = "Sym-$region",
            zeroLayer = "Zero-$region",
            enemies = listOf("shadow_wraith", "mirror_knight"),
            rewards = baseRewards,
            regionImpact = "oscillation_$region",
            endingImpact = "branching_other_side",
            difficultyTier = difficultyTier
        )
        grimState.pendingExpeditionName = expedition.expeditionName
        grimState.grimEngine.startExpedition(expedition)

        val npcStates = buildList {
            grimState.grimEngine.queryNpc("Inkwizytor Hagen")?.let(::add)
            grimState.grimEngine.queryNpc("Alchemik Wulfram")?.let(::add)
        }
        val rewardSummary = OtherSideRewardSystem().applyNpcModifiers(baseRewards, npcStates, difficultyTier)
        val chaosLevel = grimState.grimEngine.query.getRegionSnapshot(region)?.chaosLevel ?: 0
        val generatedLoot = OtherSideLootTable().generate(baseRewards, rewardSummary, difficultyTier, chaosLevel, 3)

        _state.value = OtherSideUiState(
            name = expedition.expeditionName,
            logicalLayer = expedition.logicalLayer,
            symmetricLayer = expedition.symmetricLayer,
            zeroLayer = expedition.zeroLayer,
            enemies = expedition.enemies,
            rewards = generatedLoot.entries.map { it.id },
            regionImpact = expedition.regionImpact,
            endingImpact = expedition.endingImpact,
            npcStates = npcStates,
            rewardSummary = rewardSummary
        )
    }
}
