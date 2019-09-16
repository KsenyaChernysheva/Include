package com.example.include.data.track

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Track(
    val name: String = "",
    val group: String = "",
    val url: String = ""
) : Parcelable
