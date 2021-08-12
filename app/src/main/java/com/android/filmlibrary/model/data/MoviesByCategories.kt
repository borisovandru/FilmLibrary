package com.android.filmlibrary.model.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MoviesByCategories(
    var category: Category,
    var movies: List<Movie>
) : Parcelable
