package com.android.filmlibrary.model.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class EntityMovies(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val title: String,
)
