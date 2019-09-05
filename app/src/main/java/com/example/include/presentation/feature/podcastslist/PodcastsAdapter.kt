package com.example.include.presentation.feature.podcastslist

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.include.R
import com.example.include.data.podcast.Podcast
import kotlinx.android.synthetic.main.podcast_list_item.view.*

class PodcastsAdapter(private var onItemClick: (Podcast) -> Unit) : RecyclerView.Adapter<PodcastHolder>() {

    var podcasts: List<Podcast> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PodcastHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.podcast_list_item, parent, false)
        return PodcastHolder(v)
    }

    override fun getItemCount(): Int = podcasts.size

    override fun onBindViewHolder(holder: PodcastHolder, position: Int) {
        holder.bind(podcasts[position])
        holder.itemView.setOnClickListener { onItemClick(podcasts[position]) }
    }
}