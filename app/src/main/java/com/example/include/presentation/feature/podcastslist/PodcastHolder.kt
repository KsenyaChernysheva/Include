package com.example.include.presentation.feature.podcastslist

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.include.data.podcast.Podcast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.podcast_list_item.view.*

class PodcastHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(podcast: Podcast) {
        itemView.tv_podcast_name.text = podcast.name
        Picasso.get().load(podcast.img).into(itemView.iv_background_pic)
    }
}
