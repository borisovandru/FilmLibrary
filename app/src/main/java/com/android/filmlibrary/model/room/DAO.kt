package com.android.filmlibrary.model.room

import androidx.room.*

@Dao
interface DAO {
    @Query("SELECT * FROM EntityFavMovies")
    fun getFav(): List<EntityFavMovies>

    @Query("SELECT * FROM EntityMovieNote WHERE idMovie = :idMovie")
    fun getMovieNote(idMovie: Long): List<EntityMovieNote>

    @Query("SELECT `query` FROM EntitySearchHistory")
    fun getSearchHistory(): List<String>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertNote(entity: EntityMovieNote)

    @Update
    fun updateNote(entity: EntityMovieNote)

    @Delete
    fun deleteNote(entity: EntityMovieNote)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertFavMovie(entity: EntityFavMovies)

    @Update
    fun updateFavMovie(entity: EntityFavMovies)

    @Delete
    fun deleteFavMovie(entity: EntityFavMovies)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertSearchQuery(entity: EntitySearchHistory)
}