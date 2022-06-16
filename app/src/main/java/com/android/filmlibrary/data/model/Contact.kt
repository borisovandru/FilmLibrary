package com.android.filmlibrary.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Contact(
    val name: String = "",
    val numbers: List<String> = listOf(),
) : Parcelable
