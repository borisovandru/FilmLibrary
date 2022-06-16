package com.android.filmlibrary.data.retrofit

import com.google.gson.annotations.SerializedName

data class CreditsAPI(
    @SerializedName("id")
    val id: Int,
    @SerializedName("cast")
    val cast: List<CastAPI>,
    @SerializedName("crew")
    val crew: List<CrewAPI>
)