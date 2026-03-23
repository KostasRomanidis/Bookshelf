package com.kroman.bookshelf.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.kroman.bookshelf.R
import com.kroman.bookshelf.presentation.ui.theme.BookshelfTheme
import com.kroman.bookshelf.presentation.ui.theme.Dimens
import com.kroman.bookshelf.presentation.ui.theme.Radius

@Composable
fun MetadataCard(
    modifier: Modifier = Modifier,
    iconRes: Int,
    label: String,
    value: String,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh
) {
    Surface(
        modifier = modifier,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(Radius.lg),
        color = containerColor
    ) {
        Column(
            modifier = Modifier.padding(Dimens.spaceLg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceXs)
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.size(Dimens.spaceXl),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 180)
@Composable
private fun MetadataCardPreview() {
    BookshelfTheme {
        MetadataCard(
            iconRes = R.drawable.ic_download,
            label = "Downloads",
            value = "62,461"
        )
    }
}
