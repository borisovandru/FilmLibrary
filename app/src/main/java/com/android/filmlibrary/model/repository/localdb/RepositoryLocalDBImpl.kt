package com.android.filmlibrary.model.repository.localdb

import com.android.filmlibrary.model.data.Movie
import com.android.filmlibrary.model.room.DAO
import com.android.filmlibrary.model.room.EntityFavMovies
import com.android.filmlibrary.model.room.EntityMovieNote
import com.android.filmlibrary.model.room.EntitySearchHistory

class RepositoryLocalDBImpl(private val localDataSource: DAO) : RepositoryLocalDB {

    //Fav
    override fun getFavoriteMovies(): List<EntityFavMovies> {
        return localDataSource.getFav()
    }

    override fun getFavItem(idMovie: Long): Long {
        return localDataSource.getFavItem(idMovie)
    }

    override fun addFavoriteMovie(movie: Movie): Long {
        return localDataSource.insertFavMovie(
            EntityFavMovies(
                0,
                movie.id.toLong(),
                movie.title,
                movie.year,
                movie.dateRelease,
                movie.originalTitle,
                movie.overview,
                movie.posterUrl,
                movie.voteAverage,
            )
        )
    }

    override fun removeFavoriteMovies(idMovie: Long): Int {
        return localDataSource.deleteFavMovie(idMovie)
    }

    //Note
    override fun getMovieNote(idMovie: Long): String {
        return localDataSource.getMovieNote(idMovie)
    }

    override fun updateMovieNote(idMovie: Long, note: String): Int {
        return localDataSource.updateNote(idMovie, note)
    }

    override fun insertMovieNote(idMovie: Long, note: String): Long {
        return localDataSource.insertNote(EntityMovieNote(0, idMovie, note))
    }

    override fun removeMovieNote(idMovie: Long): Int {
        return localDataSource.deleteNote(idMovie)
    }

    //SearchHistory
    override fun getSearchHistory(): List<String> {
        return localDataSource.getSearchHistory()
    }

    override fun addSearchQuery(query: String) {
        localDataSource.insertSearchQuery(EntitySearchHistory(0, query))
    }
}