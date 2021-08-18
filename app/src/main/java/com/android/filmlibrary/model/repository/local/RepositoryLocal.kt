package com.android.filmlibrary.model.repository.local

interface RepositoryLocal {
    fun getFavoriteMovies(): List<Long>
    fun addFavoriteMovies()
    fun removeFavoriteMovies()

    fun getMovieNote(idMovie: Long): String
    fun addMovieNote(idMovie: Long)
    fun removeMovieNote(idMovie: Long)

    fun getSearchHistory(): List<String>
    fun addSearchQuery(query: String)
}