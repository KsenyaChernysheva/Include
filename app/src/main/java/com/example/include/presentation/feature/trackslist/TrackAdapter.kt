package com.example.include.presentation.feature.trackslist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.include.R
import com.example.include.data.track.Track

class TrackAdapter(private var onItemClick: (Int) -> Unit) : RecyclerView.Adapter<TrackHolder>() {

    var tracks: List<Track> = arrayListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.tracks_list_item, parent, false)
        return TrackHolder(v)
    }

    override fun getItemCount(): Int = tracks.size

    override fun onBindViewHolder(holder: TrackHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener { onItemClick(position) }
    }
}