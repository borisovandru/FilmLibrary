package com.android.filmlibrary.model.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Movie(
    val id: Int = -1,
    val title: String = "Default movie",
    val year: String = "",
    val genre_ids: List<Int>,
    val dateRelease: String?,
    val originalTitle: String,
    val overview: String,
    val posterUrl: String?,
    val voteAverage: Double
) : Parcelable