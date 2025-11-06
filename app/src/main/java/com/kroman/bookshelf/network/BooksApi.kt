package com.kroman.bookshelf.network

import retrofit2.Response
import retrofit2.http.GET

interface BooksApi {
    @GET("books")
    suspend fun getBooks(): Response<BooksResponse>
}