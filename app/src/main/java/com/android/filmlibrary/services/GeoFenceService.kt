package com.android.filmlibrary.services

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import android.app.NotificationManager
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.android.filmlibrary.R

class GeoFenceService : IntentService("GeoFenceService") {
    private var messageId = 0

    init {
        Log.v("GeoFence", "constructor")
    }

    override fun onCreate() {
        Log.v("GeoFence", "on create")
        super.onCreate()
    }

    // именно здесь будет обработка события
    override fun onHandleIntent(intent: Intent?) {
        Log.v("GeoFence", "on handle")
        val geofencingEvent: GeofencingEvent =
            GeofencingEvent.fromIntent(intent) // получаем событие
        val transitionType: Int = geofencingEvent.getGeofenceTransition() // определяем тип события
        val triggeredGeofences: List<Geofence> =
            geofencingEvent.getTriggeringGeofences() // если надо, получаем, какие геозоны нам подходят
        val idFence: String = triggeredGeofences[0].getRequestId()
        makeNote(idFence, transitionType) // отправляем уведомление
    }

    // вывод уведомления в строке состояния
    private fun makeNote(idFence: String, transitionType: Int) {
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, "2")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Near $idFence")
            .setContentText(getTransitionTypeString(transitionType))
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(messageId++, builder.build())
    }

    // возвращает строку с типом события
    private fun getTransitionTypeString(transitionType: Int): String {
        return when (transitionType) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> "enter"
            Geofence.GEOFENCE_TRANSITION_EXIT -> "exit"
            Geofence.GEOFENCE_TRANSITION_DWELL -> "dwell"
            else -> "unknown"
        }
    }
}