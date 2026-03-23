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
    val editors: List<PersonResponse> = emptyList(),
    val summaries: List<String> = emptyList(),
    val translators: List<PersonResponse>,
    val bookshelves: List<String> = emptyList(),
    val languages: List<String>,
    val copyright: Boolean? = null,
    @SerialName("media_type") val mediaType: String = "",
    val formats: Map<String, String> = emptyMap(),
    @SerialName("download_count") val downloadCount: Int
)


fun BookResponse.mapToDomain(): BookItem = BookItem(
    id = this.id,
    title = this.title,
    authors = this.authors.map { it.mapToDomain() },
    subjects = this.subjects,
    languages = languages,
    downloadCount = downloadCount,
    summaries = summaries,
    bookshelves = bookshelves,
    copyright = copyright,
    formats = formats,
    mediaType = mediaType,
)
