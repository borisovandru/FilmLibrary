package com.android.filmlibrary.model.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [(EntityFavMovies::class), (EntityMovies::class), (EntitySearchHistory::class), (EntityMovieNote::class)], version = 1, exportSchema = false)
abstract class DataBase : RoomDatabase() {
    abstract fun historyDao(): DAO
}