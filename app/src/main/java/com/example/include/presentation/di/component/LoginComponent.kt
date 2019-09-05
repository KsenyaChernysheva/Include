package com.example.include.presentation.di.component

import com.example.include.presentation.feature.login.LoginActivity
import dagger.Subcomponent

@Subcomponent
interface LoginComponent {
    fun inject(loginActivity: LoginActivity)

    @Subcomponent.Builder
    interface Builder {
        fun build(): LoginComponent
    }
}