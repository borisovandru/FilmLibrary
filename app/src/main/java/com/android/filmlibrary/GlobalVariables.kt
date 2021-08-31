package com.android.filmlibrary

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.room.Room
import com.android.filmlibrary.Constant.NAME_DB
import com.android.filmlibrary.model.Settings
import com.android.filmlibrary.model.data.*
import com.android.filmlibrary.model.room.DAO
import com.android.filmlibrary.model.room.DataBase
import java.lang.IllegalStateException

class GlobalVariables : Application() {

    override fun onCreate() {
        super.onCreate()
        appInstance = this
        context = applicationContext
    }

    companion object {
        var settings: Settings = Settings(adult = false, withPhone = false, geoFence = false)
        var searchStringCache: String = ""
        var contactsCache: List<Contact> = ArrayList()
        var genresCache = mutableListOf<Genre>()
        var moviesByGenresCache = mutableListOf<MoviesByGenre>()
        var moviesListCache: MoviesList = MoviesList(listOf(), 0, 0)
        var moviesByGenreCache: MoviesByGenre =
            MoviesByGenre(Genre(), MoviesList(mutableListOf(), 0, 0))
        var moviesBySearchCache = MoviesList(listOf(), 0, 0)
        var favMoviesCache: List<Movie> = ArrayList()
        var moviesByTrendsCache: List<MoviesByTrend> = ArrayList()

        lateinit var context: Context
        lateinit var activity: Activity

        private var appInstance: GlobalVariables? = null

        private lateinit var db: DataBase
        private const val DB_NAME = NAME_DB

        fun getDAO(): DAO {
            synchronized(DataBase::class.java) {
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
            return db.historyDao()
        }
    }
}

interface IContextProvider {
    val context: Context
    val activity: Activity
}

object ContextProvider : IContextProvider {
    override val context: Context
        get() = GlobalVariables.context

    override val activity: Activity
        get() = GlobalVariables.activity
}