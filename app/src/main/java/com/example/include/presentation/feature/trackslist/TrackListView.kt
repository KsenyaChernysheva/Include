package com.example.include.presentation.feature.trackslist

import com.arellomobile.mvp.MvpView
import com.example.include.data.track.Track

interface TrackListView : MvpView{
    fun unlike()
    fun like()
    fun setList(list: List<Track>)
    fun setPic(img: String)
}
