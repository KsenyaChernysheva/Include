package com.example.include.presentation.feature.player

import com.example.include.data.track.Track

interface MusicServiceBind {

    fun playpause()
    fun next()
    fun previous()
    fun seekTo(msec: Int)
    fun playFromList(list: ArrayList<Track>, position: Int)
    fun playFromList(position: Int)
    fun getTracks(): List<Track>
    fun stop()
}