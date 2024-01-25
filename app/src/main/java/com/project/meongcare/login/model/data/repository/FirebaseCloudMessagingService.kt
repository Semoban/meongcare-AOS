package com.project.meongcare.login.model.data.repository

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.project.meongcare.MainActivity
import com.project.meongcare.R
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirebaseCloudMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("new-token", "$token")
    }

    suspend fun getToken(): String {
        return FirebaseMessaging.getInstance().token.await()
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.i("messageReceived", "$message")

        if (message.data.isNotEmpty()) {
            sendNotification(message.data)
        }
    }

    fun getNotificationBuilder(channerId: String): NotificationCompat.Builder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder(this@FirebaseCloudMessagingService, channerId)
        } else {
            NotificationCompat.Builder(this@FirebaseCloudMessagingService)
        }
    }

    fun createNotificationChannel(
        channelId: String,
        channelName: String,
        notificationBuilder: NotificationCompat.Builder,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val channel = notificationManager.getNotificationChannel(channelId)

            if (channel == null) {
                val newChannel =
                    NotificationChannel(
                        channelId,
                        channelName,
                        NotificationManager.IMPORTANCE_HIGH,
                    )
                newChannel.enableVibration(true)
                notificationManager.createNotificationChannel(newChannel)
            }
            val requestCode = UUID.randomUUID().hashCode()
            notificationManager.notify(requestCode, notificationBuilder.build())
        }
    }

    fun sendNotification(messageData: Map<String, String>) {
        val messageBody = messageData["body"]
        val messageTitle = messageData["title"]
        val messageLogoImageUrl = messageData["logoImageUrl"]!!

        val intent = Intent(this@FirebaseCloudMessagingService, MainActivity::class.java)
        intent.putExtra("moveFragment", "NoticeFragment")
        intent.addFlags(FLAG_ACTIVITY_SINGLE_TOP)

        val pendingIntent =
            NavDeepLinkBuilder(this@FirebaseCloudMessagingService)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.supplementFragment)
            .createPendingIntent()

        val notificationBuilder =
            getNotificationBuilder(PUSH_NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentText(messageBody)
            .setContentTitle(messageTitle)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.semoban_icon)

        createNotificationChannel(
            PUSH_NOTIFICATION_CHANNEL_ID,
            PUSH_NOTIFICATION_CHANNEL_NAME,
            notificationBuilder,
        )
    }

    companion object {
        const val PUSH_NOTIFICATION_CHANNEL_ID = "SemobanNotificationChannel"
        const val PUSH_NOTIFICATION_CHANNEL_NAME = "SemobanNoticifationChannel"
    }
}
