package com.kroman.bookshelf.repositories

import com.kroman.bookshelf.data.local.dao.BookDao
import com.kroman.bookshelf.data.local.db.BookshelfDatabase
import com.kroman.bookshelf.data.local.sources.BooksLocalSource
import com.kroman.bookshelf.data.remote.responses.BookResponse
import com.kroman.bookshelf.data.remote.responses.BooksResponse
import com.kroman.bookshelf.data.remote.responses.PersonResponse
import com.kroman.bookshelf.data.remote.sources.BooksRemoteSource
import com.kroman.bookshelf.data.repositories.BooksRepositoryImpl
import com.kroman.bookshelf.domain.model.BookItem
import com.kroman.bookshelf.domain.model.PersonItem
import com.kroman.bookshelf.domain.model.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.just
import io.mockk.Runs
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class BooksRepositoryTests {

    private val booksLocalSource: BooksLocalSource = mockk()
    private val remoteDataSource: BooksRemoteSource = mockk()
    private val bookshelfDatabase: BookshelfDatabase = mockk()
    private val bookDao: BookDao = mockk()

    private lateinit var bookRepository: BooksRepositoryImpl

    @Before
    fun setUp() {
        bookRepository = BooksRepositoryImpl(
            booksLocalSource = booksLocalSource,
            booksRemoteDataSource = remoteDataSource,
            bookshelfDatabase = bookshelfDatabase,
            bookDao = bookDao
        )
    }

    private val author = PersonResponse(name = "Author", yearOfBirth = 1900, yearOfDeath = null)
    private val bookResponse = BookResponse(
        id = 1,
        title = "title",
        authors = listOf(author),
        subjects = listOf("Fiction"),
        languages = listOf("en"),
        downloadCount = 10,
        translators = emptyList(),
    )

    @Test
    fun `getBooks - success maps list`() = runTest {
        val response = Response.success(
            BooksResponse(results = listOf(bookResponse), next = null, previous = null, count = 1)
        )
        coEvery { remoteDataSource.get() } returns response

        val result = bookRepository.getBooks()

        assertTrue(result is Result.Success)
        result as Result.Success
        assertEquals(
            BookItem(
                id = 1,
                title = "title",
                authors = listOf(
                    PersonItem(
                        name = "Author",
                        yearOfBirth = 1900,
                        yearOfDeath = null
                    )
                ),
                subjects = listOf("Fiction"),
                languages = listOf("en"),
                downloadCount = 10
            ),
            result.data[0]
        )
        coVerify(exactly = 1) { remoteDataSource.get() }
    }

    @Test
    fun `getBooks - http error response produces Result_Error with HttpException`() = runTest {
        val err = """{"error":"boom"}""".toResponseBody("application/json".toMediaType())
        coEvery { remoteDataSource.get() } returns Response.error(500, err)

        val result = bookRepository.getBooks()

        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is HttpException)
    }

    @Test
    fun `getBooks - IOException from data source mapped to Result_Error`() = runTest {
        coEvery { remoteDataSource.get() } throws IOException("network down")

        val result = bookRepository.getBooks()

        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is IOException)
    }

    @Test
    fun `getBook - returns cached book without remote call`() = runTest {
        val cachedBook = BookItem(
            id = 1,
            title = "cached",
            authors = emptyList(),
            subjects = emptyList(),
            languages = listOf("en"),
            downloadCount = 1
        )
        coEvery { booksLocalSource.getBook(1) } returns cachedBook

        val result = bookRepository.getBook(1)

        assertTrue(result is Result.Success)
        assertEquals(cachedBook, (result as Result.Success).data)
        coVerify(exactly = 1) { booksLocalSource.getBook(1) }
        coVerify(exactly = 0) { remoteDataSource.getBook(any()) }
    }

    @Test
    fun `getBook - fetches remote and returns saved local value when cache misses`() = runTest {
        val expected = BookItem(
            id = 1,
            title = "title",
            authors = emptyList(),
            subjects = listOf("Fiction"),
            languages = listOf("en"),
            downloadCount = 10
        )
        coEvery { booksLocalSource.getBook(1) } returnsMany listOf(null, expected)
        coEvery { remoteDataSource.getBook(1) } returns Response.success(bookResponse)
        coEvery { bookDao.insertBookWithRelations(any(), any(), any()) } just Runs

        val result = bookRepository.getBook(1)

        assertTrue(result is Result.Success)
        assertEquals(expected, (result as Result.Success).data)
        coVerify(exactly = 2) { booksLocalSource.getBook(1) }
        coVerify(exactly = 1) { remoteDataSource.getBook(1) }
        coVerify(exactly = 1) { bookDao.insertBookWithRelations(any(), any(), any()) }
    }

    @Test
    fun `getBook - remote non successful response maps to Result_Error with expected message`() =
        runTest {
            val err = """{"error":"not found"}""".toResponseBody("application/json".toMediaType())
            coEvery { booksLocalSource.getBook(10) } returns null
            coEvery { remoteDataSource.getBook(10) } returns Response.error(404, err)

            val result = bookRepository.getBook(10)

            assertTrue(result is Result.Error)
            assertEquals("Book not found", (result as Result.Error).exception.message)
        }

    @Test
    fun `getBook - successful response with null body maps to Result_Error`() = runTest {
        @Suppress("UNCHECKED_CAST")
        val nullBody = Response.success<BookResponse?>(null) as Response<BookResponse>
        coEvery { booksLocalSource.getBook(5) } returns null
        coEvery { remoteDataSource.getBook(5) } returns nullBody

        val result = bookRepository.getBook(5)

        assertTrue(result is Result.Error)
        assertEquals("Empty body", (result as Result.Error).exception.message)
    }

    @Test
    fun `getBook - missing local value after insert maps to Result_Error`() = runTest {
        coEvery { booksLocalSource.getBook(1) } returnsMany listOf(null, null)
        coEvery { remoteDataSource.getBook(1) } returns Response.success(bookResponse)
        coEvery { bookDao.insertBookWithRelations(any(), any(), any()) } just Runs

        val result = bookRepository.getBook(1)

        assertTrue(result is Result.Error)
        assertEquals("Book not found in local storage", (result as Result.Error).exception.message)
    }

    @Test
    fun `getBook - IOException thrown mapped to Result_Error`() = runTest {
        coEvery { booksLocalSource.getBook(1) } returns null
        coEvery { remoteDataSource.getBook(1) } throws IOException("timeout")

        val result = bookRepository.getBook(1)

        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is IOException)
    }

    @Test
    fun `getBook - HttpException thrown mapped to Result_Error`() = runTest {
        val err = """{"e":"x"}""".toResponseBody("application/json".toMediaType())
        val http = HttpException(Response.error<Any>(503, err))
        coEvery { booksLocalSource.getBook(2) } returns null
        coEvery { remoteDataSource.getBook(2) } throws http

        val result = bookRepository.getBook(2)

        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is HttpException)
    }
}
