package com.example.include.presentation.feature.podcastslist

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.include.data.podcast.Podcast

interface PodcastView : MvpView {
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun setList(list: List<Podcast>)
    @StateStrategyType(SkipStrategy::class)
    fun setLoading()

    fun showError(message: String?)
}
