package com.example.include.presentation.feature.trackslist

import android.content.ContentValues
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.include.R
import com.example.include.data.fav.Fav
import com.example.include.data.track.Track
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.tracks_list_item.view.*

class TrackHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var liked = false
    var favId = ""
    var trackId = ""
    fun bind(track: Track) {
        itemView.tv_list_name.text = track.name
        itemView.tv_group.text = track.group
        itemView.iv_fav.setOnClickListener { onLikePressed() }
        itemView.iv_fav.isClickable = false
        checkLiked(track)
    }

    private fun onLikePressed() {
        itemView.iv_fav.isClickable = false
        if (liked) {
            FirebaseFirestore.getInstance()
                .collection("favs")
                .document(favId).delete()
                .addOnSuccessListener {
                    liked = false
                    setLikedState()
                    itemView.iv_fav.isClickable = true
                }
                .addOnFailureListener { exception ->
                    Log.d(ContentValues.TAG, "Error deleting documents: ", exception)
                    itemView.iv_fav.isClickable = true
                }
        } else {
            FirebaseFirestore.getInstance()
                .collection("favs")
                .add(Fav(FirebaseAuth.getInstance().currentUser?.uid.toString(), trackId))
                .addOnSuccessListener {
                    liked = true
                    setLikedState()
                    itemView.iv_fav.isClickable = true
                }
                .addOnFailureListener { exception ->
                    Log.d(ContentValues.TAG, "Error adding documents: ", exception)
                    itemView.iv_fav.isClickable = true
                }
        }

    }

    private fun checkLiked(track: Track) {
        FirebaseFirestore.getInstance().collection("tracks")
            .whereEqualTo("group", track.group)
            .whereEqualTo("name", track.name)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val trackResult = document.toObject(Track::class.java)
                    if (trackResult.name.equals(track.name)) {
                        trackId = document.id
                        FirebaseFirestore.getInstance()
                            .collection("favs")
                            .whereEqualTo("user_id", FirebaseAuth.getInstance().currentUser?.uid)
                            .whereEqualTo("track_id", document.id)
                            .get()
                            .addOnSuccessListener { res ->
                                for (doc in res) {
                                    liked = true
                                    favId = doc.id
                                    setLikedState()
                                }
                                if (res.isEmpty) {
                                    liked = false
                                    setLikedState()
                                }
                                itemView.iv_fav.isClickable = true
                            }
                            .addOnFailureListener { exception ->
                                liked = false
                                setLikedState()
                                itemView.iv_fav.isClickable = true
                                Log.d(ContentValues.TAG, "Error getting documents: ", exception)
                            }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error getting documents: ", exception)
                itemView.iv_fav.isClickable = true
            }
    }

    private fun setLikedState() {
        if (liked)
            itemView.iv_fav.setImageResource(R.drawable.ic_favorite_white_24dp)
        else
            itemView.iv_fav.setImageResource(R.drawable.ic_favorite_border_white_24dp)

    }
}
