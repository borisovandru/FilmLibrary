package com.android.filmlibrary.model.repository.local

interface RepositoryLocal {
    fun getFavoriteMovies(): List<Long>
    fun addFavoriteMovies()
    fun removeFavoriteMovies()

    fun getMovieNote(idMovie: Long): String
    fun updateMovieNote(idMovie: Long, note: String): Int
    fun insertMovieNote(idMovie: Long, note: String): Long
    fun removeMovieNote(idMovie: Long): Int

    fun getSearchHistory(): List<String>
    fun addSearchQuery(query: String)
}