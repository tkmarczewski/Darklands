package com.darklandsmobile.core

import com.darklandsmobile.systems.QuestSystem
import com.darklandsmobile.world.CityCatalogue

object GameBootstrap {
    fun init() {
        CityCatalogue.clear()
        WorldMap.clear()
        QuestSystem.clear()
    }
}