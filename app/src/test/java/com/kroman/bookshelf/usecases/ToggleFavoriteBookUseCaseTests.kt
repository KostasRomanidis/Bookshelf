package com.kroman.bookshelf.usecases

import com.kroman.bookshelf.domain.repositories.BooksRepository
import com.kroman.bookshelf.domain.usecases.ToggleFavoriteBookUseCaseImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ToggleFavoriteBookUseCaseTests {

    private val repositoryMock: BooksRepository = mockk()

    private lateinit var useCase: ToggleFavoriteBookUseCaseImpl

    @Before
    fun setUp() {
        useCase = ToggleFavoriteBookUseCaseImpl(booksRepository = repositoryMock)
    }

    @Test
    fun `execute(bookId) - delegates to repository`() = runTest {
        val bookId = 7
        coEvery { repositoryMock.toggleFavorite(bookId) } returns Unit

        useCase.execute(bookId)

        coVerify(exactly = 1) { repositoryMock.toggleFavorite(bookId) }
        confirmVerified(repositoryMock)
    }
}
