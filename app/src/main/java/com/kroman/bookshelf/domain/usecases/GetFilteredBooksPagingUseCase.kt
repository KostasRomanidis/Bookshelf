package com.kroman.bookshelf.domain.usecases

import androidx.paging.PagingData
import com.kroman.bookshelf.domain.model.BookFilters
import com.kroman.bookshelf.domain.model.BookItem
import com.kroman.bookshelf.domain.repositories.BooksRepository
import kotlinx.coroutines.flow.Flow

interface GetFilteredBooksPagingUseCase {
    fun execute(filters: BookFilters): Flow<PagingData<BookItem>>
}

class GetFilteredBooksPagingUseCaseImpl(
    private val booksRepository: BooksRepository
) : GetFilteredBooksPagingUseCase {
    override fun execute(filters: BookFilters): Flow<PagingData<BookItem>> {
        return booksRepository.getFilteredBooksPaged(filters)
    }
}
