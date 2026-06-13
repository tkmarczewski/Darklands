package com.grimreich.contracts

import com.google.gson.Gson
import com.grimreich.systems.WorldSimulationProviderPrototype
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ContractSerializationTest {

    @Test
    fun worldSnapshotSerialization() {
        val gson = Gson()
        val snapshot = WorldSimulationProviderPrototype.captureSnapshot()
        
        val json = gson.toJson(snapshot)
        assertNotNull(json)
        
        val deserialized = gson.fromJson(json, WorldSnapshot::class.java)
        assertEquals(snapshot.timestamp, deserialized.timestamp)
        assertEquals(snapshot.regionState.id, deserialized.regionState.id)
        assertEquals(snapshot.npcStates.size, deserialized.npcStates.size)
    }
    
    @Test
    fun simulationTickContextData() {
        val context = SimulationTickContext(
            scale = SimulationScale.MESO,
            deltaTime = 0.5f,
            worldSeed = 123,
            currentDay = 10,
            totalTicks = 1000L
        )
        
        assertEquals(SimulationScale.MESO, context.scale)
        assertEquals(123, context.worldSeed)
    }
}
