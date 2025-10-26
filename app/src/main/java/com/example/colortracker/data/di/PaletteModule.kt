package com.example.colortracker.data.di

import com.example.colortracker.data.repository.PaletteRepositoryImpl
import com.example.colortracker.domain.repository.PaletteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PaletteModule {

    @Singleton @Provides
    fun providePaletteRepository() : PaletteRepository = PaletteRepositoryImpl()
}