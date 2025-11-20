package com.example.colortracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.colortracker.domain.model.ColorEntity
import com.example.colortracker.domain.model.ColorSwatchInfo
import com.example.colortracker.domain.model.FavoriteSwatch

@Database(entities = [ColorEntity::class, FavoriteSwatch::class], version = 1)
@TypeConverters(ColorSwatchListConverter::class)
abstract class SwatchDatabase : RoomDatabase() {

    abstract fun swatchDao() : SwatchDao
}