package com.android.filmlibrary.model.repository.local.db

import com.android.filmlibrary.model.data.Movie
import com.android.filmlibrary.model.room.EntityFavMovies
import com.android.filmlibrary.model.room.EntityMessage

interface RepositoryLocalDB {
    fun getFavoriteMovies(): List<EntityFavMovies>
    fun getFavItem(idMovie: Long): Long
    fun addFavoriteMovie(movie: Movie): Long
    fun removeFavoriteMovies(idMovie: Long): Int

    fun getMovieNote(idMovie: Long): String
    fun updateMovieNote(idMovie: Long, note: String): Int
    fun insertMovieNote(idMovie: Long, note: String): Long
    fun removeMovieNote(idMovie: Long): Int

    fun getSearchHistory(): List<String>
    fun addSearchQuery(query: String)

    fun getMessages(): List<EntityMessage>
    fun addMessage(header: String, body: String): Long
}