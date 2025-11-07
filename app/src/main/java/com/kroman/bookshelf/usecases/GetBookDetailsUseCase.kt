package com.kroman.bookshelf.usecases

import com.kroman.bookshelf.domain.data.BookItem
import com.kroman.bookshelf.domain.data.Result
import com.kroman.bookshelf.repositories.BooksRepository

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
