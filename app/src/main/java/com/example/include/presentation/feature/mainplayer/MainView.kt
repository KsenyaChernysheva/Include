package com.example.include.presentation.feature.mainplayer

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.include.data.track.Track

interface MainView : MvpView {
    fun setPlayState()
    fun setPauseState()
    fun setList(list: List<Track>)
    fun setMusic(duration: Int, track: Track)
    fun disablePlayer()
    fun enablePlayer()
    fun updateState()
    @StateStrategyType(SkipStrategy::class)
    fun showProgress()
    @StateStrategyType(SkipStrategy::class)
    fun openPlayer()
    @StateStrategyType(SkipStrategy::class)
    fun sendWait()
}