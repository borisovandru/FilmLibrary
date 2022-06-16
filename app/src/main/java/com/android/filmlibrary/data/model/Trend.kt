package com.android.filmlibrary.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Trend(
    var name: String,
    var URL: String
) : Parcelable
