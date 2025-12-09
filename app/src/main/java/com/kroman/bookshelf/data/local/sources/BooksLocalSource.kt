package com.kroman.bookshelf.data.local.sources

import androidx.paging.PagingSource
import com.kroman.bookshelf.data.local.dao.BookDao
import com.kroman.bookshelf.data.local.entity.BookEntity
import com.kroman.bookshelf.data.local.mappers.toDomain
import com.kroman.bookshelf.domain.model.BookItem

interface BooksLocalSource {
    fun getAllBooksPaged(): PagingSource<Int, BookEntity>
    suspend fun getBook(id: Int): BookItem?
}

class BooksLocalSourceImpl(
    private val bookDao: BookDao
) : BooksLocalSource {
    override fun getAllBooksPaged(): PagingSource<Int, BookEntity> {
        return bookDao.getAllBooksPaged()
    }

    override suspend fun getBook(id: Int): BookItem? {
        val book = bookDao.getBookById(id)
        return book?.toDomain(bookDao)
    }

}