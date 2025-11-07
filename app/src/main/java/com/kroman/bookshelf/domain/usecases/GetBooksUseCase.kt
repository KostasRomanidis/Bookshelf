package com.kroman.bookshelf.domain.usecases

import com.kroman.bookshelf.domain.model.BookItem
import com.kroman.bookshelf.domain.repositories.BooksRepository
import com.kroman.bookshelf.domain.model.Result

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