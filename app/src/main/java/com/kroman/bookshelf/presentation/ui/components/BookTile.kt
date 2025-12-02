package com.kroman.bookshelf.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kroman.bookshelf.R
import com.kroman.bookshelf.domain.model.BookItem
import com.kroman.bookshelf.domain.model.PersonItem

@Composable
fun BookTile(
    modifier: Modifier = Modifier,
    bookItem: BookItem,
    onBookClicked: (BookItem) -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onBookClicked(bookItem) },
        shape = RoundedCornerShape(size = 12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                modifier = Modifier.padding(bottom = 4.dp).testTag("bookTitle"),
                text = bookItem.title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                modifier = Modifier.testTag("authorNameTitle"),
                text = if (bookItem.authors.isNotEmpty()) bookItem.authors[0].name else stringResource(
                    R.string.book_author_unknown
                ),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontStyle = FontStyle.Italic,
                    fontSize = 16.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// previews

@Preview(showBackground = true)
@Composable
private fun BookTilePreview() {
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
            subjects = emptyList(),
            languages = emptyList(),
            downloadCount = 100
        ),
    ) {}
}