package com.kroman.bookshelf.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "persons")
data class PersonEntity(
    @PrimaryKey(autoGenerate = true)
    val personId: Long = 0,
    val name: String,
    val yearOfBirth: Int?,
    val yearOfDeath: Int?
)

enum class PersonType {
    AUTHOR,
    TRANSLATOR
}