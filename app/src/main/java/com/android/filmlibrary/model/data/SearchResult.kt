package com.android.filmlibrary.model.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchResult(
    val searchResult: MoviesList,
    val searchString: String
) : Parcelable
