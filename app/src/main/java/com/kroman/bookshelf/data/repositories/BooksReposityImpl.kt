package com.kroman.bookshelf.data.repositories

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.kroman.bookshelf.data.helpers.BooksRemoteMediator
import com.kroman.bookshelf.data.helpers.handleResponse
import com.kroman.bookshelf.data.local.dao.BookDao
import com.kroman.bookshelf.data.local.db.BookshelfDatabase
import com.kroman.bookshelf.data.local.mappers.toDomain
import com.kroman.bookshelf.data.local.mappers.toEntity
import com.kroman.bookshelf.data.local.sources.BooksLocalSource
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

@OptIn(ExperimentalPagingApi::class)
class BooksRepositoryImpl(
    private val booksLocalSource: BooksLocalSource,
    private val booksRemoteDataSource: BooksRemoteSource,
    private val bookshelfDatabase: BookshelfDatabase,
    private val bookDao: BookDao
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
                prefetchDistance = 5,
                initialLoadSize = 32,
                enablePlaceholders = false,
                jumpThreshold = Int.MIN_VALUE
            ),
            remoteMediator = BooksRemoteMediator(
                booksRemoteDataSource,
                database = bookshelfDatabase
            ),
            pagingSourceFactory = { booksLocalSource.getAllBooksPaged() }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain(bookDao = bookDao) }
        }
    }

    override suspend fun getBook(id: Int): Result<BookItem> {
        return try {
            val cachedBook = booksLocalSource.getBook(id)
            if (cachedBook != null) {
                Result.Success(cachedBook)
            } else {
                val response = booksRemoteDataSource.getBook(id)
                if (response.isSuccessful) {
                    val bookResponse = response.body()!!

                    val bookEntity = bookResponse.toEntity()
                    val authors = bookResponse.authors.map { it.toEntity() }
                    val translators = bookResponse.translators.map { it.toEntity() }

                    bookDao.insertBookWithRelations(bookEntity, authors, translators)

                    Result.Success(bookResponse.mapToDomain())
                } else {
                    Result.Error(exception = Exception("Book not found"))
                }
            }
        } catch (e: IOException) {
            Result.Error(exception = e)
        } catch (e: HttpException) {
            Result.Error(exception = e)
        }
    }
}