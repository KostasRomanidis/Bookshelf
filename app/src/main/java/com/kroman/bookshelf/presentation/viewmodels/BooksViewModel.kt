package com.kroman.bookshelf.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kroman.bookshelf.domain.model.BookItem
import com.kroman.bookshelf.domain.model.Result
import com.kroman.bookshelf.domain.usecases.GetBooksUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface BooksUiState {
    object Loading : BooksUiState
    data class Success(val books: List<BookItem>) : BooksUiState
    data class Error(val message: String) : BooksUiState
}

class BooksViewModel(
    private val getBooksUseCase: GetBooksUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow<BooksUiState>(BooksUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun loadBooks() {
        viewModelScope.launch(dispatcher) {
            when (val result = getBooksUseCase.execute()) {
                is Result.Success -> {
                    _uiState.value = BooksUiState.Success(result.data)
                }

                is Result.Error -> _uiState.value =
                    BooksUiState.Error(result.exception.message.toString())
            }
        }
    }
}