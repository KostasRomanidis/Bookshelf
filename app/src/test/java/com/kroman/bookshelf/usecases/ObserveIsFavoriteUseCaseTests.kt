package com.kroman.bookshelf.usecases

import com.kroman.bookshelf.domain.repositories.BooksRepository
import com.kroman.bookshelf.domain.usecases.ObserveIsFavoriteUseCaseImpl
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.junit.Before
import org.junit.Test
import kotlin.test.assertSame

class ObserveIsFavoriteUseCaseTests {

    private val repositoryMock: BooksRepository = mockk()

    private lateinit var useCase: ObserveIsFavoriteUseCaseImpl

    @Before
    fun setUp() {
        useCase = ObserveIsFavoriteUseCaseImpl(booksRepository = repositoryMock)
    }

    @Test
    fun `execute(bookId) - delegates to repository and returns flow unchanged`() {
        val bookId = 42
        val expected: Flow<Boolean> = emptyFlow()
        every { repositoryMock.observeIsFavorite(bookId) } returns expected

        val actual = useCase.execute(bookId)

        assertSame(expected, actual)
        verify(exactly = 1) { repositoryMock.observeIsFavorite(bookId) }
        confirmVerified(repositoryMock)
    }
}
