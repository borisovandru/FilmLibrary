package com.android.filmlibrary.model.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MoviesByTrend(
    var trend: Trend,
    var movieAdvs: List<Movie>
) : Parcelable
