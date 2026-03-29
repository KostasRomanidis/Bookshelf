package com.kroman.bookshelf.usecases

import com.kroman.bookshelf.domain.model.BookItem
import com.kroman.bookshelf.domain.repositories.BooksRepository
import com.kroman.bookshelf.domain.usecases.GetCuratedPickUseCase
import com.kroman.bookshelf.domain.usecases.GetCuratedPickUseCaseImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetCuratedPickUseCaseTests {

    private val repositoryMock: BooksRepository = mockk()

    private lateinit var useCase: GetCuratedPickUseCase

    @Before
    fun setUp() {
        useCase = GetCuratedPickUseCaseImpl(repositoryMock)
    }

    @Test
    fun `execute - caches first non-null repository result for the session`() = runTest {
        val expected = BookItem(
            id = 5,
            title = "Dracula",
            authors = emptyList(),
            subjects = listOf("Vampires"),
            languages = listOf("en"),
            downloadCount = 123
        )
        coEvery { repositoryMock.getRandomLocalBook() } returns expected

        val first = useCase.execute()
        val second = useCase.execute()

        assertEquals(expected, first)
        assertEquals(expected, second)
        coVerify(exactly = 1) { repositoryMock.getRandomLocalBook() }
        confirmVerified(repositoryMock)
    }

    @Test
    fun `execute - caches null result and does not re-query repository`() = runTest {
        coEvery { repositoryMock.getRandomLocalBook() } returns null

        val first = useCase.execute()
        val second = useCase.execute()

        assertNull(first)
        assertNull(second)
        coVerify(exactly = 1) { repositoryMock.getRandomLocalBook() }
        confirmVerified(repositoryMock)
    }
}
