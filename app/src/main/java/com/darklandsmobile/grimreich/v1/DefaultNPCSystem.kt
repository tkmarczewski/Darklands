package com.darklandsmobile.grimreich.v1

class DefaultNPCSystem : NPCSystem {
    private val lifePaths = mutableMapOf<String, NPCLifePath>()
    private val relationships = mutableMapOf<String, MutableList<TriLayerRelationship>>()
    private val religions = mutableMapOf<String, PhenomenonReligion>()

    override fun applyLifePath(path: NPCLifePath) {
        lifePaths[path.npcName] = path
    }

    override fun applyRelationship(rel: TriLayerRelationship) {
        relationships.getOrPut(rel.npcName) { mutableListOf() }.add(rel)
    }

    override fun applyReligion(religion: PhenomenonReligion) {
        religions[religion.religionName] = religion
    }

    fun getNPCPath(name: String): NPCLifePath? = lifePaths[name]
    fun getRelationships(name: String): List<TriLayerRelationship> = relationships[name] ?: emptyList()
    fun getReligionByNpc(name: String): PhenomenonReligion? =
        religions.values.firstOrNull { it.npcImpact.contains(name, ignoreCase = true) }
}
