package com.kroman.bookshelf.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.kroman.bookshelf.domain.usecases.GetFavoriteBooksPagingUseCase
import com.kroman.bookshelf.domain.usecases.ToggleFavoriteBookUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val getFavoriteBooksPagingUseCase: GetFavoriteBooksPagingUseCase,
    private val toggleFavoriteBookUseCase: ToggleFavoriteBookUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {
    val books = getFavoriteBooksPagingUseCase.execute().cachedIn(viewModelScope)

    fun toggleFavorite(bookId: Int) {
        viewModelScope.launch(dispatcher) {
            toggleFavoriteBookUseCase.execute(bookId)
        }
    }
}
