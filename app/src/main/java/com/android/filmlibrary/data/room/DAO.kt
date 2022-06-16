package com.android.filmlibrary.data.room

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
    fun getInsertOrUpdateNote(idMovie: Long): Int

    @Query("DELETE FROM EntityMovieNote WHERE idMovie = :idMovie")
    fun deleteNote(idMovie: Long): Int

    //Fav
    @Query("SELECT * FROM EntityFavMovies")
    fun getFav(): List<EntityFavMovies>

    @Query("SELECT `id` FROM EntityFavMovies where idMovie = :idMovie")
    fun getFavItem(idMovie: Long): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertFavMovie(entity: EntityFavMovies): Long

    @Update
    fun updateFavMovie(entity: EntityFavMovies)

    @Query("DELETE FROM EntityFavMovies WHERE idMovie = :idMovie")
    fun deleteFavMovie(idMovie: Long): Int

    //SearchHistory
    @Query("SELECT `query` FROM EntitySearchHistory")
    fun getSearchHistory(): List<String>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertSearchQuery(entity: EntitySearchHistory)

    //Message
    @Query("SELECT * FROM EntityMessage")
    fun getMessages(): List<EntityMessage>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addMessage(entity: EntityMessage): Long
}