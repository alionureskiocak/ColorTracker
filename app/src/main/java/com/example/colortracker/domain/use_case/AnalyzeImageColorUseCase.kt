package com.example.colortracker.domain.use_case

import android.graphics.Bitmap
import com.example.colortracker.domain.model.ColorSwatchInfo
import com.example.colortracker.domain.repository.PaletteRepository
import javax.inject.Inject

class AnalyzeImageColorUseCase @Inject constructor(
    private val repository : PaletteRepository
) {

    suspend operator fun invoke(bitmap : Bitmap) : List<ColorSwatchInfo>{
        return repository.analyzeBitmap(bitmap)
    }
}