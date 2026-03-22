package com.kroman.bookshelf.usecases

import androidx.paging.PagingData
import com.kroman.bookshelf.domain.model.BookItem
import com.kroman.bookshelf.domain.repositories.BooksRepository
import com.kroman.bookshelf.domain.usecases.GetFavoriteBooksPagingUseCaseImpl
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.junit.Before
import org.junit.Test
import kotlin.test.assertSame

class GetFavoriteBooksPagingUseCaseTests {

    private val repositoryMock: BooksRepository = mockk()

    private lateinit var useCase: GetFavoriteBooksPagingUseCaseImpl

    @Before
    fun setUp() {
        useCase = GetFavoriteBooksPagingUseCaseImpl(booksRepository = repositoryMock)
    }

    @Test
    fun `execute - delegates to repository and returns flow unchanged`() {
        val expected: Flow<PagingData<BookItem>> = emptyFlow()
        every { repositoryMock.getFavoriteBooksPaged() } returns expected

        val actual = useCase.execute()

        assertSame(expected, actual)
        verify(exactly = 1) { repositoryMock.getFavoriteBooksPaged() }
        confirmVerified(repositoryMock)
    }
}
