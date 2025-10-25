package com.example.colortracker.domain.repository

import android.graphics.Bitmap
import com.example.colortracker.domain.model.ColorSwatchInfo

interface PaletteRepository {

    suspend fun analyzeBitmap(bitmap : Bitmap) : List<ColorSwatchInfo>
}