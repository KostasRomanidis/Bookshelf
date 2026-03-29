package com.kroman.bookshelf.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.kroman.bookshelf.R
import com.kroman.bookshelf.presentation.ui.theme.BookshelfTheme
import com.kroman.bookshelf.presentation.ui.theme.Dimens

@Composable
fun CuratedPickTile(
    title: String,
    author: String,
    coverImageUrl: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tileContentDescription = stringResource(
        R.string.curated_pick_content_description,
        title,
        author
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {
                contentDescription = tileContentDescription
                role = Role.Button
            }
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 2.dp,
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(232.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            CuratedPickBackground(
                title = title,
                coverImageUrl = coverImageUrl
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.06f),
                                Color.Black.copy(alpha = 0.18f),
                                Color.Black.copy(alpha = 0.76f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Dimens.spaceXl),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = stringResource(R.string.curated_pick_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.88f)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = author,
                    style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                    color = Color.White.copy(alpha = 0.9f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun CuratedPickBackground(
    title: String,
    coverImageUrl: String?
) {
    if (coverImageUrl != null) {
        AsyncImage(
            model = coverImageUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize().padding(vertical = Dimens.spaceSm),
            contentScale = ContentScale.FillHeight
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.92f),
                            MaterialTheme.colorScheme.surfaceContainerHigh,
                            MaterialTheme.colorScheme.surfaceContainer
                        )
                    )
                )
        ) {
            Text(
                text = title,
                modifier = Modifier.padding(Dimens.spaceXl),
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.18f),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.22f),
                        Color.Transparent
                    )
                )
            )
    )
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun CuratedPickTilePreview() {
    BookshelfTheme {
        CuratedPickTile(
            title = "Frankenstein; Or, The Modern Prometheus",
            author = "Mary Wollstonecraft Shelley",
            coverImageUrl = "https://www.gutenberg.org/cache/epub/84/pg84.cover.medium.jpg",
            onClick = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 360, name = "No Cover")
@Composable
private fun CuratedPickTileNoCoverPreview() {
    BookshelfTheme {
        CuratedPickTile(
            title = "The Left Hand of Darkness",
            author = "Ursula K. Le Guin",
            coverImageUrl = null,
            onClick = {}
        )
    }
}
