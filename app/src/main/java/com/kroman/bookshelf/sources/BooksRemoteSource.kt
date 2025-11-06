package com.kroman.bookshelf.sources

import com.kroman.bookshelf.network.BooksApi
import com.kroman.bookshelf.network.BooksResponse
import retrofit2.Response

interface BooksRemoteSource {
    suspend fun get(): Response<BooksResponse>
}

class BooksRemoteSourceImpl(
    private val booksApi: BooksApi
) : BooksRemoteSource {
    override suspend fun get(): Response<BooksResponse> = booksApi.getBooks()
}