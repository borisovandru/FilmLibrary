package com.android.filmlibrary.model.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Movie(
    val id: Int = -1,
    val title: String = "Default movie",
    val year: Int = 1900,
    val category: Category,
    val dateRelease: String,
    val description: String,
    val posterUrl: String
) : Parcelable