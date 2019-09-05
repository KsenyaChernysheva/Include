package com.example.include.presentation.di.module

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import javax.inject.Singleton

import dagger.Module
import dagger.Provides

@Module
class FirebaseModule {
    @Singleton
    @Provides
    internal fun firebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Singleton
    @Provides
    internal fun firebaseDB(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

}