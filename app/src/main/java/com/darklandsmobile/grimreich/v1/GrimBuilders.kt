package com.darklandsmobile.grimreich.v1

object GrimBuilders {
    fun defaultNonlinearTime(regionName: String): NonlinearTime = NonlinearTime(regionName, 1, 1, 1, 1, 1, emptyList(), "neutral_time", "no_effect", "no_effect", "no_impact")
    fun basicNPCLifePath(name: String): NPCLifePath = NPCLifePath(name, "unknown", "unknown", "unknown", 0, 0, emptyList(), "no_impact", "no_impact")
    fun emptyWorldChronicle(name: String = "Empty Chronicle"): WorldChronicle = WorldChronicle(name, "", "", "", "", "", "no_impact", "no_impact", "no_impact")
}
