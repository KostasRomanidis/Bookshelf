package com.kroman.bookshelf.repositories

import com.kroman.bookshelf.domain.data.BookItem
import com.kroman.bookshelf.domain.data.PersonItem
import com.kroman.bookshelf.domain.data.Result
import com.kroman.bookshelf.network.BookResponse
import com.kroman.bookshelf.network.BooksResponse
import com.kroman.bookshelf.network.PersonResponse
import com.kroman.bookshelf.sources.BooksRemoteSource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class BooksRepositoryTests {

    private val remoteDataSource: BooksRemoteSource = mockk()

    private lateinit var bookRepository: BooksRepositoryImpl

    @Before
    fun setUp() {
        bookRepository = BooksRepositoryImpl(booksRemoteDataSource = remoteDataSource)
    }

    private val author = PersonResponse(name = "Author", yearOfBirth = 1900, yearOfDeath = null)
    private val books = BookResponse(
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
        // given
        val dtoList = listOf(books)
        val response = Response.success(
            BooksResponse(results = dtoList, next = null, previous = null, count = 1)
        )
        coEvery { remoteDataSource.get() } returns response

        // when
        val result = bookRepository.getBooks()

        // then
        assertTrue(result is Result.Success)
        result as Result.Success
        assertEquals(1, result.data.size)
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
            ), result.data[0]
        )
        coVerify(exactly = 1) { remoteDataSource.get() }
        confirmVerified(remoteDataSource)
    }

    @Test
    fun `getBooks - http error response produces Result_Error with HttpException`() = runTest {
        // given
        val err = """{"error":"boom"}""".toResponseBody("application/json".toMediaType())
        coEvery { remoteDataSource.get() } returns Response.error(500, err)

        // when
        val result = bookRepository.getBooks()

        // then
        assertTrue(result is Result.Error)
        val ex = (result as Result.Error).exception
        assertTrue(ex is HttpException)
        assertEquals(500, (ex as HttpException).code())
        coVerify(exactly = 1) { remoteDataSource.get() }
        confirmVerified(remoteDataSource)
    }

    @Test
    fun `getBooks - successful response with null body yields Result_Error NPE`() = runTest {
        // given
        @Suppress("UNCHECKED_CAST")
        val nullBodyResponse = Response.success<BooksResponse?>(null)
                as Response<BooksResponse>
        coEvery { remoteDataSource.get() } returns nullBodyResponse

        // when
        val result = bookRepository.getBooks()

        // then
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is NullPointerException)
        coVerify(exactly = 1) { remoteDataSource.get() }
        confirmVerified(remoteDataSource)
    }

    @Test
    fun `getBooks - IOException from data source mapped to Result_Error`() = runTest {
        // given
        coEvery { remoteDataSource.get() } throws IOException("network down")

        // when
        val result = bookRepository.getBooks()

        // then
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is IOException)
        coVerify(exactly = 1) { remoteDataSource.get() }
        confirmVerified(remoteDataSource)
    }

    @Test
    fun `getBooks - HttpException thrown mapped to Result_Error`() = runTest {
        // given
        val err = """{"e":"x"}""".toResponseBody("application/json".toMediaType())
        val http = HttpException(Response.error<Any>(404, err))
        coEvery { remoteDataSource.get() } throws http

        // when
        val result = bookRepository.getBooks()

        // then
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is HttpException)
        assertEquals(404, (result.exception as HttpException).code())
        coVerify(exactly = 1) { remoteDataSource.get() }
        confirmVerified(remoteDataSource)
    }
    // endregion

    // region getBook(id)

    @Test
    fun `getBook - success maps dto to domain`() = runTest {
        // given
        val dto = books
        coEvery { remoteDataSource.getBook(1) } returns Response.success(dto)

        // Act
        val result = bookRepository.getBook(1)

        // Assert
        assertTrue(result is Result.Success)
        val item = (result as Result.Success).data
        assertEquals(1, item.id)
        assertEquals("title", item.title)
        assertEquals(1, item.authors.size)
        assertEquals(listOf("Fiction"), item.subjects)
        assertEquals(listOf("en"), item.languages)
        assertEquals(10, item.downloadCount)
        coVerify(exactly = 1) { remoteDataSource.getBook(1) }
        confirmVerified(remoteDataSource)
    }

    @Test
    fun `getBook - http error response maps to Result_Error(HttpException)`() = runTest {
        // Arrange
        val err = """{"error":"not found"}""".toResponseBody("application/json".toMediaType())
        coEvery { remoteDataSource.getBook(10) } returns Response.error(404, err)

        // Act
        val result = bookRepository.getBook(10)

        // Assert
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is HttpException)
        assertEquals(404, (result.exception as HttpException).code())
        coVerify(exactly = 1) { remoteDataSource.getBook(10) }
        confirmVerified(remoteDataSource)
    }

    @Test
    fun `getBook - success with null body yields Result_Error NPE`() = runTest {
        // Arrange
        @Suppress("UNCHECKED_CAST")
        val nullBody = Response.success<BookResponse?>(null) as Response<BookResponse>
        coEvery { remoteDataSource.getBook(5) } returns nullBody

        // Act
        val result = bookRepository.getBook(5)

        // Assert
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is NullPointerException)
        coVerify(exactly = 1) { remoteDataSource.getBook(5) }
        confirmVerified(remoteDataSource)
    }

    @Test
    fun `getBook - IOException thrown mapped to Result_Error`() = runTest {
        coEvery { remoteDataSource.getBook(1) } throws IOException("timeout")
        val result = bookRepository.getBook(1)
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is IOException)
        coVerify(exactly = 1) { remoteDataSource.getBook(1) }
        confirmVerified(remoteDataSource)
    }

    @Test
    fun `getBook - HttpException thrown mapped to Result_Error`() = runTest {
        val err = """{"e":"x"}""".toResponseBody("application/json".toMediaType())
        val http = HttpException(Response.error<Any>(503, err))
        coEvery { remoteDataSource.getBook(2) } throws http

        val result = bookRepository.getBook(2)
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is HttpException)
        assertEquals(503, (result.exception as HttpException).code())
        coVerify(exactly = 1) { remoteDataSource.getBook(2) }
        confirmVerified(remoteDataSource)
    }
    // endregion

}