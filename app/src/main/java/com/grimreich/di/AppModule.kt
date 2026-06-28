package com.grimreich.di

import com.grimreich.core.CombatRandomProvider
import com.grimreich.core.DefaultCombatRandomProvider
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
}
