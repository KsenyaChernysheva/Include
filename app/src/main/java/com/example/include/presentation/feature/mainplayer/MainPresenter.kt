package com.example.include.presentation.feature.mainplayer

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.include.presentation.feature.player.MusicService
import com.example.include.presentation.feature.player.MusicServiceBind
import com.example.include.presentation.feature.player.PlayInfo
import com.vk.sdk.VKSdk
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
@InjectViewState
class MainPresenter
@Inject constructor() : MvpPresenter<MainView>(), ServiceConnection {

    private var musicService: MusicServiceBind? = null

    override fun onFirstViewAttach() {
        viewState.disablePlayer()
    }

    fun onViewAttach() = musicService?.sendPlayInfo()

    override fun onServiceDisconnected(name: ComponentName?) {
        viewState.unbindService()
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        musicService = (service as? MusicService.MBinder)?.getService()
        musicService?.getTracks()?.let { viewState.setTracksList(it) }
    }

    fun onPlayPauseClick() = musicService?.playpause()

    fun onTrackSelected(trackPosition: Int) = musicService?.playFromList(trackPosition)

    fun onSeek(position: Int) = musicService?.seekTo(position)

    fun onReceivePlayInfo(playInfo: PlayInfo) {
        musicService?.getTracks()?.let { viewState.setTracksList(it) }
        viewState.enablePlayer()
        viewState.setTrackInfo(playInfo.track, playInfo.duration)
        if (playInfo.isPreparing) {
            viewState.showProgress()
        } else {
            viewState.hideProgress()
        }
        if (playInfo.isPlaying) {
            viewState.setPlayState()
        } else {
            viewState.setPauseState()
        }
    }

    fun onReceivePosition(position: Int) {
        if (position > 0)
            viewState.setCurrentPosition(position)
    }

    fun onReceiveBufferPosition(bufferPosition: Int) {
        if (bufferPosition > 0)
            viewState.setBufferPosition(bufferPosition)
    }

    fun onFinish() {
        if (!VKSdk.isLoggedIn())
            musicService?.stop()
    }
}
