package com.example.include.presentation.feature.player

import android.os.Parcelable
import com.example.include.data.track.Track
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PlayInfo(
    val track: Track,
    val duration: Int,
    val isPlaying: Boolean,
    val isPreparing: Boolean
) : Parcelable