package com.darklandsmobile.core

object GameBootstrap {
    fun init() {
        WorldMap.seedSprint1()
        com.darklandsmobile.world.CityCatalogue.seedSprint1()
    }
}