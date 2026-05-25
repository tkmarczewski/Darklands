package com.darklandsmobile.core

data class ResolutionScreenState(
    val questId: String,
    val cityId: String,
    val goldBefore: Int,
    val goldAfter: Int,
    val reputationAfter: Int,
    val summary: String
)