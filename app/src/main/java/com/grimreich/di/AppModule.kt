package com.grimreich.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    // All systems are now provided via @Inject constructor on their respective classes.
}
