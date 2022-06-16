package com.android.filmlibrary.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PersonMini(
    val id: Int,
    val name: String,
    val profilePath: String?,
) : Parcelable
