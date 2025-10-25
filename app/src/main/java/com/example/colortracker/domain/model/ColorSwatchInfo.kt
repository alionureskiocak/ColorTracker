package com.example.colortracker.domain.model

data class ColorSwatchInfo(
    val rgb: Int,
    val hex: String,
    val population: Int,
    val percentage: Double,
    val titleTextColor: Int,
    val bodyTextColor: Int
)