package com.android.filmlibrary.model.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Language(
    val isoName: String,
    val english_name: String,
    val name: String,
) : Parcelable
