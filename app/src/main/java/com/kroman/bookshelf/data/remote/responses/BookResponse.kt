package com.kroman.bookshelf.data.remote.responses

import com.kroman.bookshelf.domain.model.BookItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BookResponse(
    val id: Int,
    val title: String,
    val subjects: List<String>,
    val authors: List<PersonResponse>,
    val translators: List<PersonResponse>,
    val languages: List<String>,
    @SerialName("download_count") val downloadCount: Int
)


fun BookResponse.mapToDomain(): BookItem = BookItem(
    id = this.id,
    title = this.title,
    authors = this.authors.map { it.mapToDomain() },
    subjects = this.subjects,
    languages = languages,
    downloadCount = downloadCount,
)
