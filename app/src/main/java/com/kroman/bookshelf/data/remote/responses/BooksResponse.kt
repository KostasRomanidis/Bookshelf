package com.kroman.bookshelf.data.remote.responses

import kotlinx.serialization.Serializable

@Serializable
data class BooksResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<BookResponse>
)