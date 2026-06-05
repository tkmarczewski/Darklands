package com.grimreich.grimreich.v1

// Boss model classes
data class FactionBoss(
    var bossName: String,
    var faction: String,
    var title: String,
    var traits: List<String>,
    var secrets: List<String>,
    var dominions: List<String>,
    var allies: List<String>,
    var healthMin: Int,
    var healthMax: Int
)

data class TriLayerBoss(
    var bossName: String,
    var logicalLayerHp: Int,
    var symmetricLayerHp: Int,
    var zeroLayerHp: Int,
    var dominion: String,
    var triLayerUnlocked: Boolean,
    var phase: Int,
    var logicalPhaseUnlock: Boolean,
    var symmetricPhaseUnlock: Boolean,
    var zeroPhaseUnlock: Boolean,
    var fullPhaseUnlock: Boolean,
    var regionImpact: String,
    var endingImpact: String
)
