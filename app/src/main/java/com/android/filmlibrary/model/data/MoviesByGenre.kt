package com.android.filmlibrary.model.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MoviesByGenre(
    var genre: Genre,
    var movies: MoviesList
) : Parcelable
