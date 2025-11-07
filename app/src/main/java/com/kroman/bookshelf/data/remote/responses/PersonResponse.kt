package com.kroman.bookshelf.data.remote.responses

import com.kroman.bookshelf.domain.model.PersonItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PersonResponse(
    @SerialName("birth_year") val yearOfBirth: Int?,
    @SerialName("death_year") val yearOfDeath: Int?,
    val name: String
)

fun PersonResponse.mapToDomain(): PersonItem = PersonItem(
    name = this.name,
    yearOfBirth = this.yearOfBirth,
    yearOfDeath = this.yearOfDeath
)
