package com.android.filmlibrary.services

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.android.filmlibrary.utils.Constant.NEW_MESSAGE
import com.android.filmlibrary.utils.GlobalVariables
import com.android.filmlibrary.R
import com.android.filmlibrary.domain.local.db.RepositoryLocalDBImpl

class MyFirebaseInstanceIDService : FirebaseMessagingService() {

    private val repositoryLocal = RepositoryLocalDBImpl(GlobalVariables.getDAO())
    private var broadcaster: LocalBroadcastManager? = null

    override fun onCreate() {
        broadcaster = LocalBroadcastManager.getInstance(this)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.v("Debug1", "MyFirebaseInstanceIDService onMessageReceived")
        val title = remoteMessage.notification?.title ?: PUSH_KEY_TITLE
        val body = remoteMessage.notification?.body ?: PUSH_KEY_MESSAGE

        repositoryLocal.addMessage(title, body)

        showNotification(title, body)

        val intent = Intent(NEW_MESSAGE)
        intent.putExtra(NEW_MESSAGE, 1)
        broadcaster?.let {
            sendBroadcast(intent)
        }
    }

    private fun showNotification(title: String, message: String) {
        Log.v("Debug1", "MyFirebaseInstanceIDService showNotification title=$title")

        val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID).apply {
            setSmallIcon(R.drawable.ic_kotlin_logo)
            setContentTitle(title)
            setContentText(message)
            priority = NotificationCompat.PRIORITY_DEFAULT
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    override fun onNewToken(token: String) {
        Log.v("Debug1", "MyFirebaseInstanceIDService onNewToken token=$token")
    }

    companion object {
        private const val PUSH_KEY_TITLE = "title"
        private const val PUSH_KEY_MESSAGE = "message"
        private const val CHANNEL_ID = "channel_id"
        private const val NOTIFICATION_ID = 10
    }
}