package com.kroman.bookshelf.data.remote.api

import com.kroman.bookshelf.data.remote.responses.BookResponse
import com.kroman.bookshelf.data.remote.responses.BooksResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BooksApi {
    @GET("books")
    suspend fun getBooks(@Query("page") page: Int? = null): Response<BooksResponse>

    @GET("books/{id}")
    suspend fun getBook(@Path("id") id: Int): Response<BookResponse>
}