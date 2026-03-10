package com.kroman.bookshelf.domain.model

data class BookFilters(
    val searchQuery: String = "",
    val language: String? = null,
    val format: BookFormatFilter? = null,
    val sort: BookSortOption = BookSortOption.DEFAULT
)

enum class BookFormatFilter {
    EPUB,
    HTML,
    PDF
}

enum class BookSortOption {
    DEFAULT,
    DOWNLOAD_COUNT_DESC,
    TITLE_ASC
}
