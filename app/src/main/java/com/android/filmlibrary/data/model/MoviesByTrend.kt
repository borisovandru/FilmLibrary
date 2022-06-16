package com.android.filmlibrary.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MoviesByTrend(
    var trend: Trend,
    var moviesList: MoviesList
) : Parcelable