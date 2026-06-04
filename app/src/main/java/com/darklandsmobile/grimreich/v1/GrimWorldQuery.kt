package com.darklandsmobile.grimreich.v1

data class RegionSnapshot(
    val regionName: String,
    val emotionalState: String,
    val mistMind: String,
    val bloodBody: String,
    val reflectionSoul: String,
    val chaosLevel: Int,
    val mistTimeLevel: Int,
    val timeEffects: List<String>,
    val memory: List<String>,
    val reactions: List<String>,
    val endingImpact: String
)

data class CollapseSnapshot(
    val collapseStage: String,
    val layerCollapse: Int,
    val regionDecay: Int,
    val endingImpact: String
)

class GrimWorldQuery(private val regionSystem: RegionSystem) {
    fun getRegionSnapshot(name: String): RegionSnapshot? {
        val sys = regionSystem as? DefaultRegionSystem ?: return null
        val region = sys.getRegion(name) ?: return null
        val time = sys.getTime(name)
        return RegionSnapshot(
            regionName = region.regionName,
            emotionalState = region.emotionalState,
            mistMind = region.mistMind,
            bloodBody = region.bloodBody,
            reflectionSoul = region.reflectionSoul,
            chaosLevel = time?.chaosTimeLevel ?: 0,
            mistTimeLevel = time?.mistTimeLevel ?: 0,
            timeEffects = time?.activeTimeEffects ?: emptyList(),
            memory = region.memory,
            reactions = region.reactions,
            endingImpact = region.endingImpact
        )
    }

    fun getCollapseSnapshot(): CollapseSnapshot? {
        val sys = regionSystem as? DefaultRegionSystem ?: return null
        val c = sys.getCollapse() ?: return null
        return CollapseSnapshot(c.collapseStage, c.layerCollapse, c.regionDecay, c.endingImpact)
    }
}
