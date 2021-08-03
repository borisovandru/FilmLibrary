package com.android.filmlibrary.model.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Movie(
    val name: String,
    var image: Int,
    val reting: Int,
    val date: String,
    val descreotion: String
) :
    Parcelable