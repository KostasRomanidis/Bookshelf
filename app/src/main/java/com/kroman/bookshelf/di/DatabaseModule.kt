package com.kroman.bookshelf.di

import androidx.room.Room
import com.kroman.bookshelf.data.local.db.BookshelfDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            BookshelfDatabase::class.java,
            "books_database"
        ).addMigrations(
            BookshelfDatabase.MIGRATION_1_2,
            BookshelfDatabase.MIGRATION_2_3,
            BookshelfDatabase.MIGRATION_3_4
        )
            .build()
    }

    single { get<BookshelfDatabase>().bookDao() }
    single { get<BookshelfDatabase>().remoteKeyDao() }
}
