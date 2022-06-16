package com.android.filmlibrary.data.retrofit

import com.google.gson.annotations.SerializedName

data class GenreAPI(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,
)