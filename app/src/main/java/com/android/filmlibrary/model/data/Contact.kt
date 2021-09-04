package com.android.filmlibrary.model.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Contact(
    val name: String = "",
    val number: String = "",
): Parcelable
