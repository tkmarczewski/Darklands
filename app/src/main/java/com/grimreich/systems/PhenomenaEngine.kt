package com.grimreich.systems

import com.grimreich.contracts.WorldSnapshot
import com.grimreich.contracts.SimulationTickContext
import com.grimreich.domain.phenomena.PhenomenonType

/**
 * Program 4: Phenomena Engine 2.0.
 * Propagates the effects of ontological phenomena (Mist, Blood, etc.) across the world.
 */
object PhenomenaEngine {

    fun processPhenomena(snapshot: WorldSnapshot, context: SimulationTickContext) {
        // Logic to calculate phenomenon intensity shifts and apply local effects
        snapshot.phenomenaState.activePhenomena.forEach { (type, intensity) ->
            applyPhenomenonEffect(type, intensity, snapshot, context)
        }
    }

    private fun applyPhenomenonEffect(type: String, intensity: Float, snapshot: WorldSnapshot, context: SimulationTickContext) {
        when (type.uppercase()) {
            "MIST" -> {
                // Mist effects: Memory loss, vision reduction
                if (intensity > 0.7f) {
                    com.grimreich.systems.ChronicleSystem.record("Mgła gęstnieje, wymazując imiona ze wspomnień.", 1)
                }
            }
            "BLOOD" -> {
                // Blood effects: Increased aggression, vitality drain
            }
            "REFLECTION" -> {
                // Reflection effects: Identity doubling, paradoxes
            }
        }
    }
}
