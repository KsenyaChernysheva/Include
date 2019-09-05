package com.example.include.data.podcast

data class Podcast(
        val name: String,
        val img: String
) {
    //empty constructor for firebaseFirestore deserialization
    constructor() : this("", "")
}
