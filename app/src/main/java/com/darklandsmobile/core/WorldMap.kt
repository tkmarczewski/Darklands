package com.darklandsmobile.core

/**
 * Węzeł mapy świata: miasto, trasa albo inna lokacja.
 */
data class WorldNode(
    val id: String,
    val name: String,
    val region: String,
    val type: String,
    val connections: MutableList<String> = mutableListOf()
)

/**
 * Prosta mapa świata oparta o węzły i połączenia.
 */
object WorldMap {
    private val nodes = mutableMapOf<String, WorldNode>()

    fun register(node: WorldNode) { nodes[node.id] = node }

    fun connect(a: String, b: String) {
        nodes[a]?.connections?.add(b)
        nodes[b]?.connections?.add(a)
    }

    fun get(id: String): WorldNode? = nodes[id]
    fun all(): List<WorldNode> = nodes.values.toList()

    /**
     * Minimalny seed sprintu 6: 6 miast z prostą siatką połączeń.
     */
    fun seedSprint1() {
        if (nodes.isNotEmpty()) return
        listOf(
            WorldNode("magdeburg", "Magdeburg", "central", "city"),
            WorldNode("koln", "Köln", "west", "city"),
            WorldNode("nurnberg", "Nürnberg", "south", "city"),
            WorldNode("frankfurt", "Frankfurt", "central_west", "city"),
            WorldNode("praha", "Praha", "east_south", "city"),
            WorldNode("lubeck", "Lübeck", "north", "city")
        ).forEach { register(it) }
        connect("magdeburg", "frankfurt")
        connect("magdeburg", "koln")
        connect("magdeburg", "nurnberg")
        connect("magdeburg", "praha")
        connect("magdeburg", "lubeck")
        connect("koln", "frankfurt")
        connect("frankfurt", "nurnberg")
        connect("nurnberg", "praha")
    }
}