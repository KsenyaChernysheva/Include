package com.example.include.presentation.feature.podcastslist

import android.content.ContentValues.TAG
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.include.data.podcast.Podcast
import com.google.firebase.firestore.FirebaseFirestore
import java.util.ArrayList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@InjectViewState
class PodcastsPresenter
@Inject constructor(val firebaseFirestore: FirebaseFirestore) : MvpPresenter<PodcastView>() {

    val podcasts = arrayListOf(Podcast("TURBO FLEX", "https://i.ytimg.com/vi/TE9jFgBOws0/hqdefault.jpg"),
            Podcast("Flex", "https://pbs.twimg.com/tweet_video_thumb/DYHorP1W0AE6lWr.jpg"),
            Podcast("На Заре", "https://www.desktopbackground.org/p/2013/05/24/581006_sunset-1920x1080-cool-1920x1080-hd-wallpapers-and-free-stock-photo_1920x1080_h.jpg"),
            Podcast("TURBO FLEX", "https://i.ytimg.com/vi/TE9jFgBOws0/hqdefault.jpg"),
            Podcast("Flex", "https://pbs.twimg.com/tweet_video_thumb/DYHorP1W0AE6lWr.jpg"),
            Podcast("На Заре", "https://www.desktopbackground.org/p/2013/05/24/581006_sunset-1920x1080-cool-1920x1080-hd-wallpapers-and-free-stock-photo_1920x1080_h.jpg"),
            Podcast("TURBO FLEX", "https://i.ytimg.com/vi/TE9jFgBOws0/hqdefault.jpg"),
            Podcast("Flex", "https://pbs.twimg.com/tweet_video_thumb/DYHorP1W0AE6lWr.jpg"),
            Podcast("На Заре", "https://www.desktopbackground.org/p/2013/05/24/581006_sunset-1920x1080-cool-1920x1080-hd-wallpapers-and-free-stock-photo_1920x1080_h.jpg"),
            Podcast("TURBO FLEX", "https://i.ytimg.com/vi/TE9jFgBOws0/hqdefault.jpg"),
            Podcast("Flex", "https://pbs.twimg.com/tweet_video_thumb/DYHorP1W0AE6lWr.jpg"),
            Podcast("На Заре", "https://www.desktopbackground.org/p/2013/05/24/581006_sunset-1920x1080-cool-1920x1080-hd-wallpapers-and-free-stock-photo_1920x1080_h.jpg"))

    fun loadPodcasts(){
        viewState.setLoading()
        val podcastsCollectionReference = firebaseFirestore.collection("groups")
        val podcastsQuery = podcastsCollectionReference
        val list = ArrayList<Podcast>()
        podcastsQuery.get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val podcast = document.toObject(Podcast::class.java)
                        list.add(podcast)
                    }
                    viewState.setList(list)
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Error getting documents: ", exception)
                    viewState.showError(exception.message)
                }
    }
}