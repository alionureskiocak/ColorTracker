package com.example.colortracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.colortracker.domain.model.ColorSwatchInfo

@Database(entities = [ColorSwatchInfo::class], version = 1)
abstract class SwatchDatabase : RoomDatabase() {

    abstract fun swatchDao() : SwatchDao
}