package com.android.filmlibrary.model.repository

import android.util.Log
import com.google.gson.Gson
import com.android.filmlibrary.BuildConfig
import com.android.filmlibrary.Constant.BASE_URL
import com.android.filmlibrary.Constant.COUNT_CATEGORY
import com.android.filmlibrary.Constant.COUNT_MOVIES
import com.android.filmlibrary.Constant.LANG
import com.android.filmlibrary.Constant.MOVIES_BY_CATEGORIES_1
import com.android.filmlibrary.Constant.MOVIES_BY_CATEGORIES_2
import com.android.filmlibrary.Constant.READ_TIMEOUT
import com.android.filmlibrary.Constant.URL_API
import com.android.filmlibrary.Constant.URL_CATEGORIES_1
import com.android.filmlibrary.Constant.URL_ITEM_MOVIE_1
import com.android.filmlibrary.Constant.URL_SETTINGS_1
import com.android.filmlibrary.model.AppState
import com.android.filmlibrary.model.data.*
import com.google.gson.internal.LinkedTreeMap
import java.io.InputStreamReader
import java.net.URL
import java.util.*
import javax.net.ssl.HttpsURLConnection


class RepositoryImpl : Repository {

    private val movies = mutableListOf<Movie>()
    private val categories = mutableListOf<Category>()
    private val cntCategories = COUNT_CATEGORY
    private val cntMovies = COUNT_MOVIES

    private fun IntRange.random() =
        Random().nextInt((endInclusive + 1) - start) + start

    init {

        for (i in 0 until cntCategories) {
            categories.add(
                Category(
                    i,
                    "Категория №" + (i + 1)
                )
            )
        }

        for (i in 0 until cntMovies) {
            movies.add(
                Movie(
                    i,
                    "Фильм №$i",
                    (1990 + i),
                    categories[(0 until cntCategories).random()],
                    "1990-1-1",
                    "descr",
                    ""
                )
            )
        }
    }

    override fun getDataFromRemoteServer(linkType: LinkType, param1: String): AppState {
        val rawData: Map<String, *>?
        var result: AppState

        val url: String = when (linkType) {
            LinkType.ITEM_MOVIE -> {
                BASE_URL + URL_ITEM_MOVIE_1 + param1 + URL_API + BuildConfig.MOVIEDB_API_KEY + LANG
            }
            LinkType.CATEGORIES -> {
                BASE_URL + URL_CATEGORIES_1 + URL_API + BuildConfig.MOVIEDB_API_KEY + LANG
            }
            LinkType.MOVIES_BY_CATEGORIES -> {
                BASE_URL + MOVIES_BY_CATEGORIES_1 + URL_API + BuildConfig.MOVIEDB_API_KEY + LANG + MOVIES_BY_CATEGORIES_2 + param1
            }
            LinkType.SETTINGS -> {
                BASE_URL + URL_SETTINGS_1 + URL_API + BuildConfig.MOVIEDB_API_KEY + LANG
            }
        }

        var urlConnection: HttpsURLConnection? = null
        val uri =
            URL(url)
        try {
            urlConnection = (uri.openConnection() as HttpsURLConnection)
                .apply {
                    requestMethod = "GET"
                    addRequestProperty("Content-Type", "application/json")
                    readTimeout = READ_TIMEOUT
                }
            Log.v("Debug1", "RepositoryImpl getDataFromRemoteServer uri=$uri")
            rawData = InputStreamReader(urlConnection.inputStream)
                .let { reader ->
                    Gson().fromJson<Map<String, *>>(reader, Map::class.java)
                }
            result = AppState.SuccessRawData(rawData)
        } catch (e: Exception) {
            Log.v("Debug1", "RepositoryImpl getDataFromRemoteServer Exception: $e")
            urlConnection?.disconnect()
            result = AppState.Error(e)
        } finally {
            urlConnection?.disconnect()
        }
        return result
    }


    override fun getMoviesByCategoryFromRemoteServer(
        category: Category,
        setCountsOfMovies: Int,
    ): AppState {
        var result: AppState = AppState.Loading
        val moviesLoc = mutableListOf<Movie>()
        Log.v("Debug1", "RepositoryImpl getMoviesByCategoryFromRemoteServer")
        val data =
            getDataFromRemoteServer(LinkType.MOVIES_BY_CATEGORIES, category.id.toString())

        when (data) {
            is AppState.SuccessRawData -> {
                data.rawData?.let {
                    val results = it["results"] as ArrayList<Map<*, *>>
                    if (results.isNotEmpty()) {
                        var countsOfMovies = setCountsOfMovies
                        if (setCountsOfMovies > results.size) {
                            countsOfMovies = results.size
                        }
                        for (i in 0 until countsOfMovies) {
                            Log.v(
                                "Debug1",
                                "RepositoryImpl getMoviesByCategoryFromRemoteServer i=$i"
                            )
                            val dateRelease = results[i]["release_date"] as? String ?: "-"
                            val title = results[i]["original_title"] as? String ?: "-"
                            val id = (results[i]["id"] as? Double ?: 0).toInt()
                            val descr = results[i]["overview"] as? String ?: "-"
                            val poster = results[i]["poster_path"] as? String ?: "-"
                            moviesLoc.add(
                                Movie(
                                    id,
                                    title,
                                    1990,
                                    category,
                                    dateRelease,
                                    descr,
                                    poster
                                )
                            )
                        }
                        result = AppState.SuccessMoviesByCategory(
                            MoviesByCategories(
                                category,
                                moviesLoc
                            )
                        )
                    }
                }

            }
            is AppState.Error -> {
                result = data
            }
        }
        return result
    }

    override fun getMoviesByCategoriesFromRemoteServer(
        categories: List<Category>,
        cntMovies: Int,
    ): AppState {
        val moviesByCategories = mutableListOf<MoviesByCategories>()
        val result: AppState
        var appState: AppState
        for (category in categories) {
            appState = getMoviesByCategoryFromRemoteServer(category, cntMovies)
            when (appState) {
                is AppState.SuccessMoviesByCategory -> {
                    appState.moviesByCategory
                    moviesByCategories.add(appState.moviesByCategory)
                }
            }
        }
        result = AppState.SuccessMoviesByCategories(moviesByCategories)
        return result
    }

    override fun getMoviesFromLocalStorage(): List<Movie> {
        return movies
    }


    override fun getMovieFromRemoteServer(id: Int): AppState {
        val movie: Movie
        var result: AppState = AppState.Loading
        Log.v("Debug1", "RepositoryImpl getMovieFromRemoteServer($id) begin")

        val data =
            getDataFromRemoteServer(LinkType.ITEM_MOVIE, id.toString())


        when (data) {
            is AppState.SuccessRawData -> {
                data.rawData?.let {
                    val title = it["original_title"] as String
                    val dateRelease = it["release_date"] as? String ?: "-"
                    val description = it["overview"] as String
                    val poster = it["poster_path"] as String

                    Log.v(
                        "Debug1",
                        "RepositoryImpl getMovieFromRemoteServer($id) title=$title"
                    )
                    movie = Movie(
                        id,
                        title,
                        1900,
                        Category(555, "terrt"),
                        dateRelease,
                        description,
                        poster
                    )
                    result = AppState.SuccessMovie(movie)
                }

            }
            is AppState.Error -> {
                result = data
            }
        }
        return result
    }


    override fun getMovieFromLocalStorage(id: Int): Movie {
        return movies[id]
    }

    override fun getCategoriesFromRemoteServer(): AppState {
        val categoriesLoc = mutableListOf<Category>()
        Log.v("Debug1", "RepositoryImpl getCategoriesFromRemoteServer")
        var result: AppState = AppState.Loading
        val data =
            getDataFromRemoteServer(LinkType.CATEGORIES, "")

        when (data) {
            is AppState.SuccessRawData -> {
                data.rawData?.let {
                    val genres = it["genres"] as ArrayList<Map<*, *>>
                    if (genres.isNotEmpty()) {
                        for (i in 0 until genres.size) {
                            Log.v("Debug1", "RepositoryImpl getCategoriesFromRemoteServer i=$i")

                            val id = (genres[i]["id"] as? Double ?: 0).toInt()
                            val name = genres[i]["name"] as? String ?: ""

                            categoriesLoc.add(
                                Category(
                                    id,
                                    name
                                )
                            )
                        }
                    }
                    result = AppState.SuccessCategories(categoriesLoc)
                }

            }
            is AppState.Error -> {
                result = data
            }
        }
        return result
    }


    override fun getSettingsFromRemoteServer(): AppState {
        val settingsTMDB: SettingsTMDB
        var result: AppState = AppState.Loading
        Log.v("Debug1", "RepositoryImpl getSettingsFromRemoteServer begin")

        val data =
            getDataFromRemoteServer(LinkType.SETTINGS, "")

        when (data) {
            is AppState.SuccessRawData -> {

                data.rawData?.let {
                    val images = it["images"] as LinkedTreeMap<*, *>
                    val baseUrl = images["base_url"] as String
                    val secureBaseUrl = images["secure_base_url"] as String
                    settingsTMDB = SettingsTMDB(baseUrl, secureBaseUrl)
                    result = AppState.SuccessSettings(settingsTMDB)
                }

            }
            is AppState.Error -> {
                result = data
            }
        }
        return result
    }

}
