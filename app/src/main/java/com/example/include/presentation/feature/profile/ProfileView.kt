package com.example.include.presentation.feature.profile

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.include.data.user.User

interface ProfileView : MvpView {
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun openHistory()
    fun setUserInfo(user: User)
    fun setPic(url: String)
    @StateStrategyType(SkipStrategy::class)
    fun exit()
}
