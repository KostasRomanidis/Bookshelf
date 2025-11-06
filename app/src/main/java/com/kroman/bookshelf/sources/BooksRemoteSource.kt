package com.kroman.bookshelf.sources

import com.kroman.bookshelf.network.BookResponse
import com.kroman.bookshelf.network.BooksApi
import com.kroman.bookshelf.network.BooksResponse
import retrofit2.Response

interface BooksRemoteSource {
    suspend fun get(): Response<BooksResponse>
    suspend fun getBook(id: Int): Response<BookResponse>
}

class BooksRemoteSourceImpl(
    private val booksApi: BooksApi
) : BooksRemoteSource {
    override suspend fun get(): Response<BooksResponse> = booksApi.getBooks()
    override suspend fun getBook(id: Int): Response<BookResponse> = booksApi.getBook(id)
}