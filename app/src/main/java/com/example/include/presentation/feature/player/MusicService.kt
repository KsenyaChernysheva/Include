package com.example.include.presentation.feature.player

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ContentValues
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.include.R
import com.example.include.data.history.HistoryElem
import com.example.include.data.track.Track
import com.example.include.presentation.feature.mainplayer.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class MusicService : Service(), MusicServiceBind {

    private val mediaPlayer = MediaPlayer()
    private var tracks = arrayListOf<Track>()
    private var playingNow = -1
    @Volatile
    private var preparing = false
    private val positionSendingLoop = Handler()
    private var positionSendingRunnable: Runnable? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        createServiceNotification()

        positionSendingRunnable = Runnable {
            sendCurrentPosition()
            startPositionSendingLoop()
        }

        mediaPlayer.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )
        mediaPlayer.setOnPreparedListener {
            onPrepared()
        }
        mediaPlayer.setOnBufferingUpdateListener { _, percent ->
            sendBufferPosition(percent)
        }
        mediaPlayer.setOnCompletionListener {
            stopPositionSendingLoop()
            next()
        }
        //WTF?! Skip tracks work without it
        mediaPlayer.setOnErrorListener { _, _, _ -> true }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.release()
        stopPositionSendingLoop()
    }

    override fun onBind(intent: Intent?): IBinder = MBinder(this)

    override fun playpause() {
        if (preparing) return
        if (mediaPlayer.isPlaying)
            mediaPlayer.pause()
        else
            mediaPlayer.start()
        sendStatusUpdate()
    }

    override fun next() {
        if (playingNow < tracks.size - 1) {
            playingNow++
            play(tracks[playingNow])
        }
    }

    override fun previous() {
        if (playingNow > 0) {
            playingNow--
            play(tracks[playingNow])
        }
    }

    override fun seekTo(msec: Int) {
        if (playingNow >= 0) {
            mediaPlayer.seekTo(msec)
        }
        sendCurrentPosition()
    }

    override fun playFromList(list: ArrayList<Track>, position: Int) {
        tracks = list
        playingNow = position
        play(tracks[playingNow])
    }

    override fun playFromList(position: Int) {
        playingNow = position
        play(tracks[playingNow])
    }

    override fun getTracks(): List<Track> = tracks

    override fun stop() = stopSelf()

    private fun onPrepared() {
        preparing = false
        mediaPlayer.start()
        sendStatusUpdate()
        startPositionSendingLoop()
    }

    private fun play(track: Track) {
        preparing = true
        mediaPlayer.reset()
        mediaPlayer.setDataSource(track.url)
        mediaPlayer.prepareAsync()
        createServiceNotification()
        sendStatusUpdate()
        updateHistory()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            mChannel.description = descriptionText
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    private fun createServiceNotification() {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setSmallIcon(R.mipmap.ic_launcher)
            if (playingNow >= 0) {
                setContentTitle(tracks[playingNow].group)
                setContentText(tracks[playingNow].name)
            }
            setContentIntent(pendingIntent)
        }.build()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun sendStatusUpdate() {
        if (playingNow < 0) return
        val intent = Intent(BROADCAST_ACTION).apply {
            putExtra(
                KEY_PLAY_INFO, PlayInfo(
                    tracks[playingNow],
                    mediaPlayer.duration,
                    mediaPlayer.isPlaying,
                    preparing
                )
            )
        }
        sendBroadcast(intent)
    }

    private fun sendBufferPosition(bufferPercent: Int) {
        val intent = Intent(BROADCAST_ACTION).apply {
            putExtra(KEY_BUFFER_POSITION, bufferPercent * mediaPlayer.duration / 100)
        }
        sendBroadcast(intent)
    }

    private fun startPositionSendingLoop() =
        positionSendingLoop.postDelayed(positionSendingRunnable, POSITION_SEND_DELAY)


    private fun stopPositionSendingLoop() =
        positionSendingLoop.removeCallbacks(positionSendingRunnable)

    private fun sendCurrentPosition() {
        val intent = Intent(BROADCAST_ACTION).apply {
            putExtra(KEY_CURRENT_POSITION, mediaPlayer.currentPosition)
        }
        sendBroadcast(intent)
    }

    private fun updateHistory() {
        val updates = hashMapOf<String, Any>(
            "time" to FieldValue.serverTimestamp()
        )
        FirebaseFirestore.getInstance()
            .collection("history")
            .whereEqualTo("user_id", FirebaseAuth.getInstance().currentUser?.uid)
            .whereEqualTo("track_url", tracks[playingNow].url)
            .get()
            .addOnSuccessListener { result ->
                for (his in result)
                    FirebaseFirestore.getInstance()
                        .collection("history")
                        .document(his.id)
                        .update(updates)
                if (result.isEmpty)
                    FirebaseFirestore.getInstance()
                        .collection("history")
                        .add(
                            HistoryElem(
                                null,
                                FirebaseAuth.getInstance().currentUser?.uid,
                                tracks[playingNow].url
                            )
                        )
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error getting documents: ", exception)
            }
    }

    companion object {
        const val POSITION_SEND_DELAY = 1000L

        const val CHANNEL_ID = "911"
        const val NOTIFICATION_ID = 112
        const val KEY_PLAY_INFO = "play info"
        const val KEY_BUFFER_POSITION = "buffer position"
        const val KEY_CURRENT_POSITION = "current position"
        const val BROADCAST_ACTION = "com.example.include.PlayerState"
    }

    class MBinder(
        private val musicServiceBind: MusicServiceBind
    ) : Binder() {
        fun getService() = musicServiceBind
    }
}