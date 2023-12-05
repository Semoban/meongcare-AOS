package com.project.meongcare.login.model.data.repository

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.coroutines.tasks.await

class FirebaseCloudMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("new-token", "$token")
    }

    suspend fun getToken(): String {
        return FirebaseMessaging.getInstance().token.await()
    }
}
