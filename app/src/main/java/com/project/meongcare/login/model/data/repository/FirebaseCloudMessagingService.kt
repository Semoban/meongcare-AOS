package com.project.meongcare.login.model.data.repository

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.project.meongcare.LocaleDateTimeFormats
import com.project.meongcare.R
import com.project.meongcare.login.model.entities.FcmNotificationContent
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
        val (title, body) = composeNotificationText(messageData)

        val pendingIntent =
            NavDeepLinkBuilder(this@FirebaseCloudMessagingService)
                .setGraph(R.navigation.nav_graph)
                .setDestination(R.id.supplementFragment)
                .createPendingIntent()

        val notificationBuilder =
            getNotificationBuilder(getString(R.string.push_notification_channel_id))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentText(body)
                .setContentTitle(title)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.semoban_notification_icon)

        createNotificationChannel(
            getString(R.string.push_notification_channel_id),
            getString(R.string.push_notification_channel_name),
            notificationBuilder,
        )
    }

    // 서버는 구조화 키(notificationType, dogName 등)와 함께 폴백용 한국어 완성 문구(title/body)를
    // 보낸다. 구조화 키가 온전할 때만 클라이언트 로케일로 조립하고, 아니면 폴백을 그대로 쓴다.
    private fun composeNotificationText(messageData: Map<String, String>): Pair<String?, String?> {
        return when (val content = FcmNotificationContent.from(messageData)) {
            is FcmNotificationContent.Supplements ->
                getString(
                    R.string.push_supplements_title,
                    content.intakeTime.format(LocaleDateTimeFormats.time12h()),
                    content.supplementsName,
                ) to getString(R.string.push_supplements_body, content.dogName)

            is FcmNotificationContent.ShareDog ->
                getString(R.string.push_share_dog_title, content.requesterEmail) to
                    getString(R.string.push_share_dog_body, content.dogName)

            is FcmNotificationContent.Fallback -> content.title to content.body
        }
    }
}
