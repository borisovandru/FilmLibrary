package com.android.filmlibrary.data.retrofit

import com.google.gson.annotations.SerializedName

data class ImagesAPI(
    @SerializedName("base_url")
    val baseURL: String,
    @SerializedName("secure_base_url")
    val secureBaseURL: String,
    @SerializedName("backdrop_sizes")
    val backdropSizes: List<String>,
    @SerializedName("logo_size")
    val logoSize: List<String>,
    @SerializedName("poster_sizes")
    val posterSizes: List<String>,
    @SerializedName("profile_sizes")
    val profileSizes: List<String>,
    @SerializedName("still_sizes")
    val stillSizes: List<String>
)