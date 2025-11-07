package com.kroman.bookshelf.sources

import com.kroman.bookshelf.network.BookResponse
import com.kroman.bookshelf.network.BooksApi
import com.kroman.bookshelf.network.BooksResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class BooksRemoteSourceTests {

    private val booksApiMock: BooksApi = mockk(relaxed = true)

    private lateinit var remoteSource: BooksRemoteSourceImpl

    @Before
    fun setUp() {
        remoteSource = BooksRemoteSourceImpl(booksApi = booksApiMock)
    }

    @Test
    fun `test get() all books`() = runTest {
        // given
        val payload: BooksResponse = mockk()
        val success = Response.success(payload)
        coEvery { booksApiMock.getBooks() } returns success

        // when
        val result = remoteSource.get()

        // then
        assertTrue(result.isSuccessful)
        assertSame(payload, result.body())
        coVerify(exactly = 1) { booksApiMock.getBooks() }
        confirmVerified(booksApiMock)
    }

    @Test
    fun `test getBook(id)`() = runTest {
        // when
        val id = 42
        val payload: BookResponse = mockk()
        every { payload.id } returns id
        every { payload.title } returns "The Answer"
        val success = Response.success(payload)
        coEvery { booksApiMock.getBook(id) } returns success

        // when
        val result = booksApiMock.getBook(id)

        // then
        assertTrue(result.isSuccessful)
        assertEquals(id, result.body()?.id)
        assertEquals("The Answer", result.body()?.title)
        coVerify(exactly = 1) { booksApiMock.getBook(id) }
        confirmVerified(booksApiMock)
    }

    @Test
    fun `test get() error response`() = runTest {
        // given
        val errorBody = """{"error":"Internal"}"""
            .toResponseBody("application/json".toMediaType())
        val error = Response.error<BooksResponse>(500, errorBody)
        coEvery { booksApiMock.getBooks() } returns error

        // when
        val result = remoteSource.get()

        // then
        assertTrue(!result.isSuccessful)
        assertEquals(500, result.code())
        coVerify(exactly = 1) { booksApiMock.getBooks() }
        confirmVerified(booksApiMock)
    }

    @Test
    fun `test getBook(id) api throws exception`() = runTest {
        // given
        val id = 7
        val exception = java.io.IOException("unknown error")
        coEvery { booksApiMock.getBook(id) } throws exception

        try {
            // when
            remoteSource.getBook(id)
            assertTrue("Expected exception to be thrown", false)
        } catch (e: Exception) {
            assertSame(exception, e)
        }

        // then
        coVerify(exactly = 1) { booksApiMock.getBook(id) }
        confirmVerified(booksApiMock)
    }
}

