package com.android.filmlibrary.model.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Person(
    val adult: Boolean?,
    val alsoKnownAs: List<String>?,
    val biography: String?,
    val birthday: String?,
    val deathday: String?,
    val gender: Int?,
    val homepage: String?,
    val id: Int,
    val imdbId: String?,
    val knownForDepartment: String?,
    val name: String,
    val placeOfBirth: String?,
    val popularity: Double?,
    val profilePath: String?,
) : Parcelable