package com.example.smartread.utils

import android.content.Context
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AlarmSoundHelper(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private var stopJob: Job? = null
    private val handler = Handler(Looper.getMainLooper())

    fun playAlarm(durationSeconds: Int = 10) {
        stopAlarm() // Stop any existing alarm

        try {
            // Use system default notification sound
            val alarmUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                ?: android.provider.Settings.System.DEFAULT_NOTIFICATION_URI

            mediaPlayer = MediaPlayer.create(context, alarmUri).apply {
                isLooping = true
                setVolume(1.0f, 1.0f)
                start()
            }

            // Stop after durationSeconds
            stopJob = CoroutineScope(Dispatchers.Main).launch {
                delay(durationSeconds * 1000L)
                stopAlarm()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback: try system beep
            try {
                val beepUri = Uri.parse("android.resource://${context.packageName}/android.R.raw.beep")
                mediaPlayer = MediaPlayer.create(context, beepUri).apply {
                    isLooping = true
                    setVolume(1.0f, 1.0f)
                    start()
                }
                stopJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(durationSeconds * 1000L)
                    stopAlarm()
                }
            } catch (e2: Exception) {
                e2.printStackTrace()
            }
        }
    }

    fun stopAlarm() {
        stopJob?.cancel()
        stopJob = null
        mediaPlayer?.let {
            try {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            mediaPlayer = null
        }
    }

    fun release() {
        stopAlarm()
    }
}
