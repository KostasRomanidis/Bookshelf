package com.kroman.bookshelf.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.kroman.bookshelf.domain.usecases.GetPagedBooksUseCase

class BooksViewModel(
    private val getPagedBooksUseCase: GetPagedBooksUseCase,
) : ViewModel() {
    val books = getPagedBooksUseCase.execute().cachedIn(viewModelScope)
}