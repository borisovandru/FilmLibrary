package com.android.filmlibrary.model.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MoviesList(
    var results: List<Movie>,
    val totalPages: Int,
    val totalResults: Int,
) : Parcelable
