package com.darklandsmobile.core

object WorldMap {
    data class Node(val id: String, val name: String, val region: String, val neighbors: List<String>)
    private val nodes = listOf(
        Node("magdeburg",   "Magdeburg",        "town",   listOf("road_north","road_south")),
        Node("road_north",  "Trakt Polnocny",   "road",   listOf("magdeburg","forest_deep")),
        Node("road_south",  "Trakt Poludniowy", "road",   listOf("magdeburg","forest_dark")),
        Node("forest_deep", "Gleboki Las",      "forest", listOf("road_north")),
        Node("forest_dark", "Mroczny Las",      "forest", listOf("road_south"))
    )
    fun getNode(id: String) = nodes.firstOrNull { it.id == id }
    fun all() = nodes
}
