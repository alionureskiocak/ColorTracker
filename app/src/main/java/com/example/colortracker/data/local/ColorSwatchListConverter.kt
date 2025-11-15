package com.example.colortracker.data.local

import androidx.room.TypeConverter
import com.example.colortracker.domain.model.ColorSwatchInfo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ColorSwatchListConverter {

    @TypeConverter
    fun fromSwatchList(list: List<ColorSwatchInfo>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun toSwatchList(json: String): List<ColorSwatchInfo> {
        val type = object : TypeToken<List<ColorSwatchInfo>>() {}.type
        return Gson().fromJson(json, type)
    }
}