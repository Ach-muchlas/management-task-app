package com.am.schedulingapp.service.di

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.am.schedulingapp.service.di.KoinModule.apiModule
import com.am.schedulingapp.service.di.KoinModule.uiModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        startKoin {
            androidContext(this@MyApplication)
            modules(
                listOf(
                    apiModule,
                    uiModule
                )
            )
        }
    }
}