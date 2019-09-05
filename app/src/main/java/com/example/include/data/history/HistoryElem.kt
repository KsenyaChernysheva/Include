package com.example.include.data.history

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class HistoryElem(
    @ServerTimestamp val time: Date?,
    val user_id: String?,
    val track_url: String
) {
    constructor() : this(null,"","")
}