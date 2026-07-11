package com.grimreich.systems

import com.grimreich.contracts.CollapseRandomProvider
import com.grimreich.grimreich.v1.CollapseScenario
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class DefaultCollapseRandomProvider @Inject constructor() : CollapseRandomProvider {
    override fun chooseScenario(options: List<CollapseScenario>): CollapseScenario {
        return options.random(Random.Default)
    }
}
