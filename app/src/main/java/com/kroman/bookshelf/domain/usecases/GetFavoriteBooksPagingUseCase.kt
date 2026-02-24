package com.kroman.bookshelf.domain.usecases

import androidx.paging.PagingData
import com.kroman.bookshelf.domain.model.BookItem
import com.kroman.bookshelf.domain.repositories.BooksRepository
import kotlinx.coroutines.flow.Flow

interface GetFavoriteBooksPagingUseCase {
    fun execute(): Flow<PagingData<BookItem>>
}

class GetFavoriteBooksPagingUseCaseImpl(
    private val booksRepository: BooksRepository
) : GetFavoriteBooksPagingUseCase {
    override fun execute(): Flow<PagingData<BookItem>> {
        return booksRepository.getFavoriteBooksPaged()
    }
}
