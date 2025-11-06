package com.kroman.bookshelf.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface BooksApi {
    @GET("books")
    suspend fun getBooks(): Response<BooksResponse>

    @GET("books/{id}")
    suspend fun getBook(@Path("id") id: Int): Response<BookResponse>
}