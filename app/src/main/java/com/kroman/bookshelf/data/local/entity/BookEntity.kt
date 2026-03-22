package com.kroman.bookshelf.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val subjects: List<String>,
    val languages: List<String>,
    val summaries: List<String> = emptyList(),
    val bookshelves: List<String> = emptyList(),
    val language: String? = null,
    val mediaType: String = "",
    val copyright: Boolean? = null,
    val formats: Map<String, String> = emptyMap(),
    val hasEpub: Boolean = false,
    val hasHtml: Boolean = false,
    val hasPdf: Boolean = false,
    val downloadCount: Int,
    val serverOrder: Long,
    val isFavorite: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
)
