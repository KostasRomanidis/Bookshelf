package com.kroman.bookshelf.repositories

import com.kroman.bookshelf.domain.data.BookItem
import com.kroman.bookshelf.domain.data.PersonItem
import com.kroman.bookshelf.domain.data.Result
import com.kroman.bookshelf.network.BookResponse
import com.kroman.bookshelf.network.PersonResponse
import com.kroman.bookshelf.sources.BooksRemoteSource
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

interface BooksRepository {
    suspend fun getBooks(): Result<List<BookItem>>
}

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
}

private fun <T, R> handleResponse(
    response: Response<T>,
    mapper: (T) -> R
): Result<R> {
    return when {
        response.isSuccessful -> {
            response.body()?.let { body ->
                Result.Success(mapper(body))
            } ?: Result.Error(
                exception = NullPointerException("Response body is null"),
            )
        }

        else -> {
            Result.Error(
                exception = HttpException(response),
            )
        }
    }
}

fun BookResponse.mapToDomain(): BookItem = BookItem(
    id = this.id,
    title = this.title,
    authors = this.authors.map { it.mapToDomain() },
    subjects = this.subjects,
    languages = this.languages,
    downloadCount = this.downloadCount,
)

fun PersonResponse.mapToDomain(): PersonItem = PersonItem(
    name = this.name,
    yearOfBirth = this.yearOfBirth,
    yearOfDeath = this.yearOfDeath
)
