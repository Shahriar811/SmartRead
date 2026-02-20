package com.example.smartread.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.smartread.timer.TimerPhase

object NotificationHelper {
    private const val CHANNEL_ID = "timer_channel"
    private const val CHANNEL_NAME = "Timer Notifications"
    private const val NOTIFICATION_ID = 1

    fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications for study and reward timer"
            enableVibration(true)
            enableLights(true)
        }
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun showTimerCompleteNotification(context: Context, phase: TimerPhase) {
        val (title, message) = when (phase) {
            TimerPhase.Study -> "📚 Study Time Complete!" to "Time for your reward break!"
            TimerPhase.Reward -> "🎉 Reward Time Complete!" to "Ready to start studying again?"
            TimerPhase.Idle -> return
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
