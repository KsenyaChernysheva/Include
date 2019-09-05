package com.example.include.presentation.feature.mainplayer

import android.opengl.Visibility
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.view.marginBottom
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.include.IncludeApp
import com.example.include.R
import com.example.include.data.podcast.Podcast
import com.example.include.data.track.Track
import com.example.include.presentation.feature.podcastslist.PodcastsFragment
import com.example.include.presentation.feature.profile.ProfileFragment
import com.example.include.presentation.feature.trackslist.IOnBackPressed
import com.example.include.presentation.feature.trackslist.TrackAdapter
import com.example.include.presentation.feature.trackslist.TrackListFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.vk.sdk.VKSdk
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import javax.inject.Inject

class MainActivity : MvpAppCompatActivity(), MainView {

    private var mUpdateSeekbar: Runnable? = null
    var mSeekbarUpdateHandler = Handler()
    private lateinit var transaction: FragmentTransaction
    private var adapter: TrackAdapter = TrackAdapter { onTrackClick(it) }

    private var favFragment = TrackListFragment.newInstance(Podcast("Favourite",""))
    private var podcastsFragment = PodcastsFragment()
    private var profileFragment = ProfileFragment()

    @Inject
    @InjectPresenter
    lateinit var mainPresenter: MainPresenter

    @ProvidePresenter
    fun initPresenter() = mainPresenter


    override fun onCreate(savedInstanceState: Bundle?) {
        IncludeApp.appComponent.inject(this)
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            bottom_nav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
            bottom_nav.selectedItemId = R.id.nav_podcasts
            rv_tracks_list.adapter = adapter
            setPausePlay()
            setSeekBar()
            setBottomSheet()
        } else
            mainPresenter.refreshState()
    }

    private fun onTrackClick(position: Int) =
            mainPresenter.playPosition(position)


    override fun setList(list: List<Track>) {
        adapter.tracks = list
        adapter.notifyDataSetChanged()
    }

    override fun setMusic(duration: Int, track: Track) {
        music_progress.max = duration
        tv_cname.text = track.name
        tv_cgroup.text = track.group
        mUpdateSeekbar = object : Runnable {
            override fun run() {
                updateState()
                mSeekbarUpdateHandler.postDelayed(this, 1000)
            }
        }
    }

    override fun updateState() {
        music_progress.secondaryProgress = mainPresenter.getCurrSecondary()
        music_progress.progress = mainPresenter.getCurr()
        tv_time_gone.text = mainPresenter.getGone()
        tv_time_left.text = mainPresenter.getLeft()
    }

    override fun setPlayState() {
        img_pause_play.setImageResource(R.drawable.ic_pause_white_36dp)
        mSeekbarUpdateHandler.postDelayed(mUpdateSeekbar, 0)
    }

    override fun setPauseState() {
        img_pause_play.setImageResource(R.drawable.ic_play_arrow_white_36dp)
        mSeekbarUpdateHandler.removeCallbacks(mUpdateSeekbar)
    }

    private fun setPausePlay() {
        img_pause_play.setOnClickListener { mainPresenter.playClick() }
    }

    override fun showProgress() {
        pb_player.visibility = View.VISIBLE
    }

    override fun disablePlayer() {
        img_pause_play.isClickable = false
        music_progress.isEnabled = false
    }

    override fun enablePlayer() {
        music_progress.isEnabled = true
        img_pause_play.isClickable = true
        pb_player.visibility = View.GONE
    }

    override fun openPlayer(){
        BottomSheetBehavior.from(bottom_sheet).state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun sendWait() {
        Toast.makeText(this,"Sorry, player is busy. Please wait",Toast.LENGTH_LONG).show()
    }

    private fun setSeekBar() {
        sheet_bar.animate().alpha(0f).setDuration(0).start()
        music_progress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.progress?.let { mainPresenter.seekTo(it) }
                updateState()
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}
        })
    }

    override fun onResume() {
        super.onResume()
        mainPresenter.updateNotification()
    }

    override fun onDestroy() {
        super.onDestroy()
        mSeekbarUpdateHandler.removeCallbacks(mUpdateSeekbar)
    }

    override fun finish() {
        super.finish()
        if (!VKSdk.isLoggedIn())
        mainPresenter.player.p.pause()
    }

    override fun onBackPressed() {
        val fragment = this.supportFragmentManager.findFragmentById(R.id.container)
        if (fragment is IOnBackPressed && fragment.onBackPressed())
            bottom_nav.selectedItemId = R.id.nav_podcasts
        else
            super.onBackPressed()
    }

    private fun setBottomSheet() {
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
                            sheet_down.animate().setDuration(300).translationY(-resources.getDimensionPixelSize(R.dimen.distance).toFloat())
                                    .setInterpolator(AccelerateInterpolator()).start()
                        }
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        if (oldState != newState) {
                            oldState = newState
                            sheet_bar.animate().alpha(0f).setDuration(0).start()
                            sheet_top.animate()
                                    .withEndAction { sheet_top.animate().setDuration(300).y(0f).start() }
                                    .setDuration(0)
                                    .y(resources.getDimensionPixelSize(R.dimen.distance).toFloat())
                            sheet_down.animate().setDuration(300).translationY(+resources.getDimensionPixelSize(R.dimen.distance).toFloat())
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

    private val mOnNavigationItemSelectedListener = object : BottomNavigationView.OnNavigationItemSelectedListener {

        override fun onNavigationItemSelected(item: MenuItem): Boolean {
            BottomSheetBehavior.from(bottom_sheet).state = BottomSheetBehavior.STATE_COLLAPSED
            when (item.itemId) {
                R.id.nav_fav -> {
                    loadFragment(favFragment)
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
        transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }
}
