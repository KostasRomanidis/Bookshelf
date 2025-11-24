package com.kroman.bookshelf.data.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.kroman.bookshelf.data.helpers.handleResponse
import com.kroman.bookshelf.data.remote.responses.mapToDomain
import com.kroman.bookshelf.data.remote.sources.BooksPagingSource
import com.kroman.bookshelf.data.remote.sources.BooksRemoteSource
import com.kroman.bookshelf.domain.model.BookItem
import com.kroman.bookshelf.domain.model.Result
import com.kroman.bookshelf.domain.repositories.BooksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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

    override fun getPagedBooks(): Flow<PagingData<BookItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 32,
                prefetchDistance = 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { BooksPagingSource(booksRemoteDataSource) }
        ).flow.map { pagingData ->
            pagingData.map { it.mapToDomain() }
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