package com.kroman.bookshelf.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.kroman.bookshelf.R
import com.kroman.bookshelf.domain.model.BookItem
import com.kroman.bookshelf.domain.model.PersonItem
import com.kroman.bookshelf.presentation.ui.theme.BookshelfTheme
import com.kroman.bookshelf.presentation.ui.theme.Dimens
import com.kroman.bookshelf.presentation.ui.theme.Radius

@Composable
fun BookTile(
    modifier: Modifier = Modifier,
    bookItem: BookItem,
    onBookClicked: (BookItem) -> Unit,
    onToggleFavorite: (Int) -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onBookClicked(bookItem) },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spaceMd),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spaceLg)
        ) {
            BookCover(
                title = bookItem.title,
                coverImageUrl = bookItem.coverImageUrl()
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = Dimens.spaceSm)
                            .testTag("bookTitle"),
                        text = bookItem.title,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    IconButton(onClick = { onToggleFavorite(bookItem.id) }) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(R.drawable.ic_add_to_favorite),
                            contentDescription = if (bookItem.isFavorite) {
                                stringResource(R.string.remove_from_favorites)
                            } else {
                                stringResource(R.string.add_to_favorites)
                            },
                            tint = if (bookItem.isFavorite) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.outlineVariant
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.spaceXs))

                Text(
                    modifier = Modifier.testTag("authorNameTitle"),
                    text = bookItem.primaryAuthor(stringResource(R.string.book_author_unknown)),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                val visibleSubjects = bookItem.subjects.take(2)
                if (visibleSubjects.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(Dimens.spaceSm))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSm)
                    ) {
                        visibleSubjects.forEach { subject ->
                            MetadataChip(label = subject)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BookCover(
    title: String,
    coverImageUrl: String?
) {
    Surface(
        modifier = Modifier
            .size(width = 72.dp, height = 96.dp)
            .aspectRatio(3f / 4f),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        if (coverImageUrl != null) {
            AsyncImage(
                model = coverImageUrl,
                contentDescription = title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
private fun MetadataChip(label: String) {
    Surface(
        shape = RoundedCornerShape(Radius.pill),
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = Dimens.spaceSm, vertical = 6.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
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

// previews

@Preview(showBackground = true)
@Composable
private fun BookTilePreview() {
    BookshelfTheme {
        BookTile(
            bookItem = BookItem(
                id = 10,
                title = "Book Title",
                authors = listOf(
                    PersonItem(
                        name = "Author's Name",
                        yearOfBirth = 1984,
                        yearOfDeath = null
                    )
                ),
                subjects = listOf(
                    "Historical fiction",
                    "Adventure stories"
                ),
                languages = emptyList(),
                downloadCount = 100
            ),
            onBookClicked = {},
            onToggleFavorite = {},
        )
    }
}

@Preview(showBackground = true, name = "Favorited")
@Composable
private fun BookTileFavoritedPreview() {
    BookshelfTheme {
        BookTile(
            bookItem = BookItem(
                id = 10,
                title = "Book Title",
                authors = listOf(
                    PersonItem(
                        name = "Author's Name",
                        yearOfBirth = 1984,
                        yearOfDeath = null
                    )
                ),
                subjects = listOf(
                    "Historical fiction",
                    "Adventure stories"
                ),
                languages = emptyList(),
                downloadCount = 100,
                isFavorite = true
            ),
            onBookClicked = {},
            onToggleFavorite = {},
        )
    }
}
