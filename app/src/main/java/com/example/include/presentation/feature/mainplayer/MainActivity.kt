package com.example.include.presentation.feature.mainplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.include.IncludeApp
import com.example.include.R
import com.example.include.data.podcast.Podcast
import com.example.include.data.track.Track
import com.example.include.presentation.feature.player.MusicService
import com.example.include.presentation.feature.player.PlayInfo
import com.example.include.presentation.feature.podcastslist.PodcastsFragment
import com.example.include.presentation.feature.profile.ProfileFragment
import com.example.include.presentation.feature.trackslist.IOnBackPressed
import com.example.include.presentation.feature.trackslist.TrackAdapter
import com.example.include.presentation.feature.trackslist.TrackListFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class MainActivity : MvpAppCompatActivity(), MainView {

    private var favouriteFragment = TrackListFragment.newInstance(Podcast("Favourite", ""))
    private var podcastsFragment = PodcastsFragment()
    private var profileFragment = ProfileFragment()

    private var mediaServiceReceiver: BroadcastReceiver? = null

    private val timeFormat = SimpleDateFormat("mm:ss", Locale.US)

    private var adapter: TrackAdapter? = null

    @Inject
    @InjectPresenter
    lateinit var mainPresenter: MainPresenter

    @ProvidePresenter
    fun initPresenter() = mainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        IncludeApp.appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottom_nav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        bottom_nav.selectedItemId = R.id.nav_podcasts

        adapter = TrackAdapter { mainPresenter.onTrackSelected(it) }
        rv_tracks_list.adapter = adapter

        img_pause_play.setOnClickListener { mainPresenter.onPlayPauseClick() }

        initSeekBar()
        initBottomSheet()
        initBroadcastReceiver()
        bindService()
    }

    override fun onResume() {
        super.onResume()
        mainPresenter.onViewAttach()
    }

    override fun setTracksList(list: List<Track>) {
        adapter?.tracks = list
    }

    override fun setTrackInfo(track: Track, duration: Int) {
        tv_cname.text = track.name
        tv_cgroup.text = track.group
        music_progress.max = duration
        tv_time_left.text = timeFormat.format(duration)
    }

    override fun setPlayState() {
        img_pause_play.setImageResource(R.drawable.ic_pause_white_36dp)
    }

    override fun setPauseState() {
        img_pause_play.setImageResource(R.drawable.ic_play_arrow_white_36dp)
    }

    override fun showProgress() {
        pb_player.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        pb_player.visibility = View.GONE
    }

    override fun setCurrentPosition(currentPosition: Int) {
        tv_time_gone.text = timeFormat.format(currentPosition)
        music_progress.progress = currentPosition
    }

    override fun setBufferPosition(bufferPosition: Int) {
        music_progress.secondaryProgress = bufferPosition
    }

    override fun disablePlayer() {
        img_pause_play.isClickable = false
        music_progress.isEnabled = false
    }

    override fun enablePlayer() {
        music_progress.isEnabled = true
        img_pause_play.isClickable = true
    }

    override fun openPlayer() {
        BottomSheetBehavior.from(bottom_sheet).state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun bindService() {
        val intent = Intent(this, MusicService::class.java)
        startService(intent)
        bindService(intent, mainPresenter, Context.BIND_AUTO_CREATE)
    }

    override fun unbindService() {
        unbindService(mainPresenter)
    }

    private fun initSeekBar() {
        sheet_bar.animate().alpha(0f).setDuration(0).start()
        music_progress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.progress?.let { mainPresenter.onSeek(it) }
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaServiceReceiver?.let {
            unregisterReceiver(it)
        }
        unbindService()
    }

    override fun finish() {
        super.finish()
        mainPresenter.onFinish()
    }

    override fun onBackPressed() {
        val fragment = this.supportFragmentManager.findFragmentById(R.id.container)
        if (fragment is IOnBackPressed && fragment.onBackPressed())
            bottom_nav.selectedItemId = R.id.nav_podcasts
        else
            super.onBackPressed()
    }

    private fun initBroadcastReceiver() {
        mediaServiceReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.apply {
                    getParcelableExtra<PlayInfo>(MusicService.KEY_PLAY_INFO)?.let {
                        mainPresenter.onReceivePlayInfo(it)
                    }
                    getIntExtra(MusicService.KEY_CURRENT_POSITION, -1).let {
                        mainPresenter.onReceivePosition(it)
                    }
                    getIntExtra(MusicService.KEY_BUFFER_POSITION, -1).let {
                        mainPresenter.onReceiveBufferPosition(it)
                    }
                }
            }
        }
        val filter = IntentFilter(MusicService.BROADCAST_ACTION)
        registerReceiver(mediaServiceReceiver, filter)
    }

    private fun initBottomSheet() {
        var oldState = 0

        val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        if (oldState != newState) {
                            oldState = newState
                            sheet_top.animate().alpha(0f).setDuration(300).start()
                            sheet_top.animate()
                                .withEndAction {
                                    sheet_top.animate().alpha(1f).setDuration(300).start()
                                    sheet_bar.animate().alpha(1f).setDuration(300).start()
                                }
                                .setDuration(300)
                                .y(bottom_of_bottom_sheet.y - resources.getDimensionPixelSize(R.dimen.distance).toFloat())
                                .setInterpolator(AccelerateInterpolator()).start()
                            sheet_down.animate().setDuration(300)
                                .translationY(-resources.getDimensionPixelSize(R.dimen.distance).toFloat())
                                .setInterpolator(AccelerateInterpolator()).start()
                        }
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        if (oldState != newState) {
                            oldState = newState
                            sheet_bar.animate().alpha(0f).setDuration(0).start()
                            sheet_top.animate()
                                .withEndAction {
                                    sheet_top.animate().setDuration(300).y(0f).start()
                                }
                                .setDuration(0)
                                .y(resources.getDimensionPixelSize(R.dimen.distance).toFloat())
                            sheet_down.animate().setDuration(300)
                                .translationY(+resources.getDimensionPixelSize(R.dimen.distance).toFloat())
                                .setInterpolator(AccelerateInterpolator()).start()
                        }
                    }
                    else -> {
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        }
        BottomSheetBehavior.from(bottom_sheet).setBottomSheetCallback(bottomSheetCallback)
    }

    private val mOnNavigationItemSelectedListener =
        object : BottomNavigationView.OnNavigationItemSelectedListener {

            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                BottomSheetBehavior.from(bottom_sheet).state = BottomSheetBehavior.STATE_COLLAPSED
                when (item.itemId) {
                    R.id.nav_fav -> {
                        loadFragment(favouriteFragment)
                        return true
                    }
                    R.id.nav_podcasts -> {
                        loadFragment(podcastsFragment)
                        return true
                    }
                    R.id.nav_profile -> {
                        loadFragment(profileFragment)
                        return true
                    }
                }
                return false
            }
        }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }
}
