package com.example.include

import android.app.Application
import com.example.include.presentation.di.component.AppComponent
import com.example.include.presentation.di.component.DaggerAppComponent
import com.example.include.presentation.di.module.MainModule
import com.vk.sdk.VKSdk

class IncludeApp : Application() {

    override fun onCreate() {
        super.onCreate()
        VKSdk.initialize(applicationContext)
        appComponent = DaggerAppComponent.builder().mainModule(MainModule(this)).build()
    }
    companion object {
        lateinit var appComponent: AppComponent
    }
}