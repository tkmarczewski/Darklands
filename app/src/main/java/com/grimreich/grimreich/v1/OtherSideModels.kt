package com.grimreich.grimreich.v1

import kotlinx.serialization.Serializable

@Serializable
enum class OtherSideLoyalty {
    loyal, torn, betrayer;

    companion object {
        @JvmField val LOYAL = loyal
        @JvmField val TORN = torn
        @JvmField val BETRAYER = betrayer
    }
}

data class OtherSideNpcState(
    val name: String,
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
