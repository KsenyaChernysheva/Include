package com.example.include.presentation.di.module

import com.example.include.domain.user.UserModel
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class UserModule {

    @Provides
    @Singleton
    fun provideUserModel(): UserModel =
        UserModel()
}
