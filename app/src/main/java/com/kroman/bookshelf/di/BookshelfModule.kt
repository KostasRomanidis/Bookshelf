package com.kroman.bookshelf.di

import com.kroman.bookshelf.presentation.bookdetails.BookDetailsViewModel
import com.kroman.bookshelf.presentation.books.BooksViewModel
import com.kroman.bookshelf.repositories.BooksRepository
import com.kroman.bookshelf.repositories.BooksRepositoryImpl
import com.kroman.bookshelf.sources.BooksRemoteSource
import com.kroman.bookshelf.sources.BooksRemoteSourceImpl
import com.kroman.bookshelf.usecases.GetBookDetailsUC
import com.kroman.bookshelf.usecases.GetBookDetailsUCImpl
import com.kroman.bookshelf.usecases.GetBooksUseCase
import com.kroman.bookshelf.usecases.GetBooksUseCaseImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val bookshelfModule = module {
    singleOf(::BooksRemoteSourceImpl) { bind<BooksRemoteSource>() }
    factoryOf(::BooksRepositoryImpl) { bind<BooksRepository>() }
    factoryOf(::GetBooksUseCaseImpl) { bind<GetBooksUseCase>() }
    factoryOf(::GetBookDetailsUCImpl) { bind<GetBookDetailsUC>() }

    viewModel {
        BooksViewModel(getBooksUseCase = get())
    }

    viewModel { (bookId: Int) ->
        BookDetailsViewModel(getBookDetailsUC = get(), bookId = bookId)
    }
}
