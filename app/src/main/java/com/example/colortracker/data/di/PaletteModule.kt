package com.example.colortracker.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.colortracker.data.local.SwatchDao
import com.example.colortracker.data.local.SwatchDatabase
import com.example.colortracker.data.repository.PaletteRepositoryImpl
import com.example.colortracker.domain.repository.PaletteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PaletteModule {

    @Singleton @Provides
    fun providePaletteRepository() : PaletteRepository = PaletteRepositoryImpl()

    @Singleton @Provides
    fun provideSwatchDatabase(@ApplicationContext context : Context) : RoomDatabase{
        return Room.databaseBuilder(
            context,
            SwatchDatabase::class.java,
            "swatch_database"
        ).build()
    }

    @Singleton @Provides
    fun provideSwatchDao(db : SwatchDatabase) = db.swatchDao()
}