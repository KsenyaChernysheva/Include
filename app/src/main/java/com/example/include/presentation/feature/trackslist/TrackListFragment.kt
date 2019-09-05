package com.example.include.presentation.feature.trackslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.include.IncludeApp
import com.example.include.R
import com.example.include.data.podcast.Podcast
import com.example.include.data.track.Track
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_tracks_list.*
import javax.inject.Inject

class TrackListFragment : MvpAppCompatFragment(), TrackListView, IOnBackPressed,
    SwipeRefreshLayout.OnRefreshListener {

    private var adapter: TrackAdapter = TrackAdapter { onTrackClick(it) }

    @Inject
    @InjectPresenter
    lateinit var presenter: TrackListPresenter

    @ProvidePresenter
    fun initPresenter() = presenter

    private var name = ""

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
        if (savedInstanceState == null) {
            rv_tracks_list.adapter = adapter
            // img_like.setOnClickListener { dateDetailPresenter.likePressed(DateDB(tv_fact.text.toString())) }
            iv_play_list.setOnClickListener { presenter.playPosition(0) }
            name = arguments?.getString(ARG_NAME) ?: ""
            val url = arguments?.getString(ARG_URL)
            tv_list_name.text = name
            pb_list.visibility = View.VISIBLE
            swipe_refresh.setOnRefreshListener(this)
            when (name) {
                "Favourite" -> presenter.loadFav()
                "History" -> presenter.loadHistory()
                else -> {
                    pb_list.visibility = View.VISIBLE
                    setPic(url ?: "")
                    presenter.loadList(name)
                }
            }
        }
    }

    private fun onTrackClick(position: Int) =
        presenter.playPosition(position)

    override fun setList(list: List<Track>) {
        adapter.tracks = list
        adapter.notifyDataSetChanged()
        pb_list.visibility = View.GONE
        swipe_refresh.isRefreshing = false
    }

    override fun setPic(img: String) {
        Picasso.get().load(img).into(iv_background_pic)
    }

    override fun unlike() {}

    override fun like() {}

    override fun onRefresh() {
        swipe_refresh.isRefreshing = true
        pb_list.visibility = View.VISIBLE
        when (name) {
            "Favourite" -> presenter.loadFav()
            "History" -> presenter.loadHistory()
            else -> {
                presenter.loadList(name)
            }
        }
    }

    override fun onBackPressed(): Boolean {
        return !name.equals("Favourite") && !name.equals("History")
    }

    companion object {
        const val ARG_URL = "url"
        const val ARG_NAME = "name"
        fun newInstance(podcast: Podcast): TrackListFragment {
            val args = Bundle()
            args.putString(ARG_NAME, podcast.name)
            args.putString(ARG_URL, podcast.img)
            val fragment = TrackListFragment()
            fragment.arguments = args
            return fragment
        }
    }
}