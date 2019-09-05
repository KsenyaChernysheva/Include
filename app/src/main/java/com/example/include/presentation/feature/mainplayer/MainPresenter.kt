package com.example.include.presentation.feature.mainplayer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.include.data.track.Track
import com.example.include.presentation.feature.player.MusicPlayer
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
@InjectViewState
class MainPresenter
@Inject constructor(val app: Context) : MvpPresenter<MainView>(), ServiceConnection {

    var secondary: Int = 0

    lateinit var player: MusicPlayer

    override fun onFirstViewAttach() {
        viewState.disablePlayer()
        val intent = Intent(app, MusicPlayer::class.java)
        app.startService(intent)
        app.bindService(intent, this, Context.BIND_AUTO_CREATE)
    }

    fun setList(list: ArrayList<Track>) {
        viewState.setList(list)
    }

    fun setMusic(track: Track) {
        secondary = player.p.duration
        player.p.setOnBufferingUpdateListener { _, percent ->
            secondary = ((percent.toDouble() / 100) * player.p.duration).toInt()
        }
        viewState.setMusic(player.p.duration, track)
        if (player.p.isPlaying)
            viewState.setPlayState()
        else
            viewState.setPauseState()
    }

    fun refreshState() {
        if (player.prepared) {
            setMusic(player.getCurrTrack())
            viewState.updateState()
        }
    }

    fun playPosition(position: Int) {
        setChangeState()
        player.playPosition(position)
    }

    fun getGone(): String = getMinuteFormat(player.p.currentPosition)

    fun getLeft(): String = getMinuteFormat(player.p.duration - player.p.currentPosition)

    fun getCurr(): Int = player.p.currentPosition

    fun getCurrSecondary(): Int = secondary

    fun seekTo(it: Int) {
        if (it < secondary)
            player.p.seekTo(it)
    }

    fun playClick() {
        if (player.p.isPlaying) {
            player.p.pause()
            viewState.setPauseState()
        } else {
            player.p.start()
            viewState.setPlayState()
        }
    }

    private fun getMinuteFormat(milli: Int): String {
        val sec = milli / 1000
        val minutes = sec / 60
        val seconds = sec % 60
        var zero = "0"
        if (seconds > 9)
            zero = ""
        return "$minutes:$zero$seconds"
    }

    fun disablePlayer() = viewState.disablePlayer()

    fun ablePlayer() = viewState.enablePlayer()

    override fun onServiceDisconnected(name: ComponentName?) {
        app.unbindService(this)
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val b = service as MusicPlayer.MBinder
        player = b.service
        player.prepare(this)
    }

    fun setChangeState() {
        viewState.showProgress()
        viewState.setPauseState()
        disablePlayer()
    }

    fun setListChangeState(){
        setChangeState()
        viewState.openPlayer()
    }

    fun sendWait() {
        viewState.sendWait()
    }

    fun updateNotification() {
        if (::player.isInitialized && player.prepared)
        player.updateNotification()
    }
}
