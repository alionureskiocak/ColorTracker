package com.example.colortracker.domain.model

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ColorEntity(
    val swatch : ColorSwatchInfo,
    val bitmap : ByteArray
){
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ColorEntity

        if (id != other.id) return false
        if (swatch != other.swatch) return false
        if (!bitmap.contentEquals(other.bitmap)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + swatch.hashCode()
        result = 31 * result + bitmap.contentHashCode()
        return result
    }
}