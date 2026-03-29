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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.kroman.bookshelf.presentation.ui.components.BackToTopButton
import com.kroman.bookshelf.presentation.ui.components.BookTile
import com.kroman.bookshelf.presentation.ui.components.BooksFilterSheet
import com.kroman.bookshelf.presentation.ui.components.CuratedPickTile
import com.kroman.bookshelf.presentation.ui.components.Loading
import com.kroman.bookshelf.presentation.ui.theme.BookshelfTheme
import com.kroman.bookshelf.presentation.ui.theme.Dimens
import com.kroman.bookshelf.presentation.viewmodels.BooksViewModel
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
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
    val uiState by viewModel.uiState.collectAsState()
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
            headerContent = {
                val curatedPick = uiState.curatedPick
                if (curatedPick != null) {
                    Text(
                        text = stringResource(R.string.curated_picks_title),
                        modifier = Modifier.padding(bottom = Dimens.spaceMd),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    CuratedPickTile(
                        title = curatedPick.title,
                        author = curatedPick.primaryAuthor(
                            fallback = stringResource(R.string.book_author_unknown)
                        ),
                        coverImageUrl = curatedPick.coverImageUrl(),
                        onClick = { onNavigateToDetails(curatedPick) },
                        modifier = Modifier.padding(bottom = Dimens.spaceLg)
                    )
                }
            }
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
    headerContent: @Composable () -> Unit = {},
) {
    val lazyListState = rememberLazyListState()
    val showBackToTopButton by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0 || lazyListState.firstVisibleItemScrollOffset > 0
        }
    }
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                headerContent()
            }
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

        BackToTopButton(
            visible = showBackToTopButton,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            onClick = {
                coroutineScope.launch {
                    lazyListState.animateScrollToItem(index = 0)
                }
            }
        )
    }
}


@Preview(showBackground = true, widthDp = 412, heightDp = 915, name = "Books Screen")
@Composable
private fun BooksScreenPreview() {
    val books = listOf(
        BookItem(
            id = 1,
            title = "The Count of Monte Cristo",
            authors = listOf(
                PersonItem(
                    name = "Alexandre Dumas",
                    yearOfBirth = 1802,
                    yearOfDeath = 1870
                )
            ),
            subjects = listOf("Adventure stories", "Historical fiction"),
            languages = listOf("en"),
            downloadCount = 8421,
            formats = mapOf(
                "image/jpeg" to "https://www.gutenberg.org/cache/epub/1184/pg1184.cover.medium.jpg"
            )
        ),
        BookItem(
            id = 2,
            title = "Jane Eyre: An Autobiography",
            authors = listOf(
                PersonItem(
                    name = "Charlotte Bronte",
                    yearOfBirth = 1816,
                    yearOfDeath = 1855
                )
            ),
            subjects = listOf("Governesses", "Love stories"),
            languages = listOf("en"),
            downloadCount = 6150,
            isFavorite = true,
            formats = mapOf(
                "image/jpeg" to "https://www.gutenberg.org/cache/epub/1260/pg1260.cover.medium.jpg"
            )
        ),
        BookItem(
            id = 3,
            title = "The War of the Worlds",
            authors = listOf(
                PersonItem(
                    name = "H. G. Wells",
                    yearOfBirth = 1866,
                    yearOfDeath = 1946
                )
            ),
            subjects = listOf("Science fiction", "Mars"),
            languages = listOf("en"),
            downloadCount = 4912
        ),
        BookItem(
            id = 4,
            title = "A Room with a View",
            authors = listOf(
                PersonItem(
                    name = "E. M. Forster",
                    yearOfBirth = 1879,
                    yearOfDeath = 1970
                )
            ),
            subjects = listOf("Young women", "England"),
            languages = listOf("en"),
            downloadCount = 2187
        ),
    )
    val pagingFlow = flowOf(PagingData.from(books))
    val lazyPagingItems = pagingFlow.collectAsLazyPagingItems()
    BookshelfTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Button(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.spaceLg, vertical = Dimens.spaceSm)
                ) {
                    Text(text = stringResource(R.string.filters_button))
                }

                BooksList(
                    modifier = Modifier.weight(1f),
                    pagedBooks = lazyPagingItems,
                    onNavigateToDetails = {},
                    onToggleFavorite = {},
                    headerContent = {
                        Text(
                            text = stringResource(R.string.curated_picks_title),
                            modifier = Modifier.padding(bottom = Dimens.spaceMd),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        CuratedPickTile(
                            title = "Frankenstein; Or, The Modern Prometheus",
                            author = "Mary Wollstonecraft Shelley",
                            coverImageUrl = "https://www.gutenberg.org/cache/epub/84/pg84.cover.medium.jpg",
                            onClick = {},
                            modifier = Modifier.padding(bottom = Dimens.spaceLg)
                        )
                    }
                )
            }
        }
    }
}

private fun BookItem.primaryAuthor(fallback: String): String {
    return authors.firstOrNull()?.name?.takeIf { it.isNotBlank() } ?: fallback
}

private fun BookItem.coverImageUrl(): String? {
    return formats.entries.firstOrNull { (key, value) ->
        key.startsWith("image/jpeg") && value.isNotBlank()
    }?.value
}
