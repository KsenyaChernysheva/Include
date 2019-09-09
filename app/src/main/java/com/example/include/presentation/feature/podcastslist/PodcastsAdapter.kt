package com.example.include.presentation.feature.podcastslist

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.include.data.podcast.Podcast

class PodcastsAdapter(private var onItemClick: (Podcast) -> Unit) : RecyclerView.Adapter<PodcastHolder>() {

    var podcasts: List<Podcast> = arrayListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PodcastHolder.create(parent)

    override fun getItemCount(): Int = podcasts.size

    override fun onBindViewHolder(holder: PodcastHolder, position: Int) {
        holder.bind(podcasts[position])
        holder.itemView.setOnClickListener { onItemClick(podcasts[position]) }
    }
}