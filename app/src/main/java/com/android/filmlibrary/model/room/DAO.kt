package com.android.filmlibrary.model.room

import androidx.room.*

@Dao
interface DAO {

    //Note
    @Query("SELECT `note` FROM EntityMovieNote WHERE idMovie = :idMovie")
    fun getMovieNote(idMovie: Long): String

    @Query("UPDATE EntityMovieNote SET `note` = :note WHERE idMovie = :idMovie")
    fun updateNote(idMovie: Long, note: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNote(entity: EntityMovieNote): Long

    @Query("SELECT `note` FROM EntityMovieNote WHERE idMovie = :idMovie")
    fun getInserOrUpdateNote(idMovie: Long): Int

    @Query("DELETE FROM EntityMovieNote WHERE idMovie = :idMovie")
    fun deleteNote(idMovie: Long): Int

    //Fav
    @Query("SELECT * FROM EntityFavMovies")
    fun getFav(): List<EntityFavMovies>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertFavMovie(entity: EntityFavMovies)

    @Update
    fun updateFavMovie(entity: EntityFavMovies)

    @Delete
    fun deleteFavMovie(entity: EntityFavMovies)

    //SearchHistory
    @Query("SELECT `query` FROM EntitySearchHistory")
    fun getSearchHistory(): List<String>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertSearchQuery(entity: EntitySearchHistory)
}