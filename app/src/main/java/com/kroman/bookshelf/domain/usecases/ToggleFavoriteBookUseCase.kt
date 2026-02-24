package com.kroman.bookshelf.domain.usecases

import com.kroman.bookshelf.domain.repositories.BooksRepository

interface ToggleFavoriteBookUseCase {
    suspend fun execute(bookId: Int)
}

class ToggleFavoriteBookUseCaseImpl(
    private val booksRepository: BooksRepository
) : ToggleFavoriteBookUseCase {
    override suspend fun execute(bookId: Int) {
        booksRepository.toggleFavorite(bookId)
    }
}
