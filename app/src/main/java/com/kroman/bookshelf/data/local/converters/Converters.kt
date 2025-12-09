package com.kroman.bookshelf.data.local.converters

import androidx.room.TypeConverter
import com.kroman.bookshelf.data.local.entity.PersonType

class Converters {
    @TypeConverter
    fun fromPersonType(value: PersonType): String = value.name

    @TypeConverter
    fun toPersonType(value: String): PersonType = PersonType.valueOf(value)
}