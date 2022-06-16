package com.android.filmlibrary.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MessageNot(
    val id: Int,
    val header: String,
    val body: String,
) : Parcelable
