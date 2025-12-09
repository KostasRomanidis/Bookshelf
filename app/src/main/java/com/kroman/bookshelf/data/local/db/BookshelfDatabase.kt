package com.kroman.bookshelf.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
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
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class BookshelfDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun remoteKeyDao(): RemoteKeyDao
}