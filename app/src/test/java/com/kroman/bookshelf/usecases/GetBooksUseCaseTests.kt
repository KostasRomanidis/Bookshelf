package com.kroman.bookshelf.usecases

import com.kroman.bookshelf.domain.data.BookItem
import com.kroman.bookshelf.domain.data.Result
import com.kroman.bookshelf.repositories.BooksRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test

class GetBooksUseCaseTests {

    private val repositoryMock: BooksRepository = mockk()

    private lateinit var useCase: GetBooksUseCase

    @Before
    fun setUp() {
        useCase = GetBooksUseCaseImpl(repositoryMock)
    }

    @Test
    fun `execute - delegates to repository and returns Success unchanged`() = runTest {
        // Arrange
        val data = listOf(
            BookItem(
                id = 1,
                title = "Alpha",
                authors = emptyList(),
                subjects = emptyList(),
                languages = listOf("en"),
                downloadCount = 1
            )
        )
        val expected: Result<List<BookItem>> = Result.Success(data)
        coEvery { repositoryMock.getBooks() } returns expected

        // Act
        val actual = useCase.execute()

        // Assert
        assertSame(expected, actual) // exact instance propagation
        coVerify(exactly = 1) { repositoryMock.getBooks() }
        confirmVerified(repositoryMock)
    }

    @Test
    fun `execute - delegates to repository and returns Error unchanged`() = runTest {
        // Arrange
        val error = IllegalStateException("boom")
        val expected: Result<List<BookItem>> = Result.Error(exception = error)
        coEvery { repositoryMock.getBooks() } returns expected

        // Act
        val actual = useCase.execute()

        // Assert
        assertSame(expected, actual)
        coVerify(exactly = 1) { repositoryMock.getBooks() }
        confirmVerified(repositoryMock)
    }
    // endregion
}