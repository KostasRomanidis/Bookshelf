package com.kroman.bookshelf.domain.model

data class BookItem(
    val id: Int,
    val title: String,
    val authors: List<PersonItem>,
    val subjects: List<String>,
    val languages: List<String>,
    val downloadCount: Int,
)

data class PersonItem(
    val yearOfBirth: Int?,
    val yearOfDeath: Int?,
    val name: String
)
