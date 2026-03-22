package com.kroman.bookshelf.presentation.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kroman.bookshelf.R
import com.kroman.bookshelf.presentation.ui.theme.BookshelfTheme

@Composable
fun BackToTopButton(
    visible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { fullHeight -> fullHeight }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { fullHeight -> fullHeight }) + fadeOut(),
        modifier = modifier
    ) {
        FloatingActionButton(
            onClick = onClick,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = CircleShape
        ) {
            if (LocalInspectionMode.current) {
                Text(text = "↑")
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_up),
                    contentDescription = stringResource(R.string.back_to_top_content_description)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BackToTopButtonPreview() {
    BookshelfTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            BackToTopButton(
                visible = true,
                onClick = {},
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            )
        }
    }
}
