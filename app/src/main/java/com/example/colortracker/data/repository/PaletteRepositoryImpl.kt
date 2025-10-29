package com.example.colortracker.data.repository

import android.graphics.Bitmap
import androidx.palette.graphics.Palette
import com.example.colortracker.data.local.SwatchDao
import com.example.colortracker.domain.model.ColorEntity
import com.example.colortracker.domain.model.ColorSwatchInfo
import com.example.colortracker.domain.repository.PaletteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.div
import kotlin.text.toDouble
import kotlin.text.toLong

class PaletteRepositoryImpl @Inject constructor(
    private val dao: SwatchDao
)
    : PaletteRepository {
    override suspend fun analyzeBitmap(bitmap: Bitmap): List<ColorSwatchInfo> = withContext(
        Dispatchers.Default) {

        val palette = Palette.from(bitmap).maximumColorCount(16).generate()
        val swatches = palette.swatches
        val totalPopulation = swatches.sumOf { it.population.toLong() }.toDouble().coerceAtLeast(1.0)

        swatches
            .sortedByDescending { it.population }
            .map { sw ->
                val perc = sw.population / totalPopulation
                ColorSwatchInfo(
                    rgb = sw.rgb,
                    hex = String.format("#%06X",0XFFFFFF and sw.rgb),
                    population = sw.population,
                    percentage = perc,
                    bodyTextColor = sw.bodyTextColor,
                    titleTextColor = sw.titleTextColor
                )
            }
    }

    override suspend fun insertSwatch(colorEntity: ColorEntity) {
        dao.insertSwatch(colorEntity)
    }

    override suspend fun deleteSwatch(colorEntity: ColorEntity) {
        dao.deleteSwatch(colorEntity)
    }

    override fun getAllSwatch(): List<ColorEntity> {
        return dao.getAllSwatch()
    }

    override suspend fun clearAll() {
       dao.clearAll()
    }

}