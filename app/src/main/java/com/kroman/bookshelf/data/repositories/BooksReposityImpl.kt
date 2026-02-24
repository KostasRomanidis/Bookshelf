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
                prefetchDistance = 2,
                initialLoadSize = 32,
                enablePlaceholders = false,
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

    override fun getFavoriteBooksPaged(): Flow<PagingData<BookItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 32,
                prefetchDistance = 2,
                initialLoadSize = 32,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = { bookDao.getFavoriteBooksPaged() }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain(bookDao = bookDao) }
        }
    }

    override suspend fun toggleFavorite(bookId: Int) {
        val current = bookDao.getIsFavorite(bookId) ?: return
        bookDao.updateFavorite(bookId = bookId, value = !current)
    }

    override fun observeIsFavorite(bookId: Int): Flow<Boolean> {
        return bookDao.observeIsFavorite(bookId).map { it ?: false }
    }

    override suspend fun getBook(id: Int): Result<BookItem> {
        return try {
            val cached = booksLocalSource.getBook(id)
            if (cached != null) {
                return Result.Success(cached)
            }

            val response = booksRemoteDataSource.getBook(id)
            if (!response.isSuccessful) {
                return Result.Error(exception = Exception("Book not found"))
            }

            val bookResponse = response.body() ?: return Result.Error(Exception("Empty body"))

            val bookEntity = bookResponse.toEntity(serverOrder = Long.MAX_VALUE)
            val authors = bookResponse.authors.map { it.toEntity() }
            val translators = bookResponse.translators.map { it.toEntity() }

            bookDao.insertBookWithRelations(bookEntity, authors, translators)
            val updatedBook = booksLocalSource.getBook(id)
                ?: return Result.Error(Exception("Book not found in local storage"))
            Result.Success(updatedBook)
        } catch (e: IOException) {
            Result.Error(exception = e)
        } catch (e: HttpException) {
            Result.Error(exception = e)
        }
    }
}
