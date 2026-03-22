package com.kroman.bookshelf.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kroman.bookshelf.R
import com.kroman.bookshelf.domain.model.BookFilters
import com.kroman.bookshelf.domain.model.BookFormatFilter
import com.kroman.bookshelf.domain.model.BookSortOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BooksFilterSheet(
    filters: BookFilters,
    onDismiss: () -> Unit,
    onApply: (BookFilters) -> Unit,
    onClear: () -> Unit,
) {
    var searchQuery by remember(filters) { mutableStateOf(filters.searchQuery) }
    var language by remember(filters) { mutableStateOf(filters.language.orEmpty()) }
    var format by remember(filters) { mutableStateOf(filters.format) }
    var sort by remember(filters) { mutableStateOf(filters.sort) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = stringResource(R.string.filters_title))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = stringResource(R.string.filters_search)) },
                singleLine = true
            )

            OutlinedTextField(
                value = language,
                onValueChange = { language = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = stringResource(R.string.filters_language)) },
                singleLine = true
            )

            Text(text = stringResource(R.string.filters_format))
            FilterOptionRow(
                label = stringResource(R.string.filters_format_any),
                selected = format == null,
                onClick = { format = null }
            )
            FilterOptionRow(
                label = stringResource(R.string.filters_format_epub),
                selected = format == BookFormatFilter.EPUB,
                onClick = { format = BookFormatFilter.EPUB }
            )
            FilterOptionRow(
                label = stringResource(R.string.filters_format_html),
                selected = format == BookFormatFilter.HTML,
                onClick = { format = BookFormatFilter.HTML }
            )
            FilterOptionRow(
                label = stringResource(R.string.filters_format_pdf),
                selected = format == BookFormatFilter.PDF,
                onClick = { format = BookFormatFilter.PDF }
            )

            Text(text = stringResource(R.string.filters_sort))
            FilterOptionRow(
                label = stringResource(R.string.filters_sort_default),
                selected = sort == BookSortOption.DEFAULT,
                onClick = { sort = BookSortOption.DEFAULT }
            )
            FilterOptionRow(
                label = stringResource(R.string.filters_sort_download_count),
                selected = sort == BookSortOption.DOWNLOAD_COUNT_DESC,
                onClick = { sort = BookSortOption.DOWNLOAD_COUNT_DESC }
            )
            FilterOptionRow(
                label = stringResource(R.string.filters_sort_title),
                selected = sort == BookSortOption.TITLE_ASC,
                onClick = { sort = BookSortOption.TITLE_ASC }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        searchQuery = ""
                        language = ""
                        format = null
                        sort = BookSortOption.DEFAULT
                        onClear()
                    }
                ) {
                    Text(text = stringResource(R.string.filters_clear))
                }
                Button(
                    onClick = {
                        onApply(
                            BookFilters(
                                searchQuery = searchQuery,
                                language = language.trim().takeIf { it.isNotEmpty() },
                                format = format,
                                sort = sort
                            )
                        )
                    }
                ) {
                    Text(text = stringResource(R.string.filters_apply))
                }
            }
        }
    }
}

@Composable
private fun FilterOptionRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(text = label)
    }
}
