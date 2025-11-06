package com.kroman.bookshelf.presentation.books

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kroman.bookshelf.R
import com.kroman.bookshelf.domain.data.BookItem
import com.kroman.bookshelf.domain.data.PersonItem
import org.koin.androidx.compose.koinViewModel

@Composable
fun BooksScreen(
    modifier: Modifier = Modifier,
    viewModel: BooksViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    when (val state = uiState) {
        is BooksUiState.Success -> {
            BooksList(modifier = modifier, books = state.books)
        }

        BooksUiState.Loading -> Loading()
        is BooksUiState.Error -> ErrorScreen(errorMessage = state.message)
    }
}

@Composable
private fun Loading() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.testTag("loading"))
    }
}

@Composable
private fun ErrorScreen(errorMessage: String) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier.size(size = 48.dp),
                painter = painterResource(id = R.drawable.warning_icon),
                contentDescription = "Warning Icon",
                tint = Color.Red
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = errorMessage, color = Color.Red, fontSize = 16.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorScreenPreview() {
    ErrorScreen(errorMessage = "Something went wrong")
}


@Composable
private fun BooksList(
    modifier: Modifier = Modifier,
    books: List<BookItem>
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(books.size) { index ->
            BookListItem(bookItem = books[index])
        }
    }
}

@Composable
private fun BookListItem(
    bookItem: BookItem
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = bookItem.title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (bookItem.authors.isNotEmpty()) bookItem.authors[0].name else "Unknown",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 20.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// previews

@Preview(showBackground = true)
@Composable
private fun BookListItemPreview() {
    BookListItem(
        BookItem(
            id = 10,
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
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun BooksListPreview() {
    BooksList(
        modifier = Modifier,
        listOf(
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
            BookItem(
                id = 4,
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
            )
        )
    )
}


