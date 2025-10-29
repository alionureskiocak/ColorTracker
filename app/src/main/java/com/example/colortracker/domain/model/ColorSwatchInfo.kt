package com.example.colortracker.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class ColorSwatchInfo(
    val rgb: Int,
    val hex: String,
    val population: Int,
    val percentage: Double,
    val titleTextColor: Int,
    val bodyTextColor: Int
){
}