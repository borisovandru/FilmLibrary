package com.android.filmlibrary.model.repository.local

import com.android.filmlibrary.model.room.DAO
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

    override fun getMovieNote(idMovie: Long): String {
        TODO("Not yet implemented")
    }

    override fun addMovieNote(idMovie: Long) {
        TODO("Not yet implemented")
    }

    override fun removeMovieNote(idMovie: Long) {
        TODO("Not yet implemented")
    }

    override fun getSearchHistory(): List<String> {
        val result: List<String> = localDataSource.getSearchHistory()
        return result
    }

    override fun addSearchQuery(query: String) {
        localDataSource.insertSearchQuery(EntitySearchHistory(0, query))
    }
}