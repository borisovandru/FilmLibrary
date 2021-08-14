package com.android.filmlibrary.model.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class MoviesByTrend(
    var trend: Trend,
    var movies: List<Movie>
) : Parcelable
