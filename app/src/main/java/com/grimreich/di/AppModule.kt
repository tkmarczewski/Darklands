package com.grimreich.di

import com.grimreich.contracts.CollapseRandomProvider
import com.grimreich.core.CombatRandomProvider
import com.grimreich.core.DefaultCombatRandomProvider
import com.grimreich.systems.DefaultCollapseRandomProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    @Binds
    @Singleton
    abstract fun bindCombatRandomProvider(impl: DefaultCombatRandomProvider): CombatRandomProvider

    @Binds
    @Singleton
    abstract fun bindEconomyCalculator(impl: com.grimreich.systems.EconomySystem): com.grimreich.core.EconomyCalculator

    @Binds
    @Singleton
    abstract fun bindCollapseRandomProvider(impl: DefaultCollapseRandomProvider): CollapseRandomProvider
}
