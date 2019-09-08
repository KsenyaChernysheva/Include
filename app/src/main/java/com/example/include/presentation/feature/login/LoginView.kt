package com.example.include.presentation.feature.login

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

interface LoginView : MvpView{

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showLoginVK()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showError(e: String)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun navigateToMainActivity()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun enableLoginBtn()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun disableLoginBtn()
}