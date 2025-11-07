package com.kroman.bookshelf.domain.repositories

import com.kroman.bookshelf.domain.model.BookItem
import com.kroman.bookshelf.domain.model.Result

interface BooksRepository {
    suspend fun getBooks(): Result<List<BookItem>>
    suspend fun getBook(id: Int): Result<BookItem>
}