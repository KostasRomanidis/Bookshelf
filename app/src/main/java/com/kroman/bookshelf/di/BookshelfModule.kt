package com.kroman.bookshelf.di

import com.kroman.bookshelf.presentation.books.BooksViewModel
import com.kroman.bookshelf.repositories.BooksRepository
import com.kroman.bookshelf.repositories.BooksRepositoryImpl
import com.kroman.bookshelf.sources.BooksRemoteSource
import com.kroman.bookshelf.sources.BooksRemoteSourceImpl
import com.kroman.bookshelf.usecases.GetBooksUseCase
import com.kroman.bookshelf.usecases.GetBooksUseCaseImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val bookshelfModule = module {
    singleOf(::BooksRemoteSourceImpl) { bind<BooksRemoteSource>() }
    factoryOf(::BooksRepositoryImpl) { bind<BooksRepository>() }
    factoryOf(::GetBooksUseCaseImpl) { bind<GetBooksUseCase>() }

    viewModel {
        BooksViewModel(getBooksUseCase = get())
    }
}