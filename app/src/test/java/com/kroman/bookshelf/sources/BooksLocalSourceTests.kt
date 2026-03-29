package com.kroman.bookshelf.sources

import androidx.paging.PagingSource
import com.kroman.bookshelf.data.local.dao.BookDao
import com.kroman.bookshelf.data.local.entity.BookEntity
import com.kroman.bookshelf.data.local.entity.PersonEntity
import com.kroman.bookshelf.data.local.entity.PersonType
import com.kroman.bookshelf.data.local.sources.BooksLocalSourceImpl
import com.kroman.bookshelf.domain.model.BookItem
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test

class BooksLocalSourceTests {

    private val bookDao: BookDao = mockk()

    private lateinit var localSource: BooksLocalSourceImpl

    @Before
    fun setUp() {
        localSource = BooksLocalSourceImpl(bookDao)
    }

    @Test
    fun `getAllBooksPaged - delegates to dao`() {
        val pagingSource = mockk<PagingSource<Int, BookEntity>>()
        every { bookDao.getAllBooksPaged() } returns pagingSource

        val result = localSource.getAllBooksPaged()

        assertSame(pagingSource, result)
    }

    @Test
    fun `getBook - maps dao entity to domain model`() = runTest {
        val entity = createBookEntity(id = 1, title = "Local Book")
        coEvery { bookDao.getBookById(1) } returns entity
        coEvery {
            bookDao.getPersonsForBook(1, PersonType.AUTHOR)
        } returns listOf(PersonEntity(name = "Author One", yearOfBirth = 1901, yearOfDeath = null))
        coEvery { bookDao.getPersonsForBook(1, PersonType.TRANSLATOR) } returns emptyList()

        val result = localSource.getBook(1)

        assertEquals(
            BookItem(
                id = 1,
                title = "Local Book",
                authors = listOf(
                    com.kroman.bookshelf.domain.model.PersonItem(
                        name = "Author One",
                        yearOfBirth = 1901,
                        yearOfDeath = null
                    )
                ),
                subjects = listOf("Fiction"),
                languages = listOf("en"),
                downloadCount = 25,
                isFavorite = true
            ),
            result
        )
    }

    @Test
    fun `getBook - returns null when dao has no entity`() = runTest {
        coEvery { bookDao.getBookById(3) } returns null

        val result = localSource.getBook(3)

        assertNull(result)
        coVerify(exactly = 1) { bookDao.getBookById(3) }
    }

    @Test
    fun `getRandomBook - maps dao entity to domain model`() = runTest {
        val entity = createBookEntity(id = 8, title = "Random Pick")
        coEvery { bookDao.getRandomBook() } returns entity
        coEvery {
            bookDao.getPersonsForBook(8, PersonType.AUTHOR)
        } returns listOf(PersonEntity(name = "Random Author", yearOfBirth = 1888, yearOfDeath = 1950))
        coEvery { bookDao.getPersonsForBook(8, PersonType.TRANSLATOR) } returns emptyList()

        val result = localSource.getRandomBook()

        assertEquals("Random Pick", result?.title)
        assertEquals("Random Author", result?.authors?.singleOrNull()?.name)
        assertEquals(8, result?.id)
    }

    @Test
    fun `getRandomBook - returns null when dao has no entity`() = runTest {
        coEvery { bookDao.getRandomBook() } returns null

        val result = localSource.getRandomBook()

        assertNull(result)
        coVerify(exactly = 1) { bookDao.getRandomBook() }
    }

    private fun createBookEntity(
        id: Int,
        title: String
    ): BookEntity {
        return BookEntity(
            id = id,
            title = title,
            subjects = listOf("Fiction"),
            languages = listOf("en"),
            downloadCount = 25,
            serverOrder = 1,
            isFavorite = true
        )
    }
}
