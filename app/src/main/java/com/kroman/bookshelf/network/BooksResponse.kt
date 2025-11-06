package com.kroman.bookshelf.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BooksResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<BookResponse>
)

@Serializable
data class BookResponse(
    val id: Int,
    val title: String,
    val subjects: List<String>,
    val authors: List<PersonResponse>,
    val summaries: List<String>,
    val translators: List<PersonResponse>,
    val languages: List<String>,
    @SerialName("download_count") val downloadCount: Int
)

@Serializable
data class PersonResponse(
    @SerialName("birth_year") val yearOfBirth: Int?,
    @SerialName("death_year") val yearOfDeath: Int?,
    val name: String
)

