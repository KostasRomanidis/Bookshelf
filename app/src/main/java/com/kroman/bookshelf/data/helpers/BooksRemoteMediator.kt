package com.kroman.bookshelf.data.helpers

import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.kroman.bookshelf.data.local.db.BookshelfDatabase
import com.kroman.bookshelf.data.local.entity.BookEntity
import com.kroman.bookshelf.data.local.entity.RemoteKeyEntity
import com.kroman.bookshelf.data.local.mappers.toEntity
import com.kroman.bookshelf.data.remote.sources.BooksRemoteSource
import retrofit2.HttpException
import java.io.IOException


@OptIn(ExperimentalPagingApi::class)
class BooksRemoteMediator(
    private val booksRemoteSource: BooksRemoteSource,
    private val database: BookshelfDatabase
): RemoteMediator<Int, BookEntity>() {
    private val bookDao = database.bookDao()
    private val remoteKeyDao = database.remoteKeyDao()

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, BookEntity>
    ): MediatorResult {
        return try {
            val page = when(loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKey = state.lastItemOrNull()?.let { book ->
                        remoteKeyDao.getRemoteKey(book.id)
                    }
                    remoteKey?.nextKey ?: return MediatorResult.Success(endOfPaginationReached = remoteKey != null)

                }
            }

            val response = booksRemoteSource.get(page)
            if (!response.isSuccessful) {
                return MediatorResult.Error(HttpException(response))
            }

            val books = response.body()?.results ?: emptyList()
            val endOfPaginationReached = response.body()?.next == null

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    remoteKeyDao.clearAll()
                    bookDao.clearAllBooks()
                }

                val nextPage = response.body()?.next?.let { nextUrl ->
                    val pageParam = nextUrl.substringAfter("page=", "")
                    if (pageParam.isNotEmpty()) pageParam.toIntOrNull() else null
                }

                val remoteKeys = books.map { book ->
                    RemoteKeyEntity(
                        bookId = book.id,
                        prevKey = if (page == 1) null else page - 1,
                        nextKey = nextPage
                    )
                }
                remoteKeyDao.insertAll(remoteKeys)

                books.forEach { bookResponse ->
                    val bookEntity = bookResponse.toEntity()
                    val authorEntities = bookResponse.authors.map { it.toEntity() }
                    val translatorEntities = bookResponse.translators.map { it.toEntity() }

                    bookDao.insertBookWithRelations(
                        book = bookEntity,
                        authors = authorEntities,
                        translators = translatorEntities
                    )
                }
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}