package com.example.colortracker.utils

import com.example.colortracker.domain.model.ColorSwatchInfo
import com.example.colortracker.domain.model.FavoriteSwatch

// FavoriteSwatch -> ColorSwatchInfo dönüşümü
fun FavoriteSwatch.toColorSwatchInfo(): ColorSwatchInfo {
    return ColorSwatchInfo(
        hex = this.hex,
        rgb = this.rgb,
        population = this.population,
        percentage = this.percentage,
        titleTextColor = this.titleTextColor,
        bodyTextColor = this.bodyTextColor
    )
}