package com.grimreich.grimreich.v1

class OtherSideNpcLogic {
    fun evaluate(
        npc: NPCLifePath,
        rels: List<TriLayerRelationship>,
        religion: PhenomenonReligion?
    ): OtherSideNpcState {
        val chaos = npc.chaosLevel
        val fullness = npc.fullnessLevel
        val relationChaos = rels.maxOfOrNull { it.chaosRelationLevel } ?: 0f
        val relationFull = rels.maxOfOrNull { it.fullnessRelationLevel } ?: 0f
        val baseCorruption = chaos + relationChaos / 2f
        val baseSanity = fullness + relationFull / 2f
        val dogmaFactor = when {
            religion == null -> 0
            religion.dogma.contains("nico", ignoreCase = true) -> 3
            religion.dogma.contains("mg", ignoreCase = true) -> 2
            else -> 1
        }
        val corruption = (baseCorruption + dogmaFactor).toInt().coerceIn(0, 20)
        val sanity = (baseSanity - dogmaFactor).toInt().coerceIn(0, 20)
        val loyalty = when {
            corruption <= 5 && sanity >= 10 -> OtherSideLoyalty.loyal
            corruption in 6..12 -> OtherSideLoyalty.torn
            else -> OtherSideLoyalty.betrayer
        }
        val deathRisk = when (loyalty) {
            OtherSideLoyalty.loyal -> (corruption / 2)
            OtherSideLoyalty.torn -> (5 + corruption / 2)
            OtherSideLoyalty.betrayer -> (10 + corruption)
        }.coerceIn(0, 25)
        val rewardModifier = when (loyalty) {
            OtherSideLoyalty.loyal -> 2 - (corruption / 10)
            OtherSideLoyalty.torn -> 0
            OtherSideLoyalty.betrayer -> -2
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
