package com.android.filmlibrary.model.retrofit

import com.google.gson.annotations.SerializedName

data class MoviesListAPI(
    @SerializedName("results")
    val results: List<MovieAPI>,

    @SerializedName("total_pages")
    val totalPages: Int,

    @SerializedName("total_results")
    val totalResults: Int
)