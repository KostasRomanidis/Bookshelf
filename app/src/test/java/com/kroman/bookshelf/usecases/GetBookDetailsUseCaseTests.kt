package com.kroman.bookshelf.usecases

import com.kroman.bookshelf.domain.data.BookItem
import com.kroman.bookshelf.repositories.BooksRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertSame
import com.kroman.bookshelf.domain.data.Result

class GetBookDetailsUseCaseTests {

    private val repositoryMock: BooksRepository = mockk()

    private lateinit var useCase: GetBookDetailsUseCaseImpl

    @Before
    fun setUp() {
        useCase = GetBookDetailsUseCaseImpl(booksRepository = repositoryMock)
    }

    @Test
    fun `execute(id) - delegates with correct id and returns Success unchanged`() = runTest {
        // Arrange
        val id = 42
        val data = BookItem(
            id = id,
            title = "The Answer",
            authors = emptyList(),
            subjects = emptyList(),
            languages = listOf("en"),
            downloadCount = 4242
        )
        val expected: Result<BookItem> = Result.Success(data)
        coEvery { repositoryMock.getBook(id) } returns expected

        // Act
        val actual = useCase.execute(id)

        // Assert
        assertSame(expected, actual)
        coVerify(exactly = 1) { repositoryMock.getBook(id) }
        confirmVerified(repositoryMock)
    }

    @Test
    fun `execute(id) - delegates with correct id and returns Error unchanged`() = runTest {
        // given
        val id = 7
        val error = NoSuchElementException("not found")
        val expected: Result<BookItem> = Result.Error(exception = error)
        coEvery { repositoryMock.getBook(id) } returns expected

        // when
        val actual = useCase.execute(id)

        // then
        assertSame(expected, actual)
        coVerify(exactly = 1) { repositoryMock.getBook(id) }
        confirmVerified(repositoryMock)
    }
}