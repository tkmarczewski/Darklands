package com.grimreich.core

/**
 * Stan społeczny bohatera wobec miasta i frakcji.
 */
data class SocialStanding(
    val city: String,
    val faction: String,
    var audienceUnlocked: Boolean = false,
    var suspicion: Int = 0,
    var respect: Int = 0
)

