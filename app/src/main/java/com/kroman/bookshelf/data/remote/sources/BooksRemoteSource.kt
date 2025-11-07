package com.kroman.bookshelf.data.remote.sources

import com.kroman.bookshelf.data.remote.responses.BookResponse
import com.kroman.bookshelf.data.remote.api.BooksApi
import com.kroman.bookshelf.data.remote.responses.BooksResponse
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