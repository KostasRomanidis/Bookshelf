package com.kroman.bookshelf.di

import com.kroman.bookshelf.presentation.viewmodels.BookDetailsViewModel
import com.kroman.bookshelf.presentation.viewmodels.BooksViewModel
import com.kroman.bookshelf.domain.repositories.BooksRepository
import com.kroman.bookshelf.data.remote.sources.BooksRemoteSource
import com.kroman.bookshelf.data.remote.sources.BooksRemoteSourceImpl
import com.kroman.bookshelf.data.repositories.BooksRepositoryImpl
import com.kroman.bookshelf.domain.usecases.GetBookDetailsUseCase
import com.kroman.bookshelf.domain.usecases.GetBookDetailsUseCaseImpl
import com.kroman.bookshelf.domain.usecases.GetBooksUseCase
import com.kroman.bookshelf.domain.usecases.GetBooksUseCaseImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val bookshelfModule = module {
    singleOf(::BooksRemoteSourceImpl) { bind<BooksRemoteSource>() }
    factoryOf(::BooksRepositoryImpl) { bind<BooksRepository>() }
    factoryOf(::GetBooksUseCaseImpl) { bind<GetBooksUseCase>() }
    factoryOf(::GetBookDetailsUseCaseImpl) { bind<GetBookDetailsUseCase>() }

    viewModel {
        BooksViewModel(getBooksUseCase = get())
    }

    viewModel { (bookId: Int) ->
        BookDetailsViewModel(getBookDetailsUseCase = get(), bookId = bookId)
    }
}
