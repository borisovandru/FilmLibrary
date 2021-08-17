package com.android.filmlibrary.model.retrofit

import com.android.filmlibrary.model.data.Country
import com.android.filmlibrary.model.data.Genre
import com.google.gson.annotations.SerializedName

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

