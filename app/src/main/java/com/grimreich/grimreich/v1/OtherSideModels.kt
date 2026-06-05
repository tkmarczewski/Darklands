package com.grimreich.grimreich.v1

enum class OtherSideLoyalty { LOYAL, TORN, BETRAYER }

data class OtherSideNpcState(
    val npcName: String,
    val loyalty: OtherSideLoyalty,
    val sanity: Int,
    val corruption: Int,
    val deathRisk: Int,
    val rewardModifier: Int,
    val notes: String
)

data class OtherSideReward(
    val baseRewards: List<String>,
    val finalRewards: List<String>,
    val riskNote: String
)
