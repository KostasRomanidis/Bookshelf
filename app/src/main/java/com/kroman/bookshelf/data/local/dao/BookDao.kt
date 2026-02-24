package com.kroman.bookshelf.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.kroman.bookshelf.data.local.entity.BookEntity
import com.kroman.bookshelf.data.local.entity.BookPersonCrossRef
import com.kroman.bookshelf.data.local.entity.PersonEntity
import com.kroman.bookshelf.data.local.entity.PersonType
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Transaction
    @Query("SELECT * FROM books ORDER BY serverOrder ASC")
    fun getAllBooksPaged(): PagingSource<Int, BookEntity>

    @Transaction
    @Query("SELECT * FROM books WHERE isFavorite = 1 ORDER BY serverOrder ASC")
    fun getFavoriteBooksPaged(): PagingSource<Int, BookEntity>

    @Transaction
    @Query("SELECT * FROM books WHERE id = :bookId")
    suspend fun getBookById(bookId: Int): BookEntity?

    @Query("SELECT isFavorite FROM books WHERE id = :bookId")
    suspend fun getIsFavorite(bookId: Int): Boolean?

    @Query("SELECT isFavorite FROM books WHERE id = :bookId")
    fun observeIsFavorite(bookId: Int): Flow<Boolean?>

    @Query("UPDATE books SET isFavorite = :value WHERE id = :bookId")
    suspend fun updateFavorite(bookId: Int, value: Boolean)

    @Query(
        """
        SELECT p.* FROM persons p
        INNER JOIN book_person_cross_ref bpr ON p.personId = bpr.personId
        WHERE bpr.bookId = :bookId AND bpr.type = :type
        """
    )
    suspend fun getPersonsForBook(bookId: Int, type: PersonType): List<PersonEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<BookEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPersons(persons: List<PersonEntity>): List<Long>

    @Query("SELECT personId FROM persons WHERE name = :name")
    suspend fun getPersonIdByName(name: String): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookPersonRefs(refs: List<BookPersonCrossRef>)

    @Query("DELETE FROM books")
    suspend fun clearAllBooks()

    @Transaction
    suspend fun insertBookWithRelations(
        book: BookEntity,
        authors: List<PersonEntity>,
        translators: List<PersonEntity>
    ) {
        val preservedFavorite = getIsFavorite(book.id) ?: false
        insertBooks(listOf(book.copy(isFavorite = preservedFavorite)))

        val authorRefs = mutableListOf<BookPersonCrossRef>()
        authors.forEach { author ->
            val existingId = getPersonIdByName(author.name)
            val personId = existingId ?: insertPersons(listOf(author)).first()
            authorRefs.add(BookPersonCrossRef(book.id, personId, PersonType.AUTHOR))
        }

        val translatorRefs = mutableListOf<BookPersonCrossRef>()
        translators.forEach { translator ->
            val existingId = getPersonIdByName(translator.name)
            val personId = existingId ?: insertPersons(listOf(translator)).first()
            translatorRefs.add(BookPersonCrossRef(book.id, personId, PersonType.TRANSLATOR))
        }

        insertBookPersonRefs(authorRefs + translatorRefs)
    }
}
