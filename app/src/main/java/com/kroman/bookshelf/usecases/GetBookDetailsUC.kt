package com.kroman.bookshelf.usecases

import com.kroman.bookshelf.domain.data.BookItem
import com.kroman.bookshelf.domain.data.Result
import com.kroman.bookshelf.repositories.BooksRepository

interface GetBookDetailsUC {
    suspend fun execute(id: Int): Result<BookItem>
}

class GetBookDetailsUCImpl(
    private val booksRepository: BooksRepository
) : GetBookDetailsUC {
    override suspend fun execute(id: Int): Result<BookItem> {
        return booksRepository.getBook(id)
    }
}
