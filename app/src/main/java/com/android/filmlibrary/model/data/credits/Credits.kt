package com.android.filmlibrary.model.data.credits

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Credits(
    val id: Int,
    val cast: List<Cast>,
    val crew: List<Crew>,
) : Parcelable
