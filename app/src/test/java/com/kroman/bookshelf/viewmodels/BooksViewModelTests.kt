package com.kroman.bookshelf.viewmodels

import androidx.paging.PagingData
import app.cash.turbine.test
import com.kroman.bookshelf.domain.model.BookFilters
import com.kroman.bookshelf.domain.model.BookFormatFilter
import com.kroman.bookshelf.domain.model.BookItem
import com.kroman.bookshelf.domain.model.BookSortOption
import com.kroman.bookshelf.domain.usecases.GetCuratedPickUseCase
import com.kroman.bookshelf.domain.usecases.GetFilteredBooksPagingUseCase
import com.kroman.bookshelf.domain.usecases.ToggleFavoriteBookUseCase
import com.kroman.bookshelf.presentation.viewmodels.BooksViewModel
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
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
    private val getCuratedPickUseCase: GetCuratedPickUseCase = mockk()
    private val toggleFavoriteBookUseCase: ToggleFavoriteBookUseCase = mockk()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: BooksViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { getFilteredBooksPagingUseCase.execute(any()) } returns flowOf(PagingData.empty())
        coEvery { getCuratedPickUseCase.execute() } returns null
        coEvery { toggleFavoriteBookUseCase.execute(any()) } returns Unit
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `initial filters are default`() = runTest(testDispatcher) {
        initViewModel()
        assertEquals(BookFilters(), viewModel.filters.value)
    }

    @Test
    fun `init loads curated pick into ui state`() = runTest(testDispatcher) {
        val curatedPick = BookItem(
            id = 3,
            title = "Emma",
            authors = emptyList(),
            subjects = emptyList(),
            languages = listOf("en"),
            downloadCount = 88
        )
        coEvery { getCuratedPickUseCase.execute() } returns curatedPick
        initViewModel()

        runCurrent()

        assertEquals(curatedPick, viewModel.uiState.value.curatedPick)
        coVerify(exactly = 1) { getCuratedPickUseCase.execute() }
    }

    @Test
    fun `filter actions update state`() = runTest(testDispatcher) {
        initViewModel()
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
        val capturedFilters = mutableListOf<BookFilters>()
        every {
            getFilteredBooksPagingUseCase.execute(any())
        } answers {
            capturedFilters += invocation.args[0] as BookFilters
            flowOf(PagingData.empty())
        }

        initViewModel()
        val collectJob = launch {
            viewModel.books.test {
                awaitItem()
                cancelAndIgnoreRemainingEvents()
            }
        }
        runCurrent()

        viewModel.updateSearchQuery("dickens")
        val secondCollectJob = launch {
            viewModel.books.test {
                awaitItem()
                cancelAndIgnoreRemainingEvents()
            }
        }
        runCurrent()

        assertEquals(
            listOf(
                BookFilters(),
                BookFilters(searchQuery = "dickens")
            ),
            capturedFilters
        )

        collectJob.cancel()
        secondCollectJob.cancel()
    }

    @Test
    fun `toggleFavorite delegates to use case`() = runTest(testDispatcher) {
        initViewModel()
        viewModel.toggleFavorite(9)
        runCurrent()

        coVerify(exactly = 1) { toggleFavoriteBookUseCase.execute(9) }
    }

    private fun initViewModel() {
        viewModel = BooksViewModel(
            getFilteredBooksPagingUseCase = getFilteredBooksPagingUseCase,
            getCuratedPickUseCase = getCuratedPickUseCase,
            toggleFavoriteBookUseCase = toggleFavoriteBookUseCase,
            dispatcher = testDispatcher
        )
    }
}
