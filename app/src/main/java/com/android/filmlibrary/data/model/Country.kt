package com.android.filmlibrary.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Country(
    val codeISO: String = "",
    val name: String = ""
) : Parcelable
