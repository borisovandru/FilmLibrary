package com.android.filmlibrary.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchResult(
    val searchResult: MoviesList,
    val searchString: String
) : Parcelable
