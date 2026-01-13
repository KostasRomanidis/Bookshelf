package com.kroman.bookshelf.data.local.mappers

import com.kroman.bookshelf.data.local.dao.BookDao
import com.kroman.bookshelf.data.local.entity.BookEntity
import com.kroman.bookshelf.data.local.entity.PersonEntity
import com.kroman.bookshelf.data.local.entity.PersonType
import com.kroman.bookshelf.data.remote.responses.BookResponse
import com.kroman.bookshelf.data.remote.responses.PersonResponse
import com.kroman.bookshelf.domain.model.BookItem
import com.kroman.bookshelf.domain.model.PersonItem

fun BookResponse.toEntity(serverOrder: Long): BookEntity = BookEntity(
    id = id,
    title = title,
    subjects = subjects.joinToString(","),
    languages = languages.joinToString(","),
    downloadCount = downloadCount,
    serverOrder = serverOrder
)

fun PersonResponse.toEntity(): PersonEntity = PersonEntity(
    name = name,
    yearOfBirth = yearOfBirth,
    yearOfDeath = yearOfDeath
)

suspend fun BookEntity.toDomain(bookDao: BookDao): BookItem {
    val authors = bookDao.getPersonsForBook(id, PersonType.AUTHOR)
    val translators = bookDao.getPersonsForBook(id, PersonType.TRANSLATOR)
    return BookItem(
        id = id,
        title = title,
        authors = authors.map { it.toDomain() },
        subjects = subjects.split(",").filter { it.isNotEmpty() },
        languages = languages.split(",").filter { it.isNotEmpty() },
        downloadCount = downloadCount
    )
}

fun PersonEntity.toDomain(): PersonItem = PersonItem(
    name = name,
    yearOfBirth = yearOfBirth,
    yearOfDeath = yearOfDeath
)

