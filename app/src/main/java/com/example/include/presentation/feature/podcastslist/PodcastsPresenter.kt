package com.example.include.presentation.feature.podcastslist

import android.content.ContentValues.TAG
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.include.data.podcast.Podcast
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@InjectViewState
class PodcastsPresenter
@Inject constructor(val firebaseFirestore: FirebaseFirestore) : MvpPresenter<PodcastView>() {

    override fun onFirstViewAttach() = loadPodcasts()

    fun onPodcastClick(podcast: Podcast) = viewState.navigateToTrackList(podcast)

    fun loadPodcasts() {
        viewState.showLoading()
        val podcastsQuery = firebaseFirestore.collection("groups")
        val list = arrayListOf<Podcast>()
        podcastsQuery.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val podcast = document.toObject(Podcast::class.java)
                    list.add(podcast)
                }
                viewState.setList(list)
                viewState.hideLoading()
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
                viewState.showError(exception.message)
                viewState.hideLoading()
            }
    }
}