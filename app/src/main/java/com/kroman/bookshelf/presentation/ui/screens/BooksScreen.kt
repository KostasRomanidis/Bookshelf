package com.kroman.bookshelf.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kroman.bookshelf.domain.model.BookItem
import com.kroman.bookshelf.domain.model.PersonItem
import com.kroman.bookshelf.presentation.ui.components.BookTile
import com.kroman.bookshelf.presentation.ui.components.ErrorScreen
import com.kroman.bookshelf.presentation.ui.components.Loading
import com.kroman.bookshelf.presentation.viewmodels.BooksUiState
import com.kroman.bookshelf.presentation.viewmodels.BooksViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun BooksScreen(
    modifier: Modifier = Modifier,
    viewModel: BooksViewModel = koinViewModel(),
    onNavigateToDetails: (BookItem) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadBooks()
    }

    when (val state = uiState) {
        BooksUiState.Loading -> Loading()
        is BooksUiState.Success -> {
            BooksList(
                modifier = modifier,
                books = state.books,
                onNavigateToDetails = onNavigateToDetails,
            )
        }
        is BooksUiState.Error -> ErrorScreen(errorMessage = state.message)
    }
}

@Composable
private fun BooksList(
    modifier: Modifier = Modifier,
    books: List<BookItem>,
    onNavigateToDetails: (BookItem) -> Unit,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(books.size) { index ->
            BookTile(
                bookItem = books[index],
                onBookClicked = onNavigateToDetails,
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun BooksListPreview() {
    BooksList(
        modifier = Modifier,
        books = listOf(
            BookItem(
                id = 1,
                title = "Book Title",
                authors = listOf(
                    PersonItem(
                        name = "Author's Name",
                        yearOfBirth = 1984,
                        yearOfDeath = null
                    )
                ),
                subjects = emptyList(),
                languages = emptyList(),
                downloadCount = 100
            ),
            BookItem(
                id = 2,
                title = "Book Title",
                authors = listOf(
                    PersonItem(
                        name = "Author's Name",
                        yearOfBirth = 1984,
                        yearOfDeath = null
                    )
                ),
                subjects = emptyList(),
                languages = emptyList(),
                downloadCount = 100
            ),
            BookItem(
                id = 3,
                title = "Book Title",
                authors = listOf(
                    PersonItem(
                        name = "Author's Name",
                        yearOfBirth = 1984,
                        yearOfDeath = null
                    )
                ),
                subjects = emptyList(),
                languages = emptyList(),
                downloadCount = 100
            ),
        ),
        onNavigateToDetails = {}
    )
}
