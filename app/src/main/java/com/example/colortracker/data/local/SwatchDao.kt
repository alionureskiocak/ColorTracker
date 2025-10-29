package com.example.colortracker.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.colortracker.domain.model.ColorEntity
import com.example.colortracker.domain.model.ColorSwatchInfo

@Dao
interface SwatchDao {

    @Insert
    suspend fun insertSwatch(colorEntity : ColorEntity)

    @Delete
    suspend fun deleteSwatch(colorEntity: ColorEntity)

    @Query("DELETE  FROM ColorEntity")
    suspend fun clearAll()
}