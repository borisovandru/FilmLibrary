package com.android.filmlibrary.model.retrofit

import com.google.gson.annotations.SerializedName

data class ConfigurationAPI(
    @SerializedName("images")
    val images: ImagesAPI,

    @SerializedName("change_keys")
    val changeKeys: List<String>,
)