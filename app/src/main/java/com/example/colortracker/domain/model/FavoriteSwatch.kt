package com.example.colortracker.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FavoriteSwatch(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val rgb: Int,
    val hex: String,
    val population: Int,
    val percentage: Double,
    val titleTextColor: Int,
    val bodyTextColor: Int
)
