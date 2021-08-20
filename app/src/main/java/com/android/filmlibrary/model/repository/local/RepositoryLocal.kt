package com.android.filmlibrary.model.repository.local

import com.android.filmlibrary.model.data.Movie
import com.android.filmlibrary.model.room.EntityFavMovies

interface RepositoryLocal {
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
}