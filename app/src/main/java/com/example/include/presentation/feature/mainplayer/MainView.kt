package com.example.include.presentation.feature.mainplayer

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.include.data.track.Track

interface MainView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setPlayState()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setPauseState()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setTracksList(list: List<Track>)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setTrackInfo(track: Track, duration: Int)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setCurrentPosition(currentPosition: Int)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setBufferPosition(bufferPosition: Int)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun disablePlayer()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun enablePlayer()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showProgress()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun hideProgress()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun openPlayer()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun unbindService()

}