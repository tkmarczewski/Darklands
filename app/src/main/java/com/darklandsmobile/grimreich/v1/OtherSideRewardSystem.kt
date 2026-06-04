package com.darklandsmobile.grimreich.v1

class OtherSideRewardSystem {
    fun applyNpcModifiers(
        baseRewards: List<String>,
        npcStates: List<OtherSideNpcState>,
        difficultyTier: Int = 1
    ): OtherSideReward {
        if (npcStates.isEmpty()) {
            return OtherSideReward(baseRewards, baseRewards, "Brak towarzyszy - brak modyfikatorow.")
        }
        val totalRewardMod = npcStates.sumOf { it.rewardModifier } - (difficultyTier - 1)
        val avgDeathRisk = npcStates.sumOf { it.deathRisk } / npcStates.size
        val extraRewards = mutableListOf<String>()
        if (totalRewardMod > 0) repeat(totalRewardMod) { extraRewards.add("bonus_mist_shard") }
        val finalRewards = buildList {
            addAll(baseRewards)
            addAll(extraRewards)
            if (totalRewardMod < 0) add("penalty_curse")
        }
        return OtherSideReward(
            baseRewards = baseRewards,
            finalRewards = finalRewards,
            riskNote = "Srednie ryzyko smierci towarzyszy: $avgDeathRisk, modyfikator nagrod: $totalRewardMod"
        )
    }
}
