package com.kroman.bookshelf.data.local.converters

import androidx.room.TypeConverter
import com.kroman.bookshelf.data.local.entity.PersonType
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    @TypeConverter
    fun fromPersonType(value: PersonType): String = value.name

    @TypeConverter
    fun toPersonType(value: String): PersonType = PersonType.valueOf(value)

    @TypeConverter
    fun fromStringList(value: List<String>): String = json.encodeToString(value)

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        if (value.isNullOrBlank()) return emptyList()
        return runCatching { json.decodeFromString<List<String>>(value) }
            .getOrElse {
                // Backward-compatible read for legacy comma-separated values.
                value.split(",").map { entry -> entry.trim() }.filter { it.isNotEmpty() }
            }
    }

    @TypeConverter
    fun fromStringMap(value: Map<String, String>): String = json.encodeToString(value)

    @TypeConverter
    fun toStringMap(value: String?): Map<String, String> {
        if (value.isNullOrBlank()) return emptyMap()
        return runCatching { json.decodeFromString<Map<String, String>>(value) }
            .getOrDefault(emptyMap())
    }
}
