package com.kroman.bookshelf

import android.app.Application
import com.kroman.bookshelf.di.bookshelfModule
import com.kroman.bookshelf.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class BookshelfApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@BookshelfApplication)
            modules( bookshelfModule, networkModule)
        }
    }
}