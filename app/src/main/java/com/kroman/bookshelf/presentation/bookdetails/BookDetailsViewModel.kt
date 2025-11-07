package com.kroman.bookshelf.presentation.bookdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kroman.bookshelf.domain.data.BookItem
import com.kroman.bookshelf.domain.data.Result
import com.kroman.bookshelf.usecases.GetBookDetailsUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface BookDetailsUiState {
    data class Success(val book: BookItem) : BookDetailsUiState
    data class Error(val message: String) : BookDetailsUiState
    object Loading : BookDetailsUiState
}

class BookDetailsViewModel(
    private val bookId: Int,
    private val getBookDetailsUseCase: GetBookDetailsUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow<BookDetailsUiState>(BookDetailsUiState.Loading)
    val uiState: StateFlow<BookDetailsUiState> = _uiState

    fun getBookDetails() {
        viewModelScope.launch(dispatcher) {
            _uiState.value = BookDetailsUiState.Loading
            when (val result = getBookDetailsUseCase.execute(bookId)) {
                is Result.Success -> {
                    _uiState.value = BookDetailsUiState.Success(result.data)
                }

                is Result.Error -> {
                    _uiState.value =
                        BookDetailsUiState.Error(result.exception?.message ?: "Unknown error")
                }
            }
        }
    }
}
