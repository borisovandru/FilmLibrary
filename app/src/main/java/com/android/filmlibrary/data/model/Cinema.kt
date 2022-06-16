package com.android.filmlibrary.data.model

import android.app.PendingIntent
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Cinema(
    val name: String = "",
    val coordinates: LatLng,
    var pendingIntent: PendingIntent?
) : Parcelable