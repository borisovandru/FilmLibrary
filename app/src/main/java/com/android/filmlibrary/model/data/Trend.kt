package com.android.filmlibrary.model.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Trend(
    var name: String,
    var URL: String
) : Parcelable
