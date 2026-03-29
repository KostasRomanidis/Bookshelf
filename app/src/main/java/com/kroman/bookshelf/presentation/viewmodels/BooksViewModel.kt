package com.kroman.bookshelf.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.kroman.bookshelf.domain.model.BookFilters
import com.kroman.bookshelf.domain.model.BookFormatFilter
import com.kroman.bookshelf.domain.model.BookItem
import com.kroman.bookshelf.domain.model.BookSortOption
import com.kroman.bookshelf.domain.usecases.GetCuratedPickUseCase
import com.kroman.bookshelf.domain.usecases.GetFilteredBooksPagingUseCase
import com.kroman.bookshelf.domain.usecases.ToggleFavoriteBookUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update

data class BooksUiState(
    val curatedPick: BookItem? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class BooksViewModel(
    private val getFilteredBooksPagingUseCase: GetFilteredBooksPagingUseCase,
    private val getCuratedPickUseCase: GetCuratedPickUseCase,
    private val toggleFavoriteBookUseCase: ToggleFavoriteBookUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {
    private val _filters = MutableStateFlow(BookFilters())
    val filters = _filters.asStateFlow()

    private val _uiState = MutableStateFlow(BooksUiState())
    val uiState: StateFlow<BooksUiState> = _uiState.asStateFlow()

    val books = filters
        .flatMapLatest { currentFilters ->
            getFilteredBooksPagingUseCase.execute(currentFilters)
        }
        .cachedIn(viewModelScope)

    init {
        viewModelScope.launch(dispatcher) {
            _uiState.update { it.copy(curatedPick = getCuratedPickUseCase.execute()) }
        }
    }

    fun updateSearchQuery(query: String) {
        _filters.update { it.copy(searchQuery = query) }
    }

    fun selectLanguage(language: String?) {
        _filters.update { it.copy(language = language) }
    }

    fun selectFormat(format: BookFormatFilter?) {
        _filters.update { it.copy(format = format) }
    }

    fun selectSort(sort: BookSortOption) {
        _filters.update { it.copy(sort = sort) }
    }

    fun clearFilters() {
        _filters.value = BookFilters()
    }

    fun toggleFavorite(bookId: Int) {
        viewModelScope.launch(dispatcher) {
            toggleFavoriteBookUseCase.execute(bookId)
        }
    }
}
