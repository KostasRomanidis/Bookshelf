package com.kroman.bookshelf.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.kroman.bookshelf.R
import com.kroman.bookshelf.domain.model.BookItem
import com.kroman.bookshelf.domain.model.PersonItem
import com.kroman.bookshelf.presentation.ui.components.ErrorScreen
import com.kroman.bookshelf.presentation.ui.components.LibrarianNoteSection
import com.kroman.bookshelf.presentation.ui.components.Loading
import com.kroman.bookshelf.presentation.ui.components.MetadataCard
import com.kroman.bookshelf.presentation.ui.theme.BookshelfTheme
import com.kroman.bookshelf.presentation.ui.theme.Dimens
import com.kroman.bookshelf.presentation.ui.theme.Radius
import com.kroman.bookshelf.presentation.viewmodels.BookDetailsUiState
import com.kroman.bookshelf.presentation.viewmodels.BookDetailsViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.text.NumberFormat

@Composable
fun BookDetailsScreen(
    bookId: Int,
    viewModel: BookDetailsViewModel = koinViewModel(parameters = { parametersOf(bookId) })
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isFavorite by viewModel.isFavorite.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.getBookDetails()
    }

    when (val state = uiState) {
        BookDetailsUiState.Loading -> Loading()
        is BookDetailsUiState.Success -> BookDetails(
            item = state.book,
            isFavorite = isFavorite,
            onToggleFavorite = viewModel::toggleFavorite
        )

        is BookDetailsUiState.Error -> ErrorScreen(errorMessage = state.message)
    }
}

@Composable
fun BookDetails(
    item: BookItem,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit
) {
    val summary = item.summaries.firstOrNull { it.isNotBlank() }
    val formats = rememberAvailableFormats(item.formats)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = Dimens.spaceLg),
        verticalArrangement = Arrangement.spacedBy(Dimens.spaceXl)
    ) {
        item {
            Spacer(modifier = Modifier.height(Dimens.spaceSm))
        }

        item {
            HeroSection(
                item = item,
                isFavorite = isFavorite,
                onToggleFavorite = onToggleFavorite
            )
        }

        item {
            TitleSection(
                item = item
            )
        }

        item {
            MetadataSection(item = item)
        }

        item {
            CopyrightSection(item = item)
        }

        item {
            Button(
                onClick = {},
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(Radius.lg),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                    disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_start_reading),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(Dimens.spaceSm))
                Text(
                    text = stringResource(R.string.book_start_reading),
                    modifier = Modifier.padding(vertical = Dimens.spaceXs),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                )
            }
        }

        if (item.subjects.isNotEmpty()) {
            item {
                SubjectsSection(subjects = item.subjects)
            }
        }

        summary?.let {
            item {
                SummarySection(summary = it)
            }
        }

        if (formats.isNotEmpty()) {
            item {
                FormatsSection(formats = formats)
            }
        }

        summary?.let {
            item {
                LibrarianNoteSection(summary = it)
            }
        }

        item {
            Spacer(modifier = Modifier.height(Dimens.spaceXl))
        }
    }
}

@Composable
private fun HeroSection(
    item: BookItem,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit
) {
    val coverUrl = item.coverImageUrl()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Radius.xl),
        color = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.spaceXl, vertical = Dimens.space2xl),
        ) {
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    painter = painterResource(
                        if (isFavorite) R.drawable.ic_add_to_favorite else R.drawable.ic_add_to_favorite
                    ),
                    contentDescription = if (isFavorite) {
                        stringResource(R.string.remove_from_favorites)
                    } else {
                        stringResource(R.string.add_to_favorites)
                    },
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Surface(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(0.62f)
                    .aspectRatio(0.72f)
                    .shadow(elevation = 10.dp, shape = RoundedCornerShape(Radius.md)),
                shape = RoundedCornerShape(Radius.md),
                color = MaterialTheme.colorScheme.primary
            ) {
                if (coverUrl != null) {
                    AsyncImage(
                        model = coverUrl,
                        contentDescription = item.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimens.spaceLg),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = item.primaryAuthor().uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.82f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            maxLines = 4,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = item.categoryLabel(stringResource(R.string.book_category_fallback)),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.82f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TitleSection(
    item: BookItem
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Dimens.spaceSm)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceSm)
        ) {
            Text(
                text = item.categoryLabel(stringResource(R.string.book_category_fallback))
                    .uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = item.title,
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = item.primaryAuthor(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun MetadataSection(item: BookItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMd)
    ) {
        MetadataCard(
            modifier = Modifier.weight(1f),
            iconRes = R.drawable.ic_download,
            label = stringResource(R.string.book_downloads),
            value = item.downloadCount.formatWithGrouping()
        )
        MetadataCard(
            modifier = Modifier.weight(1f),
            iconRes = R.drawable.ic_language,
            label = stringResource(R.string.book_language),
            value = item.languages.firstOrNull()?.uppercase()
                ?: stringResource(R.string.book_author_unknown)
        )
    }
}

@Composable
private fun CopyrightSection(item: BookItem) {
    val copyrightValue = when (item.copyright) {
        true -> stringResource(R.string.book_copyrighted)
        false -> stringResource(R.string.book_public_domain)
        null -> stringResource(R.string.book_rights_unknown)
    }

    MetadataCard(
        modifier = Modifier.fillMaxWidth(),
        iconRes = R.drawable.ic_copyright,
        label = stringResource(R.string.book_copyright_label),
        value = copyrightValue,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SubjectsSection(subjects: List<String>) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Dimens.spaceMd)
    ) {
        SectionTitle(title = stringResource(R.string.book_subjects))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSm),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceSm)
        ) {
            subjects.take(8).forEach { subject ->
                Surface(
                    shape = RoundedCornerShape(Radius.pill),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh
                ) {
                    Text(
                        text = subject,
                        modifier = Modifier.padding(
                            horizontal = Dimens.spaceMd,
                            vertical = Dimens.spaceSm
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun SummarySection(summary: String) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Dimens.spaceMd)
    ) {
        SectionTitle(title = stringResource(R.string.book_summary_title))
        Text(
            text = summary,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun FormatsSection(formats: List<BookFormatUi>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Radius.xl),
        color = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Column(
            modifier = Modifier.padding(Dimens.spaceLg),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceMd)
        ) {
            SectionTitle(title = stringResource(R.string.book_formats_title))
            formats.forEach { format ->
                Surface(
                    shape = RoundedCornerShape(Radius.md),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimens.spaceMd),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(Dimens.spaceXs)
                        ) {
                            Text(
                                text = format.label,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = format.metadata,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceContainerHigh
                        ) {
                            Text(
                                text = stringResource(R.string.book_format_action_label),
                                modifier = Modifier.padding(
                                    horizontal = Dimens.spaceSm,
                                    vertical = Dimens.spaceXs
                                ),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

private data class BookFormatUi(
    val label: String,
    val metadata: String
)

private fun BookItem.primaryAuthor(): String {
    return authors.firstOrNull()?.name ?: "Unknown"
}

private fun BookItem.categoryLabel(fallback: String): String {
    return bookshelves.firstOrNull()
        ?: subjects.firstOrNull()
        ?: fallback
}

private fun rememberAvailableFormats(formats: Map<String, String>): List<BookFormatUi> {
    return formats
        .filterValues { it.isNotBlank() }
        .entries
        .mapNotNull { (key, value) ->
            when {
                key.startsWith("application/epub+zip") -> BookFormatUi(
                    label = "EPUB",
                    metadata = value.formatHostLabel()
                )

                key.startsWith("text/html") -> BookFormatUi(
                    label = "HTML",
                    metadata = value.formatHostLabel()
                )

                key.startsWith("application/pdf") -> BookFormatUi(
                    label = "PDF",
                    metadata = value.formatHostLabel()
                )

                key.startsWith("text/plain") -> BookFormatUi(
                    label = "Plain Text",
                    metadata = value.formatHostLabel()
                )

                else -> null
            }
        }
        .distinctBy { it.label }
}

private fun String.formatHostLabel(): String {
    return substringAfter("://", this)
        .substringBefore('/')
        .ifBlank { this }
}

private fun BookItem.coverImageUrl(): String? {
    return formats.entries.firstOrNull { (key, value) ->
        key.startsWith("image/jpeg") && value.isNotBlank()
    }?.value
}

private fun Int.formatWithGrouping(): String = NumberFormat.getIntegerInstance().format(this)

@Preview(showBackground = true)
@Composable
private fun BookDetailsScreenPreview() {
    BookshelfTheme {
        BookDetails(
            item = BookItem(
                id = 84,
                title = "Moby Dick; Or, The Whale",
                authors = listOf(
                    PersonItem(
                        name = "Herman Melville",
                        yearOfBirth = 1819,
                        yearOfDeath = 1891
                    )
                ),
                subjects = listOf(
                    "Whaling",
                    "Fiction",
                    "Sea stories",
                    "Epic literature"
                ),
                languages = listOf("en"),
                downloadCount = 62461,
                summaries = listOf(
                    "Few books are more iconic than Herman Melville's 1851 masterpiece, Moby Dick."
                ),
                bookshelves = listOf("Classic Literature"),
                copyright = false,
                formats = mapOf(
                    "application/epub+zip" to "https://www.gutenberg.org/ebooks/2701.epub.images",
                    "text/html" to "https://www.gutenberg.org/cache/epub/2701/pg2701-images.html"
                ),
                mediaType = "Text"
            ),
            isFavorite = false,
            onToggleFavorite = {}
        )
    }
}
