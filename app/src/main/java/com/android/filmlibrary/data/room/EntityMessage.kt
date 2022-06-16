package com.android.filmlibrary.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class EntityMessage(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val header: String,
    val body: String
)