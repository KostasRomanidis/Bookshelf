package com.kroman.bookshelf.viewmodels

import app.cash.turbine.test
import com.kroman.bookshelf.domain.data.BookItem
import com.kroman.bookshelf.presentation.books.BooksUiState
import com.kroman.bookshelf.presentation.books.BooksViewModel
import com.kroman.bookshelf.usecases.GetBooksUseCase
import com.kroman.bookshelf.domain.data.Result
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class BooksViewModelTests {
    private val getBooksUseCaseMock: GetBooksUseCase = mockk()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: BooksViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = BooksViewModel(getBooksUseCaseMock, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `test initial state`() = runTest(testDispatcher) {
        // when
        viewModel.uiState.test {
            // then
            assertIs<BooksUiState.Loading>(awaitItem())
        }
    }

    @Test
    fun `test loadBooks() on success result`() =
        runTest(testDispatcher) {
            // given
            val books = listOf(
                BookItem(
                    id = 1,
                    title = "Book 1",
                    authors = emptyList(),
                    subjects = emptyList(),
                    languages = listOf("en"),
                    downloadCount = 10
                ),
                BookItem(
                    id = 2,
                    title = "Book 2",
                    authors = emptyList(),
                    subjects = emptyList(),
                    languages = listOf("en"),
                    downloadCount = 5
                )
            )
            coEvery { getBooksUseCaseMock.execute() } returns Result.Success(books)

            // when
            viewModel.loadBooks()
            runCurrent()

            // then
            viewModel.uiState.test {
                with(awaitItem()) {
                    assertIs<BooksUiState.Success>(this)
                    assertEquals(books, this.books)
                }
            }
        }

    @Test
    fun `test loadBooks() on error`() = runTest {
        // given
        val exception = IllegalStateException("Network failure")
        coEvery { getBooksUseCaseMock.execute() } returns Result.Error(exception = exception)

        // when
        viewModel.loadBooks()
        runCurrent()

        viewModel.uiState.test {
            // then
            with(awaitItem()) {
                assertIs<BooksUiState.Error>(this)
                assertEquals("Network failure", this.message)
            }
        }
    }

    @Test
    fun `test loadBooks() on loading to success`() = runTest {
        // given
        val bookList = listOf(BookItem(1, "One", emptyList(), emptyList(), listOf("en"), 1))
        coEvery { getBooksUseCaseMock.execute() } returns Result.Success(bookList)
        viewModel = BooksViewModel(getBooksUseCaseMock, testDispatcher)

        viewModel.uiState.test {
            with(awaitItem()) {
                assertIs<BooksUiState.Loading>(this)
            }

            // when
            viewModel.loadBooks()
            runCurrent()

            // then
            with(awaitItem()) {
                assertIs<BooksUiState.Success>(this)
                assertEquals(bookList, this.books)
            }
            cancelAndIgnoreRemainingEvents()
        }
    }
}