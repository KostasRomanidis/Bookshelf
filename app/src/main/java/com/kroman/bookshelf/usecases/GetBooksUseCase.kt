package com.kroman.bookshelf.usecases

import com.kroman.bookshelf.domain.data.BookItem
import com.kroman.bookshelf.repositories.BooksRepository
import com.kroman.bookshelf.domain.data.Result

interface GetBooksUseCase {
    suspend fun execute(): Result<List<BookItem>>
}

class GetBooksUseCaseImpl(
    private val booksRepository: BooksRepository
) : GetBooksUseCase {
    override suspend fun execute(): Result<List<BookItem>> {
        return booksRepository.getBooks()
    }
}