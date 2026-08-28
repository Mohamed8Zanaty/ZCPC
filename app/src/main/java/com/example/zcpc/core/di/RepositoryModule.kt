package com.example.zcpc.core.di

import com.example.zcpc.data.repository.CodeforcesRepositoryImpl
import com.example.zcpc.domain.repository.CodeforcesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCodeforcesRepository(
        codeforcesRepositoryImpl: CodeforcesRepositoryImpl
    ): CodeforcesRepository
}