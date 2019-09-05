package com.example.include.presentation.di.module

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class MainModule(private val app: Application) {

    @Provides
    @Singleton
    fun provideApp(): Context = app
}