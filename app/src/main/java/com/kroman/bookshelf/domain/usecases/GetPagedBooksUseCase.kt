package com.kroman.bookshelf.domain.usecases

import androidx.paging.PagingData
import com.kroman.bookshelf.domain.model.BookItem
import com.kroman.bookshelf.domain.repositories.BooksRepository
import kotlinx.coroutines.flow.Flow

interface GetPagedBooksUseCase {
    fun execute(): Flow<PagingData<BookItem>>
}

class GetPagedBooksUseCaseImpl(
    private val booksRepository: BooksRepository
) : GetPagedBooksUseCase {
    override fun execute(): Flow<PagingData<BookItem>> = booksRepository.getPagedBooks()
}