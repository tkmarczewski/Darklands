package com.darklandsmobile.grimreich.v1

enum class OtherSideLoyalty {
    LOYAL,
    TORN,
    BETRAYER
}

data class OtherSideNpcState(
    val npcName: String,
    val loyalty: OtherSideLoyalty,
    val currentHp: Int,
    val maxHp: Int,
    val armorClass: Int,
    val rewardModifier: Int,
    val deathRisk: Int
)
