package com.example.include.presentation.feature.podcastslist

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.include.data.podcast.Podcast

interface PodcastView : MvpView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setList(list: List<Podcast>)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showLoading()

    fun hideLoading()

    fun showError(message: String?)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun navigateToTrackList(podcast: Podcast)
}
