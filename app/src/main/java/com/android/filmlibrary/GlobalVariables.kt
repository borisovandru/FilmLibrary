package com.android.filmlibrary

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
    var moviesByTrend: List<MoviesByTrend> = ArrayList()
    var favMovies: List<Movie> = ArrayList()
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

    var settings: Settings = Settings(false, false)


    override fun onCreate() {
        super.onCreate()
        appInstance = this
        context = applicationContext
    }

    companion object {
        lateinit var context: Context
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
                    DB_NAME)
                    .allowMainThreadQueries()
                    .build()
            }
            return db.historyDao()
        }
    }
}

interface IContextProvider{
    val context: Context
}

object ContextProvider: IContextProvider{
    override val context: Context
        get() = GlobalVariables.context
}