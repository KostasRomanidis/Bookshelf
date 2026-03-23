package com.kroman.bookshelf

import android.app.Application
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.request.crossfade
import com.kroman.bookshelf.di.bookshelfModule
import com.kroman.bookshelf.di.databaseModule
import com.kroman.bookshelf.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class BookshelfApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SingletonImageLoader.setSafe { context ->
            ImageLoader.Builder(context)
                .crossfade(true)
                .build()
        }
        startKoin {
            androidContext(this@BookshelfApplication)
            modules( bookshelfModule, networkModule, databaseModule)
        }
    }
}
