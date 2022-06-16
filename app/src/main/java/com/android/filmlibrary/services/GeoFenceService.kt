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

    @Deprecated("Deprecated in Java")
    override fun onCreate() {
        Log.v("GeoFence", "on create")
        super.onCreate()
    }

    // именно здесь будет обработка события
    @Deprecated("Deprecated in Java")
    override fun onHandleIntent(intent: Intent?) {
        Log.v("GeoFence", "on handle")
        val geofencingEvent: GeofencingEvent =
            GeofencingEvent.fromIntent(intent!!) // получаем событие
        val transitionType: Int = geofencingEvent.geofenceTransition // определяем тип события
        val triggeredGeofences: List<Geofence> =
            geofencingEvent.triggeringGeofences // если надо, получаем, какие геозоны нам подходят
        val idFence: String = triggeredGeofences.first().requestId
        makeNote(idFence, transitionType) // отправляем уведомление
    }

    // вывод уведомления в строке состояния
    private fun makeNote(idFence: String, transitionType: Int) {
        Log.v("GeoFence", "makeNote")
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, "2")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(getString(R.string.Near) + idFence)
            .setContentText(getTransitionTypeString(transitionType))
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(messageId++, builder.build())
    }

    // возвращает строку с типом события
    private fun getTransitionTypeString(transitionType: Int): String {
        Log.v("GeoFence", "getTransitionTypeString")
        return when (transitionType) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> getString(R.string.EnterGoeFence)
            Geofence.GEOFENCE_TRANSITION_EXIT -> getString(R.string.ExitGoeFence)
            Geofence.GEOFENCE_TRANSITION_DWELL -> getString(R.string.DwellGoeFence)
            else -> getString(R.string.UnknowGoeFence)
        }
    }
}