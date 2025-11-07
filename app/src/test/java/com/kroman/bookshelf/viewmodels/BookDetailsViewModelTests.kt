package com.kroman.bookshelf.viewmodels

import app.cash.turbine.test
import com.kroman.bookshelf.domain.data.BookItem
import com.kroman.bookshelf.domain.data.PersonItem
import com.kroman.bookshelf.domain.data.Result
import com.kroman.bookshelf.presentation.bookdetails.BookDetailsUiState
import com.kroman.bookshelf.presentation.bookdetails.BookDetailsViewModel
import com.kroman.bookshelf.usecases.GetBookDetailsUseCase
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
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
class BookDetailsViewModelTests {

    private val getBookDetailsUseCaseMock: GetBookDetailsUseCase = mockk()

    private val testDispatcher = StandardTestDispatcher()
    private val bookId = 42
    private lateinit var viewModel: BookDetailsViewModel

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
        viewModel = BookDetailsViewModel(
            bookId = bookId,
            getBookDetailsUseCase = getBookDetailsUseCaseMock,
            testDispatcher
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `initial state is Loading`() = runTest(testDispatcher) {
        val state = viewModel.uiState.value
        assertTrue(state is BookDetailsUiState.Loading)
    }

    @Test
    fun `test getBookDetails() - loading to Success`() = runTest(testDispatcher) {
        // given
        val expectedBook = book
        coEvery { getBookDetailsUseCaseMock.execute(bookId) } returns Result.Success(expectedBook)

        viewModel.uiState.test {
            assertIs<BookDetailsUiState.Loading>(awaitItem())

            // when
            viewModel.getBookDetails()

            // then
            with(awaitItem()) {
                assertIs<BookDetailsUiState.Success>(this)
                assertEquals(expectedBook, this.book)
            }

            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 1) { getBookDetailsUseCaseMock.execute(bookId) }
        confirmVerified(getBookDetailsUseCaseMock)
    }

    @Test
    fun `test getBookDetails() - emits error with message`() =
        runTest(testDispatcher) {
            // given
            val exception = IllegalStateException("Network down")
            coEvery { getBookDetailsUseCaseMock.execute(bookId) } returns Result.Error(exception)

            viewModel.uiState.test {
                assertTrue(awaitItem() is BookDetailsUiState.Loading)

                // when
                viewModel.getBookDetails()
                runCurrent()

                // then
                with(awaitItem()) {
                    assertIs<BookDetailsUiState.Error>(this)
                    assertEquals("Network down", this.message)
                }
                cancelAndIgnoreRemainingEvents()
            }

            coVerify(exactly = 1) { getBookDetailsUseCaseMock.execute(bookId) }
            confirmVerified(getBookDetailsUseCaseMock)
        }
}