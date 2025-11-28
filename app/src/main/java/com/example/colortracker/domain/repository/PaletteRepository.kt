package com.example.colortracker.domain.repository

import android.graphics.Bitmap
import com.example.colortracker.domain.model.ColorEntity
import com.example.colortracker.domain.model.ColorSwatchInfo
import com.example.colortracker.domain.model.FavoriteSwatch
import kotlinx.coroutines.flow.Flow

interface PaletteRepository {

    suspend fun analyzeBitmap(bitmap : Bitmap) : List<ColorSwatchInfo>

    suspend fun insertSwatch(colorEntity : ColorEntity)

    suspend fun deleteSwatch(colorEntity: ColorEntity)

    suspend fun getAllSwatch() :Flow<List<ColorEntity>>

    suspend fun clearAll()

    suspend fun addFavorites(favoriteSwatch: FavoriteSwatch)

    suspend fun deleteFavorites(hex : String)

    suspend fun getFavorites() : Flow<List<FavoriteSwatch>>

}