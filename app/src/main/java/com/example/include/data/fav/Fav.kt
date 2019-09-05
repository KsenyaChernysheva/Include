package com.example.include.data.fav

data class Fav(
        val user_id: String,
        val track_id: String
) {
    constructor() : this("", "")
}