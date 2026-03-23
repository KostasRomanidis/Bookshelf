package com.kroman.bookshelf.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import com.kroman.bookshelf.R
import com.kroman.bookshelf.presentation.ui.theme.BookshelfTheme
import com.kroman.bookshelf.presentation.ui.theme.Dimens
import com.kroman.bookshelf.presentation.ui.theme.Radius

@Composable
fun LibrarianNoteSection(
    summary: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Radius.xl),
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(
            modifier = Modifier.padding(Dimens.spaceLg),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceMd)
        ) {
            Text(
                text = stringResource(R.string.book_librarian_note_title),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "\"${summary.take(180).trim()}${if (summary.length > 180) "..." else ""}\"",
                style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun LibrarianNoteSectionPreview() {
    BookshelfTheme {
        LibrarianNoteSection(
            summary = "Few books are more iconic than Herman Melville's 1851 masterpiece, Moby Dick. This note captures the reflective, editorial tone used across the details screen."
        )
    }
}
