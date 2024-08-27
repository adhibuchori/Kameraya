package com.adhibuchori.kameraya.utils.notification

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.adhibuchori.kameraya.R

class KamerayaNotification(
    private val context: Context,
) {

    private var channelId = "kameraya_app_notification"

    operator fun invoke(
        title: String,
        description: String,
        pendingIntent: PendingIntent? = null,
    ) {
        val notification =
            createNotification(title = title, description = description, pendingIntent)
        val notificationManager = NotificationManagerCompat.from(context)

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationManager.notify(1, notification)
    }

    private fun createNotification(
        title: String,
        description: String,
        pendingIntent: PendingIntent? = null,
    ): Notification {
        createNotificationChannel()
        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.iv_app_logo_kameraya)
            .setContentTitle(title)
            .setContentText(description)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
    }

    private fun createNotificationChannel() {
        val channelName = "Kameraya Notification"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = "Kameraya Channel Description"
        }
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}