package com.kroman.bookshelf.domain.usecases

import com.kroman.bookshelf.domain.model.BookItem
import com.kroman.bookshelf.domain.repositories.BooksRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface GetCuratedPickUseCase {
    suspend fun execute(): BookItem?
}

class GetCuratedPickUseCaseImpl(
    private val booksRepository: BooksRepository
) : GetCuratedPickUseCase {
    private val mutex = Mutex()
    private var resolved = false
    private var cachedBook: BookItem? = null

    override suspend fun execute(): BookItem? {
        if (resolved) return cachedBook

        return mutex.withLock {
            if (!resolved) {
                cachedBook = booksRepository.getRandomLocalBook()
                resolved = true
            }
            cachedBook
        }
    }
}
