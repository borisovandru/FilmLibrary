package com.android.filmlibrary.data.retrofit

import com.google.gson.annotations.SerializedName

data class MovieAPI(

    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("genre_ids")
    val genre_ids: List<Int>,

    @SerializedName("release_date")
    val dateRelease: String?,

    @SerializedName("original_title")
    val originalTitle: String,

    @SerializedName("overview")
    val overview: String,

    @SerializedName("poster_path")
    val posterUrl: String,

    @SerializedName("vote_average")
    val voteAverage: Double,
)
