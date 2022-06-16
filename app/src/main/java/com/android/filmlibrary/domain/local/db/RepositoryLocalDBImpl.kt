package com.android.filmlibrary.domain.local.db

import com.android.filmlibrary.data.model.Movie
import com.android.filmlibrary.data.room.*

class RepositoryLocalDBImpl(private val localDataSource: DAO) : RepositoryLocalDB {
    //Fav
    override fun getFavoriteMovies(): List<EntityFavMovies> = localDataSource.getFav()

    override fun getFavItem(idMovie: Long) = localDataSource.getFavItem(idMovie)

    override fun addFavoriteMovie(movie: Movie) = localDataSource.insertFavMovie(
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

    override fun removeFavoriteMovies(idMovie: Long) = localDataSource.deleteFavMovie(idMovie)

    //Note
    override fun getMovieNote(idMovie: Long) = localDataSource.getMovieNote(idMovie)

    override fun updateMovieNote(idMovie: Long, note: String) =
        localDataSource.updateNote(idMovie, note)

    override fun insertMovieNote(idMovie: Long, note: String) =
        localDataSource.insertNote(EntityMovieNote(0, idMovie, note))

    override fun removeMovieNote(idMovie: Long) = localDataSource.deleteNote(idMovie)

    //SearchHistory
    override fun getSearchHistory() = localDataSource.getSearchHistory()

    override fun addSearchQuery(query: String) {
        localDataSource.insertSearchQuery(EntitySearchHistory(0, query))
    }

    //Message
    override fun getMessages(): List<EntityMessage> = localDataSource.getMessages()

    override fun addMessage(header: String, body: String) =
        localDataSource.addMessage(EntityMessage(0, header, body))
}