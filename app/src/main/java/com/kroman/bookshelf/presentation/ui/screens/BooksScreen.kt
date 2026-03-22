package com.kroman.bookshelf.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.kroman.bookshelf.domain.model.BookItem
import com.kroman.bookshelf.domain.model.BookFilters
import com.kroman.bookshelf.domain.model.PersonItem
import com.kroman.bookshelf.presentation.ui.components.BookTile
import com.kroman.bookshelf.presentation.ui.components.BooksFilterSheet
import com.kroman.bookshelf.presentation.ui.components.Loading
import com.kroman.bookshelf.presentation.viewmodels.BooksViewModel
import kotlinx.coroutines.flow.flowOf
import org.koin.androidx.compose.koinViewModel
import com.kroman.bookshelf.R

@Composable
fun BooksScreen(
    modifier: Modifier = Modifier,
    viewModel: BooksViewModel = koinViewModel(),
    onNavigateToDetails: (BookItem) -> Unit,
) {
    val pagedBooks = viewModel.books.collectAsLazyPagingItems()
    val filters by viewModel.filters.collectAsState()
    var showFilters by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        Button(
            onClick = { showFilters = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(text = stringResource(R.string.filters_button))
        }

        BooksList(
            modifier = Modifier.weight(1f),
            pagedBooks = pagedBooks,
            onNavigateToDetails = onNavigateToDetails,
            onToggleFavorite = viewModel::toggleFavorite,
        )
    }

    if (showFilters) {
        BooksFilterSheet(
            filters = filters,
            onDismiss = { showFilters = false },
            onApply = { updated ->
                applyFilters(viewModel = viewModel, filters = updated)
                showFilters = false
            },
            onClear = viewModel::clearFilters
        )
    }
}

private fun applyFilters(
    viewModel: BooksViewModel,
    filters: BookFilters
) {
    viewModel.updateSearchQuery(filters.searchQuery)
    viewModel.selectLanguage(filters.language)
    viewModel.selectFormat(filters.format)
    viewModel.selectSort(filters.sort)
}

@Composable
private fun BooksList(
    modifier: Modifier = Modifier,
    pagedBooks: LazyPagingItems<BookItem>,
    onNavigateToDetails: (BookItem) -> Unit,
    onToggleFavorite: (Int) -> Unit,
) {
    val lazyListState = rememberLazyListState()
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        state = lazyListState,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            count = pagedBooks.itemCount, key = { index -> pagedBooks[index]?.id ?: index }
        ) { index ->
            val book = pagedBooks[index]
            book?.let {
                BookTile(
                    bookItem = it,
                    onBookClicked = onNavigateToDetails,
                    onToggleFavorite = onToggleFavorite,
                )
            }
        }
        pagedBooks.apply {
            when {
                loadState.refresh is LoadState.Loading -> {
                    item {
                        Loading()
                    }
                }

                loadState.append is LoadState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                loadState.refresh is LoadState.Error -> {
                    val e = loadState.refresh as LoadState.Error
                    item {
                        Column(
                            modifier = Modifier
                                .fillParentMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("Error: ${e.error.message}")
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { pagedBooks.retry() }) {
                                Text("Retry")
                            }
                        }
                    }
                }

                loadState.append is LoadState.Error -> {
                    val e = loadState.append as LoadState.Error
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Error loading more: ${e.error.message}")
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { pagedBooks.retry() }) {
                                Text("Retry")
                            }
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun BooksListPreview() {
    val books = listOf(
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
    )
    val pagingFlow = flowOf(PagingData.from(books))
    val lazyPagingItems = pagingFlow.collectAsLazyPagingItems()
    BooksList(
        modifier = Modifier,
        pagedBooks = lazyPagingItems,
        onNavigateToDetails = {},
        onToggleFavorite = {}
    )
}
