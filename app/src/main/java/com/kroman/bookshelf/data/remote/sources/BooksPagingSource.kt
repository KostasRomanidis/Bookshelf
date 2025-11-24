package com.kroman.bookshelf.data.remote.sources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.kroman.bookshelf.data.remote.responses.BookResponse
import retrofit2.HttpException
import java.io.IOException

class BooksPagingSource(
    private val booksRemoteSource: BooksRemoteSource
): PagingSource<Int, BookResponse>() {
    override fun getRefreshKey(state: PagingState<Int, BookResponse>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BookResponse> {
        return try {
            val page = params.key ?: 1
            val response = booksRemoteSource.get(page = page)
            if (response.isSuccessful) {
                val body = response.body()
                val books = body?.results ?: emptyList()

                val nextPage = body?.next?.let { nextUrl ->
                    val pageParam = nextUrl.substringAfter("page=", "")
                    if (pageParam.isNotEmpty()) pageParam.toIntOrNull() else null
                }

                LoadResult.Page(
                    data = books,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = nextPage
                )
            } else {
                LoadResult.Error(HttpException(response))
            }
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }    }
}