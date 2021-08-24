package com.android.filmlibrary.model.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SettingsTMDB(
    var imageBaseURL: String = "",
    var imageSecureBaseURL: String = "",
) : Parcelable