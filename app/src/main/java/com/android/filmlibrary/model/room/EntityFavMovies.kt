package com.android.filmlibrary.model.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class EntityFavMovies(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val idMovie: Long
)