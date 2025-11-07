package com.kroman.bookshelf.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kroman.bookshelf.R
import com.kroman.bookshelf.domain.model.BookItem
import com.kroman.bookshelf.domain.model.PersonItem
import com.kroman.bookshelf.presentation.ui.components.ErrorScreen
import com.kroman.bookshelf.presentation.ui.components.Loading
import com.kroman.bookshelf.presentation.viewmodels.BookDetailsUiState
import com.kroman.bookshelf.presentation.viewmodels.BookDetailsViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun BookDetailsScreen(
    bookId: Int,
    viewModel: BookDetailsViewModel = koinViewModel(parameters = { parametersOf(bookId) })
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.getBookDetails()
    }

    when (val state = uiState) {
        BookDetailsUiState.Loading -> Loading()
        is BookDetailsUiState.Success -> BookDetails(item = state.book)
        is BookDetailsUiState.Error -> ErrorScreen(errorMessage = state.message)
    }
}


@Composable
fun BookDetails(item: BookItem) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(color = MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(size = 12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
        ) {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(all = 16.dp)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (item.authors.isNotEmpty()) item.authors[0].name else stringResource(R.string.book_author_unknown),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 16.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.book_author_lifespan)
                    )
                    Text(
                        text = "${item.authors[0].yearOfBirth} - ${item.authors[0].yearOfDeath}"
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.book_primary_subject)
                    )

                    Text(
                        modifier = Modifier,
                        text = item.subjects[2],
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BookDetailsScreenPreview() {
    BookDetails(
        item = BookItem(
            id = 84,
            title = "Frankenstein; Or, The Modern Prometheus",
            authors = listOf(
                PersonItem(
                    name = "Shelley, Mary Wollstonecraft",
                    yearOfBirth = 1797,
                    yearOfDeath = 1851
                )
            ),
            subjects = listOf(
                "Frankenstein's monster (Fictitious character) -- Fiction",
                "Frankenstein, Victor (Fictitious character) -- Fiction",
                "Gothic fiction",
                "Horror tales",
                "Monsters -- Fiction",
                "Science fiction",
                "Scientists -- Fiction"
            ),
            languages = listOf(),
            downloadCount = 195270,
        )
    )
}
