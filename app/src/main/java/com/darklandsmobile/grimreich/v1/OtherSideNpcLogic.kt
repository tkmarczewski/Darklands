package com.darklandsmobile.grimreich.v1

class OtherSideNpcLogic {
    fun evaluate(
        npc: NPCLifePath,
        rels: List<TriLayerRelationship>,
        religion: PhenomenonReligion?
    ): OtherSideNpcState {
        val chaos = npc.chaosLevel
        val fullness = npc.fullnessLevel
        val relationChaos = rels.maxOfOrNull { it.chaosRelationLevel } ?: 0
        val relationFull = rels.maxOfOrNull { it.fullnessRelationLevel } ?: 0
        val baseCorruption = chaos + relationChaos / 2
        val baseSanity = fullness + relationFull / 2
        val dogmaFactor = when {
            religion == null -> 0
            religion.dogma.contains("nico", ignoreCase = true) -> 3
            religion.dogma.contains("mg", ignoreCase = true) -> 2
            else -> 1
        }
        val corruption = (baseCorruption + dogmaFactor).coerceIn(0, 20)
        val sanity = (baseSanity - dogmaFactor).coerceIn(0, 20)
        val loyalty = when {
            corruption <= 5 && sanity >= 10 -> OtherSideLoyalty.LOYAL
            corruption in 6..12 -> OtherSideLoyalty.TORN
            else -> OtherSideLoyalty.BETRAYER
        }
        val deathRisk = when (loyalty) {
            OtherSideLoyalty.LOYAL -> (corruption / 2).coerceIn(0, 10)
            OtherSideLoyalty.TORN -> (5 + corruption / 2).coerceIn(5, 15)
            OtherSideLoyalty.BETRAYER -> (10 + corruption).coerceIn(10, 25)
        }
        val rewardModifier = when (loyalty) {
            OtherSideLoyalty.LOYAL -> 2 - (corruption / 10)
            OtherSideLoyalty.TORN -> 0
            OtherSideLoyalty.BETRAYER -> -2
        }
        val notes = buildString {
            append("Chaos=").append(chaos)
            append(", Fullness=").append(fullness)
            append(", RelChaos=").append(relationChaos)
            append(", RelFull=").append(relationFull)
            append(", DogmaFactor=").append(dogmaFactor)
            append(", DeathRisk=").append(deathRisk)
            append(", RewardMod=").append(rewardModifier)
        }
        return OtherSideNpcState(npc.npcName, loyalty, sanity, corruption, deathRisk, rewardModifier, notes)
    }
}
