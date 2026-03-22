package com.kroman.bookshelf.viewmodels

import androidx.paging.PagingData
import app.cash.turbine.test
import com.kroman.bookshelf.domain.model.BookFilters
import com.kroman.bookshelf.domain.model.BookFormatFilter
import com.kroman.bookshelf.domain.model.BookSortOption
import com.kroman.bookshelf.domain.usecases.GetFilteredBooksPagingUseCase
import com.kroman.bookshelf.domain.usecases.ToggleFavoriteBookUseCase
import com.kroman.bookshelf.presentation.viewmodels.BooksViewModel
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class BooksViewModelTests {

    private val getFilteredBooksPagingUseCase: GetFilteredBooksPagingUseCase = mockk()
    private val toggleFavoriteBookUseCase: ToggleFavoriteBookUseCase = mockk()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: BooksViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { getFilteredBooksPagingUseCase.execute(any()) } returns flowOf(PagingData.empty())
        coEvery { toggleFavoriteBookUseCase.execute(any()) } returns Unit
        viewModel = BooksViewModel(
            getFilteredBooksPagingUseCase = getFilteredBooksPagingUseCase,
            toggleFavoriteBookUseCase = toggleFavoriteBookUseCase,
            dispatcher = testDispatcher
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `initial filters are default`() = runTest(testDispatcher) {
        assertEquals(BookFilters(), viewModel.filters.value)
    }

    @Test
    fun `filter actions update state`() = runTest(testDispatcher) {
        viewModel.updateSearchQuery("cities")
        viewModel.selectLanguage("en")
        viewModel.selectFormat(BookFormatFilter.EPUB)
        viewModel.selectSort(BookSortOption.DOWNLOAD_COUNT_DESC)

        assertEquals(
            BookFilters(
                searchQuery = "cities",
                language = "en",
                format = BookFormatFilter.EPUB,
                sort = BookSortOption.DOWNLOAD_COUNT_DESC
            ),
            viewModel.filters.value
        )

        viewModel.clearFilters()
        assertEquals(BookFilters(), viewModel.filters.value)
    }

    @Test
    fun `books flow re-queries use case when filters change`() = runTest(testDispatcher) {
        val collectJob = launch {
            viewModel.books.test {
                awaitItem()
                cancelAndIgnoreRemainingEvents()
            }
        }
        runCurrent()
        verify(exactly = 1) { getFilteredBooksPagingUseCase.execute(BookFilters()) }

        viewModel.updateSearchQuery("dickens")
        val secondCollectJob = launch {
            viewModel.books.test {
                awaitItem()
                cancelAndIgnoreRemainingEvents()
            }
        }
        runCurrent()

        verify(exactly = 1) {
            getFilteredBooksPagingUseCase.execute(
                match { it.searchQuery == "dickens" }
            )
        }

        collectJob.cancel()
        secondCollectJob.cancel()
    }

    @Test
    fun `toggleFavorite delegates to use case`() = runTest(testDispatcher) {
        viewModel.toggleFavorite(9)
        runCurrent()

        coVerify(exactly = 1) { toggleFavoriteBookUseCase.execute(9) }
    }
}
