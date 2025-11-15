package com.example.colortracker.domain.model

import android.graphics.Bitmap
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ColorEntity(
    val swatches : List<ColorSwatchInfo>,
    val colorPath : String
){
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0
}