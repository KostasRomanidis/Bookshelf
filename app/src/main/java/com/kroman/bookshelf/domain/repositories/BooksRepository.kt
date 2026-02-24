package com.kroman.bookshelf.domain.repositories

import androidx.paging.PagingData
import com.kroman.bookshelf.domain.model.BookItem
import com.kroman.bookshelf.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface BooksRepository {
    suspend fun getBooks(): Result<List<BookItem>>
    fun getPagedBooks(): Flow<PagingData<BookItem>>
    fun getFavoriteBooksPaged(): Flow<PagingData<BookItem>>
    suspend fun toggleFavorite(bookId: Int)
    fun observeIsFavorite(bookId: Int): Flow<Boolean>
    suspend fun getBook(id: Int): Result<BookItem>
}
