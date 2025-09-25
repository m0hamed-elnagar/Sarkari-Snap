package com.rawderm.taaza.today.bloger.data.database

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json

class StringListConverter {
    @TypeConverter
    fun fromString(value: String): List<String> {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun toString(list: List<String>): String {
        return Json.encodeToString(list)
    }
}