package com.android.filmlibrary.data.retrofit

import com.google.gson.annotations.SerializedName
import com.android.filmlibrary.data.model.Country
import com.android.filmlibrary.data.model.Genre

data class MovieAdvAPI(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("genres")
    val genres: List<Genre>,
    @SerializedName("production_countries")
    val countries: List<Country>,
    @SerializedName("release_date")
    val dateRelease: String,
    @SerializedName("original_title")
    val originalTitle: String,
    @SerializedName("overview")
    val overview: String,
    @SerializedName("poster_path")
    val posterUrl: String,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("runtime")
    val runtime: Int
)



