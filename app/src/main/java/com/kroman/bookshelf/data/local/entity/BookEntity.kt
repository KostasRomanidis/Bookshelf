package com.kroman.bookshelf.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val subjects: String,
    val languages: String,
    val downloadCount: Int,
    val serverOrder: Long,
    val lastUpdated: Long = System.currentTimeMillis()
)
