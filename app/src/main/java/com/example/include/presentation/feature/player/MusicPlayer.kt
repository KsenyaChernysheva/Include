package com.example.include.presentation.feature.player

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.net.Uri
import android.widget.Toast
import com.example.include.data.track.Track
import com.example.include.presentation.feature.mainplayer.MainPresenter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import com.example.include.R
import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.ContentValues
import android.os.*
import android.util.Log
import com.example.include.data.history.HistoryElem
import com.example.include.presentation.feature.mainplayer.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore




class MusicPlayer : Service() {

    lateinit var p: MediaPlayer

    private lateinit var mainPresenter: MainPresenter

    private lateinit var sp: SharedPreferences

    private var binder = MBinder()

    var prepared: Boolean = false

    var restored: Boolean = false

    private var busy: Boolean = false

    private var curr = 0

    private var tracks = arrayListOf<Track>()

    private var h = Handler {
        when (it.what) {
            0 -> mainPresenter.disablePlayer()
            1 -> {
                mainPresenter.ablePlayer()
                mainPresenter.setMusic(tracks[curr])
            }
            2 -> Toast.makeText(this, "Error loading track", Toast.LENGTH_LONG).show()
            3 -> mainPresenter.setList(tracks)
            4->mainPresenter.sendWait()
        }
        return@Handler true
    }

    private fun start() {
        if (!busy)
            Thread(Runnable {
                busy = true
                h.sendEmptyMessage(0)
                try {
                    p = MediaPlayer.create(this@MusicPlayer, Uri.parse(tracks[curr].url))
                    p.start()
                    if (restored) {
                        p.pause()
                        restored = !restored
                    }
                    prepared = true
                    p.setOnCompletionListener { next() }
                    h.sendEmptyMessage(1)
                    updateNotification()
                    updateHistory()
                    busy = false
                } catch (e: Exception) {
                    busy = false
                    h.sendEmptyMessage(2)
                    next()
                }
            }).start()
        else
            h.sendEmptyMessage(4)
    }

    fun next() {
        curr++
        curr = if (curr == tracks.size) 0 else curr
        sp.edit().putInt(CURR, curr).apply()
        if (prepared) {
            mainPresenter.setChangeState()
            p.release()
        }
        start()
    }

    fun pre() {
        curr--
        curr = if (curr == -1) tracks.size - 1 else curr
        p.release()
        start()
    }

    fun prepare(mainPresenter: MainPresenter) {
        this.mainPresenter = mainPresenter
        setUpNotification()
        mainPresenter.setListChangeState()
        Thread(Runnable {
            sp = getSharedPreferences(MY_SETTINGS, Context.MODE_PRIVATE)
            val savedTracks = sp.getString(TRACKS, null)
            if (savedTracks != null) {
                tracks = Gson().fromJson(savedTracks, object : TypeToken<List<Track>>() {}.type)
                curr = sp.getInt(CURR, 0)
                h.sendEmptyMessage(3)
                restored = true
                start()
            }
        }).start()
    }

    fun playListFrom(list: ArrayList<Track>, position: Int) {
        if (prepared && !busy)
            mainPresenter.setListChangeState()
        if (!busy)
            Thread(Runnable {
                sp.edit().putString(TRACKS, Gson().toJson(list)).apply()
                tracks = list
                h.sendEmptyMessage(3)
                playPosition(position)
            }).start()
        else
            h.sendEmptyMessage(4)
    }

    fun playPosition(position: Int) {
        if (!busy)
            Thread(Runnable {
                curr = position
                sp.edit().putInt(CURR, curr).apply()
                if (prepared) {
                    p.release()
                }
                start()
            }).start()
        else
            h.sendEmptyMessage(4)
    }

    fun getCurrTrack(): Track = tracks[curr]

    private fun setUpNotification() {
        val intent = Intent(this, MainActivity::class.java)
        val piMain = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT > 25) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("1414", "channel", importance)
            notificationManager.createNotificationChannel(channel)
        }
        val builder = NotificationCompat.Builder(this, "1414")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("No track selected")
            .setContentText("...")
            .setContentIntent(piMain)
        val notification = builder.build()
        notificationManager.notify(1, notification)
    }

    fun updateNotification(){
        val intent = Intent(this, MainActivity::class.java)
        val piMain = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(this, "1414")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(tracks[curr].name)
            .setContentText(tracks[curr].group)
            .setContentIntent(piMain)
        val notification = builder.build()
        notificationManager.notify(1, notification)
    }


    private fun updateHistory() {
        val updates = hashMapOf<String, Any>(
            "time" to FieldValue.serverTimestamp()
        )
        FirebaseFirestore.getInstance()
            .collection("history")
            .whereEqualTo("user_id",FirebaseAuth.getInstance().currentUser?.uid)
            .whereEqualTo("track_url",tracks[curr].url)
            .get()
            .addOnSuccessListener { result->
                for (his in result)
                    FirebaseFirestore.getInstance()
                        .collection("history")
                        .document(his.id)
                        .update(updates)
                if (result.isEmpty)
                    FirebaseFirestore.getInstance()
                        .collection("history")
                        .add(HistoryElem(null,FirebaseAuth.getInstance().currentUser?.uid,tracks[curr].url))
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error getting documents: ", exception)
            }
    }

    override fun onBind(intent: Intent?): IBinder? = binder

    override fun onDestroy() {
        super.onDestroy()
        if (prepared) {
            p.release()
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(1)
        }
    }

    inner class MBinder : Binder() {
        val service: MusicPlayer
            get() = this@MusicPlayer
    }

    companion object {
        private const val MY_SETTINGS = "my_settings"
        private const val CURR = "CURR"
        private const val TRACKS = "TRACKS"
    }

}