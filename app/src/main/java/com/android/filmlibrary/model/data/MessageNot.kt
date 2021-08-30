package com.android.filmlibrary.model.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MessageNot(
    val id: Int,
    val header: String,
    val body: String,
) : Parcelable
