package com.kroman.bookshelf.viewmodels

import app.cash.turbine.test
import com.kroman.bookshelf.domain.model.BookItem
import com.kroman.bookshelf.domain.model.PersonItem
import com.kroman.bookshelf.domain.model.Result
import com.kroman.bookshelf.domain.usecases.GetBookDetailsUseCase
import com.kroman.bookshelf.domain.usecases.ObserveIsFavoriteUseCase
import com.kroman.bookshelf.domain.usecases.ToggleFavoriteBookUseCase
import com.kroman.bookshelf.presentation.viewmodels.BookDetailsUiState
import com.kroman.bookshelf.presentation.viewmodels.BookDetailsViewModel
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class BookDetailsViewModelTests {

    private val getBookDetailsUseCase: GetBookDetailsUseCase = mockk()
    private val toggleFavoriteBookUseCase: ToggleFavoriteBookUseCase = mockk()
    private val observeIsFavoriteUseCase: ObserveIsFavoriteUseCase = mockk()

    private val testDispatcher = StandardTestDispatcher()
    private val bookId = 42
    private lateinit var viewModel: BookDetailsViewModel
    private lateinit var favoriteFlow: MutableStateFlow<Boolean>

    private val book = BookItem(
        id = bookId,
        title = "The Answer",
        authors = listOf(
            PersonItem(
                name = "Douglas Adams",
                yearOfBirth = 1952,
                yearOfDeath = 2001
            )
        ),
        subjects = listOf("Humor"),
        languages = listOf("en"),
        downloadCount = 4242
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        favoriteFlow = MutableStateFlow(false)
        every { observeIsFavoriteUseCase.execute(bookId) } returns favoriteFlow
        coEvery { toggleFavoriteBookUseCase.execute(bookId) } returns Unit

        viewModel = BookDetailsViewModel(
            bookId = bookId,
            getBookDetailsUseCase = getBookDetailsUseCase,
            toggleFavoriteBookUseCase = toggleFavoriteBookUseCase,
            observeIsFavoriteUseCase = observeIsFavoriteUseCase,
            dispatcher = testDispatcher
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `initial state is loading and favorite false`() = runTest(testDispatcher) {
        runCurrent()
        assertIs<BookDetailsUiState.Loading>(viewModel.uiState.value)
        assertEquals(false, viewModel.isFavorite.value)
    }

    @Test
    fun `getBookDetails emits success`() = runTest(testDispatcher) {
        coEvery { getBookDetailsUseCase.execute(bookId) } returns Result.Success(book)

        viewModel.uiState.test {
            assertIs<BookDetailsUiState.Loading>(awaitItem())
            viewModel.getBookDetails()

            val success = awaitItem()
            assertIs<BookDetailsUiState.Success>(success)
            assertEquals(book, success.book)
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 1) { getBookDetailsUseCase.execute(bookId) }
    }

    @Test
    fun `getBookDetails emits error with exception message`() = runTest(testDispatcher) {
        coEvery { getBookDetailsUseCase.execute(bookId) } returns Result.Error(
            IllegalStateException("Network down")
        )

        viewModel.uiState.test {
            assertIs<BookDetailsUiState.Loading>(awaitItem())
            viewModel.getBookDetails()

            val error = awaitItem()
            assertIs<BookDetailsUiState.Error>(error)
            assertEquals("Network down", error.message)
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 1) { getBookDetailsUseCase.execute(bookId) }
    }

    @Test
    fun `favorite observer updates isFavorite state`() = runTest(testDispatcher) {
        runCurrent()
        assertEquals(false, viewModel.isFavorite.value)

        favoriteFlow.value = true
        runCurrent()

        assertEquals(true, viewModel.isFavorite.value)
        verify(exactly = 1) { observeIsFavoriteUseCase.execute(bookId) }
    }

    @Test
    fun `toggleFavorite delegates to use case`() = runTest(testDispatcher) {
        viewModel.toggleFavorite()
        runCurrent()

        coVerify(exactly = 1) { toggleFavoriteBookUseCase.execute(bookId) }
        confirmVerified(toggleFavoriteBookUseCase)
    }
}
