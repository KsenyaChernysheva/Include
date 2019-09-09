package com.example.include.presentation.feature.podcastslist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.include.R
import com.example.include.data.podcast.Podcast
import com.squareup.picasso.Picasso
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.podcast_list_item.*

class PodcastHolder(
    override val containerView: View
) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(podcast: Podcast) {
        tv_podcast_name.text = podcast.name
        Picasso.get().load(podcast.img).into(iv_background_pic)
    }

    companion object {
        fun create(parent: ViewGroup) = PodcastHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.podcast_list_item, parent, false)
        )
    }
}
