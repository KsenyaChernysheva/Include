package com.example.include.presentation.feature.trackslist

import android.content.ComponentName
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.include.data.history.HistoryElem
import com.example.include.data.fav.Fav
import com.example.include.data.track.Track
import com.example.include.presentation.feature.player.MusicPlayer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import javax.inject.Inject

@InjectViewState
class TrackListPresenter
@Inject constructor(val app: Context, val firebaseFirestore: FirebaseFirestore) :
    MvpPresenter<TrackListView>(), ServiceConnection {

    lateinit var player: MusicPlayer

    var tracks = arrayListOf<Track>()

    override fun onFirstViewAttach() {
        val intent = Intent(app, MusicPlayer::class.java)
        app.bindService(intent, this, Context.BIND_AUTO_CREATE)
    }

    fun playPosition(position: Int) {
        player.playListFrom(tracks, position)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        app.unbindService(this)
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val b = service as MusicPlayer.MBinder
        player = b.service
    }

    fun loadList(name: String) {
        val list = ArrayList<Track>()
        firebaseFirestore
            .collection("tracks")
            .whereEqualTo("group", name)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val track = document.toObject(Track::class.java)
                    list.add(track)
                }
                viewState.setList(list)
                tracks = list
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
                viewState.setList(list)
            }
    }

    fun loadFav() {
        val list = ArrayList<Track>()
        firebaseFirestore
            .collection("favs")
            .whereEqualTo("user_id", FirebaseAuth.getInstance().currentUser?.uid)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    var size = result.size()
                    val fav = document.toObject(Fav::class.java)
                    val docRef = firebaseFirestore.collection("tracks").document(fav.track_id)
                    docRef.get()
                        .addOnSuccessListener { trackResult ->
                            if (trackResult != null) {
                                size -= 1
                                val track = trackResult.toObject(Track::class.java)
                                track?.let { list.add(it) }
                                tracks = list
                            } else {
                                Log.d(TAG, "No such document")
                            }
                            viewState.setList(list)
                        }
                        .addOnFailureListener { exception ->
                            Log.d(TAG, "get failed with ", exception)
                            viewState.setList(list)
                        }
                }
                if (result.isEmpty)
                    viewState.setList(list)
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
                viewState.setList(list)
            }

    }

    fun loadHistory() {
        val list = ArrayList<Track>()
        firebaseFirestore
            .collection("history")
            .whereEqualTo("user_id", FirebaseAuth.getInstance().currentUser?.uid)
            .orderBy("time", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                for (his in result) {
                    val history = his.toObject(HistoryElem::class.java)
                    firebaseFirestore
                        .collection("tracks")
                        .whereEqualTo("url", history.track_url)
                        .get()
                        .addOnSuccessListener { res ->
                            for (tr in res) {
                                val track = tr.toObject(Track::class.java)
                                list.add(track)
                                tracks = list
                            }
                            viewState.setList(list)
                        }
                        .addOnFailureListener { exception ->
                            Log.d(TAG, "Error getting documents: ", exception)
                            viewState.setList(list)
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
                viewState.setList(list)
            }
    }
}