package com.example.colortracker.domain.repository

import android.graphics.Bitmap
import com.example.colortracker.domain.model.ColorEntity
import com.example.colortracker.domain.model.ColorSwatchInfo

interface PaletteRepository {

    suspend fun analyzeBitmap(bitmap : Bitmap) : List<ColorSwatchInfo>

    suspend fun insertSwatch(colorEntity : ColorEntity)

    suspend fun deleteSwatch(colorEntity: ColorEntity)

    fun getAllSwatch() : List<ColorEntity>

    suspend fun clearAll()

}