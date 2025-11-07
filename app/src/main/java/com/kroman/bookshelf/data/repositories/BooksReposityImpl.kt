package com.kroman.bookshelf.data.repositories

import com.kroman.bookshelf.data.helpers.handleResponse
import com.kroman.bookshelf.data.remote.responses.mapToDomain
import com.kroman.bookshelf.data.remote.sources.BooksRemoteSource
import com.kroman.bookshelf.domain.model.BookItem
import com.kroman.bookshelf.domain.model.Result
import com.kroman.bookshelf.domain.repositories.BooksRepository
import retrofit2.HttpException
import java.io.IOException

class BooksRepositoryImpl(
    private val booksRemoteDataSource: BooksRemoteSource
) : BooksRepository {
    override suspend fun getBooks(): Result<List<BookItem>> {
        return try {
            val response = booksRemoteDataSource.get()
            handleResponse(response) { it.results.map { dto -> dto.mapToDomain() } }
        } catch (e: IOException) {
            Result.Error(exception = e)
        } catch (e: HttpException) {
            Result.Error(exception = e)
        }
    }

    override suspend fun getBook(id: Int): Result<BookItem> {
        return try {
            val response = booksRemoteDataSource.getBook(id)
            handleResponse(response) { it.mapToDomain() }
        } catch (e: IOException) {
            Result.Error(exception = e)
        } catch (e: HttpException) {
            Result.Error(exception = e)
        }
    }
}