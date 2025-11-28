package com.example.colortracker.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.colortracker.domain.model.ColorEntity
import com.example.colortracker.domain.model.FavoriteSwatch
import kotlinx.coroutines.flow.Flow

@Dao
interface SwatchDao {

    @Insert
    suspend fun insertSwatch(colorEntity : ColorEntity)

    @Delete
    suspend fun deleteFavorites(colorEntity: ColorEntity)

    @Query("SELECT * FROM ColorEntity")
    fun getAllSwatch() : Flow<List<ColorEntity>>

    @Query("DELETE  FROM ColorEntity")
    suspend fun clearAll()

    @Insert
    suspend fun addFavorites(favoriteSwatch: FavoriteSwatch)

    @Query("DELETE FROM favoriteswatch WHERE hex = :hex")
    suspend fun deleteFavorites(hex: String)

    @Query("SELECT * FROM favoriteswatch")
     fun getFavorites() : Flow<List<FavoriteSwatch>>


}