package com.android.filmlibrary

import android.app.Application
import androidx.room.Room
import com.android.filmlibrary.model.Settings
import com.android.filmlibrary.model.data.*
import com.android.filmlibrary.model.room.DAO
import com.android.filmlibrary.model.room.DataBase
import java.lang.IllegalStateException

class GlobalVariables : Application() {
    var moviesByTrend: List<MoviesByTrend> = ArrayList()
    var moviesBySearch = MoviesList(
        listOf(),
        0,
        0
    )
    var moviesByGenre: MoviesByGenre = MoviesByGenre(Genre(), MoviesList(mutableListOf(), 0, 0))
    var moviesList: MoviesList = MoviesList(listOf(), 0, 0)
    var moviesByGenres: List<MoviesByGenre> = ArrayList()
    var genres: List<Genre> = ArrayList()

    var seachString: String = ""

    var settings: Settings = Settings(false)


    override fun onCreate() {
        super.onCreate()
        appInstance = this
    }

    companion object {
        private var appInstance: GlobalVariables? = null
        private var db: DataBase? = null
        private const val DB_NAME = "Movie.db"

        fun getDAO(): DAO {
            if (db == null) {
                synchronized(DataBase::class.java) {
                    if (db == null) {
                        if (appInstance == null) {
                            throw IllegalStateException("Application ids null meanwhile creating database")
                        }
                        db = Room.databaseBuilder(
                            appInstance!!.applicationContext,
                            DataBase::class.java,
                            DB_NAME
                        )
                            .allowMainThreadQueries()
                            .build()
                    }
                }
            }
            return db!!.historyDao()
        }
    }
}