package com.android.filmlibrary.data.retrofit

import com.google.gson.annotations.SerializedName

data class GenresAPI(
    @SerializedName("genres")
    val results: List<GenreAPI>,
)
