package com.android.filmlibrary.model.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MovieAdv(
    val id: Int = -1,
    val title: String = "Default movie",
    val year: Int = 1900,
    val genres: List<Genre>,
    val countries: List<Country>,
    val dateRelease: String,
    val originalTitle: String,
    val overview: String,
    val posterUrl: String,
    val voteAverage: Double,
    val runtime: Int
) : Parcelable
