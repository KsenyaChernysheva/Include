package com.example.include.presentation.feature.login

import com.arellomobile.mvp.MvpView

interface LoginView : MvpView{

    fun loginVK()
    fun showError(e: String, b: Boolean)
    fun enterApp()
}