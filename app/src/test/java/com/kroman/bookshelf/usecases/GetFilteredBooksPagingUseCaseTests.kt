package com.kroman.bookshelf.usecases

import androidx.paging.PagingData
import com.kroman.bookshelf.domain.model.BookFilters
import com.kroman.bookshelf.domain.model.BookItem
import com.kroman.bookshelf.domain.repositories.BooksRepository
import com.kroman.bookshelf.domain.usecases.GetFilteredBooksPagingUseCaseImpl
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.junit.Before
import org.junit.Test
import kotlin.test.assertSame

class GetFilteredBooksPagingUseCaseTests {

    private val repositoryMock: BooksRepository = mockk()

    private lateinit var useCase: GetFilteredBooksPagingUseCaseImpl

    @Before
    fun setUp() {
        useCase = GetFilteredBooksPagingUseCaseImpl(booksRepository = repositoryMock)
    }

    @Test
    fun `execute(filters) - delegates to repository and returns flow unchanged`() {
        val filters = BookFilters(searchQuery = "dickens")
        val expected: Flow<PagingData<BookItem>> = emptyFlow()
        every { repositoryMock.getFilteredBooksPaged(filters) } returns expected

        val actual = useCase.execute(filters)

        assertSame(expected, actual)
        verify(exactly = 1) { repositoryMock.getFilteredBooksPaged(filters) }
        confirmVerified(repositoryMock)
    }
}
