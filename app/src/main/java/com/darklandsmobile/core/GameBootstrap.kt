package com.darklandsmobile.core

import com.darklandsmobile.world.CityCatalogue

/**
 * Start gry: inicjalizuje mapę świata i katalog miast.
 */
object GameBootstrap {
    fun init() {
        WorldMap.seedSprint1()
        CityCatalogue.seedSprint1()
    }
}