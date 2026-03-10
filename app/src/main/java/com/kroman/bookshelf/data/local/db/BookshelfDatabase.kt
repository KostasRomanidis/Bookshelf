package com.kroman.bookshelf.data.local.db

import androidx.room.Database
import androidx.room.migration.Migration
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kroman.bookshelf.data.local.converters.Converters
import com.kroman.bookshelf.data.local.dao.BookDao
import com.kroman.bookshelf.data.local.dao.RemoteKeyDao
import com.kroman.bookshelf.data.local.entity.BookEntity
import com.kroman.bookshelf.data.local.entity.BookPersonCrossRef
import com.kroman.bookshelf.data.local.entity.PersonEntity
import com.kroman.bookshelf.data.local.entity.RemoteKeyEntity

@Database(
    entities = [
        BookEntity::class,
        PersonEntity::class,
        BookPersonCrossRef::class,
        RemoteKeyEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class BookshelfDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun remoteKeyDao(): RemoteKeyDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE books ADD COLUMN language TEXT")
                db.execSQL("ALTER TABLE books ADD COLUMN hasEpub INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE books ADD COLUMN hasHtml INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE books ADD COLUMN hasPdf INTEGER NOT NULL DEFAULT 0")
                db.execSQL(
                    """
                    UPDATE books
                    SET language = CASE
                        WHEN languages IS NULL OR languages = '' THEN NULL
                        WHEN INSTR(languages, ',') > 0 THEN SUBSTR(languages, 1, INSTR(languages, ',') - 1)
                        ELSE languages
                    END
                    """.trimIndent()
                )
            }
        }
    }
}
