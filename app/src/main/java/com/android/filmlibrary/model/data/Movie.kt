package com.android.filmlibrary.model.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Movie(
    val name: String,
    var image: Int,
    val rating: Int,
    val date: String,
    val description: String
) :
    Parcelable