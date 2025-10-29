package com.example.colortracker.domain.model

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ColorEntity(
    val swatch : ColorSwatchInfo,
    val bitmap : Bitmap
){
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0
}