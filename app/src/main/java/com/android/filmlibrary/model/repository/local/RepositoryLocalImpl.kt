package com.android.filmlibrary.model.repository.local

import com.android.filmlibrary.model.room.DAO
import com.android.filmlibrary.model.room.EntityMovieNote
import com.android.filmlibrary.model.room.EntitySearchHistory

class RepositoryLocalImpl(private val localDataSource: DAO) : RepositoryLocal {

    override fun getFavoriteMovies(): List<Long> {
        TODO("Not yet implemented")
    }

    override fun addFavoriteMovies() {
        TODO("Not yet implemented")
    }

    override fun removeFavoriteMovies() {
        TODO("Not yet implemented")
    }

    //Note
    override fun getMovieNote(idMovie: Long): String {
        val result: String = localDataSource.getMovieNote(idMovie)
        return result
    }

    override fun updateMovieNote(idMovie: Long, note: String): Int {
        val result: Int = localDataSource.updateNote(idMovie, note)
        return result
    }

    override fun insertMovieNote(idMovie: Long, note: String): Long {
        val result: Long = localDataSource.insertNote(EntityMovieNote(0, idMovie, note))
        return result
    }

    override fun removeMovieNote(idMovie: Long): Int {
        return localDataSource.deleteNote(idMovie)
    }

    override fun getSearchHistory(): List<String> {
        val result: List<String> = localDataSource.getSearchHistory()
        return result
    }

    override fun addSearchQuery(query: String) {
        localDataSource.insertSearchQuery(EntitySearchHistory(0, query))
    }
}