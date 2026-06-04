package com.darklandsmobile.grimreich.v1

object GrimBuilders {

    fun defaultNonlinearTime(regionName: String): NonlinearTime =
        NonlinearTime(
            regionName = regionName,
            mistTimeLevel = 1,
            bloodTimeLevel = 1,
            reflectionTimeLevel = 1,
            fullnessTimeLevel = 1,
            chaosTimeLevel = 1,
            activeTimeEffects = emptyList(),
            regionImpact = "neutral_time",
            npcImpact = "no_effect",
            monsterImpact = "no_effect",
            endingImpact = "no_impact"
        )

    fun basicNPCLifePath(name: String): NPCLifePath =
        NPCLifePath(
            npcName = name,
            mistFate = "unknown_mist_fate",
            bloodFate = "unknown_blood_fate",
            reflectionFate = "unknown_reflection_fate",
            fullnessLevel = 0,
            chaosLevel = 0,
            timelineEvents = emptyList(),
            npcImpact = "no_impact",
            endingImpact = "no_impact"
        )

    fun emptyWorldChronicle(name: String = "Empty Chronicle"): WorldChronicle =
        WorldChronicle(
            chronicleName = name,
            mistRecord = "",
            bloodRecord = "",
            reflectionRecord = "",
            fullnessRecord = "",
            chaosRecord = "",
            regionImpact = "no_impact",
            npcImpact = "no_impact",
            endingImpact = "no_impact"
        )
}
