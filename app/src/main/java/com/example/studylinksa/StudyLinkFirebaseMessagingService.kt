package com.example.studylinksa

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class StudyLinkFirebaseMessagingService :
    FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {

        val title = message.notification?.title
            ?: message.data["title"]
            ?: "StudyLink SA"

        val body = message.notification?.body
            ?: message.data["body"]
            ?: "You have a new notification."

        val id = System.currentTimeMillis().toInt()

        NotificationHelper.showNotification(
            this,
            title,
            body,
            id
        )
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        // A refreshed FCM token can be registered with
        // your backend when device-token registration is implemented.
    }
}