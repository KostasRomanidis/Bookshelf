package com.kroman.bookshelf.domain.usecases

import com.kroman.bookshelf.domain.model.BookItem
import com.kroman.bookshelf.domain.model.Result
import com.kroman.bookshelf.domain.repositories.BooksRepository

interface GetBookDetailsUseCase {
    suspend fun execute(id: Int): Result<BookItem>
}

class GetBookDetailsUseCaseImpl(
    private val booksRepository: BooksRepository
) : GetBookDetailsUseCase {
    override suspend fun execute(id: Int): Result<BookItem> {
        return booksRepository.getBook(id)
    }
}
