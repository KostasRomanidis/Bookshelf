package com.kroman.bookshelf.domain.usecases

import com.kroman.bookshelf.domain.repositories.BooksRepository
import kotlinx.coroutines.flow.Flow

interface ObserveIsFavoriteUseCase {
    fun execute(bookId: Int): Flow<Boolean>
}

class ObserveIsFavoriteUseCaseImpl(
    private val booksRepository: BooksRepository
) : ObserveIsFavoriteUseCase {
    override fun execute(bookId: Int): Flow<Boolean> {
        return booksRepository.observeIsFavorite(bookId)
    }
}
