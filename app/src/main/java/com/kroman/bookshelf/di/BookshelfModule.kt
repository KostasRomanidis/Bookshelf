package com.kroman.bookshelf.di

import com.kroman.bookshelf.data.local.sources.BooksLocalSource
import com.kroman.bookshelf.data.local.sources.BooksLocalSourceImpl
import com.kroman.bookshelf.data.remote.sources.BooksPagingSource
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
import com.kroman.bookshelf.domain.usecases.GetFavoriteBooksPagingUseCase
import com.kroman.bookshelf.domain.usecases.GetFavoriteBooksPagingUseCaseImpl
import com.kroman.bookshelf.domain.usecases.GetPagedBooksUseCase
import com.kroman.bookshelf.domain.usecases.GetPagedBooksUseCaseImpl
import com.kroman.bookshelf.domain.usecases.ObserveIsFavoriteUseCase
import com.kroman.bookshelf.domain.usecases.ObserveIsFavoriteUseCaseImpl
import com.kroman.bookshelf.domain.usecases.ToggleFavoriteBookUseCase
import com.kroman.bookshelf.domain.usecases.ToggleFavoriteBookUseCaseImpl
import com.kroman.bookshelf.presentation.viewmodels.FavoritesViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val bookshelfModule = module {
    // sources
    singleOf(::BooksRemoteSourceImpl) { bind<BooksRemoteSource>() }
    singleOf(::BooksLocalSourceImpl) { bind<BooksLocalSource>() }
    factoryOf(::BooksPagingSource)

    // repositories
    factoryOf(::BooksRepositoryImpl) { bind<BooksRepository>() }

    // usecases
    factoryOf(::GetBooksUseCaseImpl) { bind<GetBooksUseCase>() }
    factoryOf(::GetPagedBooksUseCaseImpl) { bind<GetPagedBooksUseCase>() }
    factoryOf(::GetBookDetailsUseCaseImpl) { bind<GetBookDetailsUseCase>() }
    factoryOf(::GetFavoriteBooksPagingUseCaseImpl) { bind<GetFavoriteBooksPagingUseCase>() }
    factoryOf(::ObserveIsFavoriteUseCaseImpl) { bind<ObserveIsFavoriteUseCase>() }
    factoryOf(::ToggleFavoriteBookUseCaseImpl) { bind<ToggleFavoriteBookUseCase>() }

    // viewModels
    viewModel {
        BooksViewModel(getPagedBooksUseCase = get(), toggleFavoriteBookUseCase = get())
    }

    viewModel { (bookId: Int) ->
        BookDetailsViewModel(
            bookId = bookId,
            getBookDetailsUseCase = get(),
            toggleFavoriteBookUseCase = get(),
            observeIsFavoriteUseCase = get()
        )
    }

    viewModel {
        FavoritesViewModel(
            getFavoriteBooksPagingUseCase = get(),
            toggleFavoriteBookUseCase = get(),
        )
    }
}
