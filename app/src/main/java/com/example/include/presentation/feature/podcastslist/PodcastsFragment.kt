package com.example.include.presentation.feature.podcastslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.include.IncludeApp
import com.example.include.R
import com.example.include.data.podcast.Podcast
import com.example.include.presentation.feature.trackslist.TrackListFragment
import kotlinx.android.synthetic.main.fragment_tracks_list.*
import javax.inject.Inject

class PodcastsFragment : MvpAppCompatFragment(), PodcastView, SwipeRefreshLayout.OnRefreshListener {

    @Inject
    @InjectPresenter
    lateinit var presenter: PodcastsPresenter

    @ProvidePresenter
    fun initPresenter() = presenter

    private var adapter: PodcastsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        IncludeApp.appComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_tracks_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = PodcastsAdapter { presenter.onPodcastClick(it) }
        rv_tracks_list.adapter = adapter
        rv_tracks_list.layoutManager = GridLayoutManager(context, 2)
        tv_list_name.text = getString(R.string.podcasts)
        iv_play_list.visibility = View.GONE
        swipe_refresh.setOnRefreshListener(this)
    }

    override fun showLoading() {
        pb_list.visibility = View.VISIBLE
    }

    override fun onRefresh() {
        presenter.loadPodcasts()
    }

    override fun showError(message: String?) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    override fun setList(list: List<Podcast>) {
        adapter?.podcasts = list
    }

    override fun hideLoading() {
        pb_list.visibility = View.GONE
        swipe_refresh.isRefreshing = false
    }

    override fun navigateToTrackList(podcast: Podcast) {
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.container, TrackListFragment.newInstance(podcast))?.commit()
    }
}