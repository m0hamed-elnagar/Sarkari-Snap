package com.example.sarkarisnap.app


import android.app.Application
import com.example.sarkarisnap.di.initKoin
import org.koin.android.ext.koin.androidContext

class BloggerApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@BloggerApplication)
        }
    }
}