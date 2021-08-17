package com.android.filmlibrary.model.repository

import android.util.Log
import com.android.filmlibrary.BuildConfig
import com.android.filmlibrary.Constant.BASE_API_URL
import com.android.filmlibrary.Constant.BASE_URL
import com.android.filmlibrary.Constant.LANG
import com.android.filmlibrary.Constant.MOVIES_BY_CATEGORIES_1
import com.android.filmlibrary.Constant.MOVIES_BY_CATEGORIES_2
import com.android.filmlibrary.Constant.READ_TIMEOUT
import com.android.filmlibrary.Constant.URL_API
import com.android.filmlibrary.Constant.URL_CATEGORIES_1
import com.android.filmlibrary.Constant.URL_GENRES_1
import com.android.filmlibrary.Constant.URL_GENRES_2
import com.android.filmlibrary.Constant.URL_GENRES_3
import com.android.filmlibrary.Constant.URL_ITEM_MOVIE_1
import com.android.filmlibrary.Constant.URL_MOVIES_BY_GENRE_DIR_1
import com.android.filmlibrary.Constant.URL_MOVIES_BY_GENRE_DIR_2
import com.android.filmlibrary.Constant.URL_SEARCH_1
import com.android.filmlibrary.Constant.URL_SEARCH_2
import com.android.filmlibrary.Constant.URL_SETTINGS_1
import com.android.filmlibrary.Constant.URL_TREND
import com.android.filmlibrary.Constant.VERSION_API
import com.android.filmlibrary.model.AppState
import com.android.filmlibrary.model.data.*
import com.android.filmlibrary.model.repository.api.TheMovieDBAPI
import com.android.filmlibrary.model.retrofit.GenresAPI
import com.android.filmlibrary.model.retrofit.MovieAdvAPI
import com.android.filmlibrary.model.retrofit.MoviesListAPI
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.internal.LinkedTreeMap
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.util.*
import javax.net.ssl.HttpsURLConnection

class RepositoryImpl : Repository {

    private val movies = mutableListOf<MovieAdv>()

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
            LinkType.TREND -> {
                BASE_URL + URL_TREND + param1 + URL_API + BuildConfig.MOVIEDB_API_KEY + LANG
            }
            LinkType.SEARCH -> {
                BASE_URL + URL_SEARCH_1 + URL_API + BuildConfig.MOVIEDB_API_KEY + LANG + URL_SEARCH_2 + param1
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

    private fun parseItemMovieAdv(rawMovieDate: Map<*, *>): MovieAdv {

        val genresArr = mutableListOf<Genre>()
        val countriesArr = mutableListOf<Country>()

        val dateRelease = rawMovieDate["release_date"] as? String ?: "-"
        val title = rawMovieDate["title"] as? String ?: "-"
        val originalTitle = rawMovieDate["original_title"] as? String ?: "-"
        val id = (rawMovieDate["id"] as? Double ?: 0).toInt()
        val rated = rawMovieDate["vote_average"] as Double
        val runtime = (rawMovieDate["runtime"] as? Double ?: 0).toInt()

        val genres = rawMovieDate["genres"] as? ArrayList<Map<*, *>>
        if (genres != null) {
            for (i in 0 until genres.size) {
                val idGenres = (genres[i]["id"] as? Double ?: 0).toInt()
                val nameGenres = genres[i]["name"] as? String ?: ""
                genresArr.add(Genre(idGenres, nameGenres))
            }
        }

        val countries = rawMovieDate["production_countries"] as? ArrayList<Map<*, *>>
        if (countries != null) {
            for (i in 0 until countries.size) {
                val codeISO = countries[i]["iso_3166_1"] as? String ?: "-"
                val name = countries[i]["name"] as? String ?: "-"

                countriesArr.add(Country(codeISO, name))
            }
        }
        val overview = rawMovieDate["overview"] as? String ?: "-"
        val poster = rawMovieDate["poster_path"] as? String ?: "-"

        return MovieAdv(
            id,
            title,
            1990,
            genresArr,
            countriesArr,
            dateRelease,
            originalTitle,
            overview,
            poster,
            rated,
            runtime
        )
    }

    private fun parseItemMovie(rawMovieDate: Map<*, *>): Movie {

        val genresArr = mutableListOf<Genre>()

        val dateRelease = rawMovieDate["release_date"] as? String ?: "-"
        val title = rawMovieDate["title"] as? String ?: "-"
        val originalTitle = rawMovieDate["original_title"] as? String ?: "-"
        val id = (rawMovieDate["id"] as? Double ?: 0).toInt()
        val rated = rawMovieDate["vote_average"] as Double

        val genre_ids = rawMovieDate["genre_ids"] as List<Int>

        for (i in 0 until genre_ids.size) {
            val idGenres = (genre_ids[i].toDouble()).toInt()
            genresArr.add(Genre(idGenres, "genre №" + idGenres))
        }

        val overview = rawMovieDate["overview"] as? String ?: "-"
        val poster = rawMovieDate["poster_path"] as? String ?: "-"

        return Movie(
            id,
            title,
            1990,
            genre_ids,
            dateRelease,
            originalTitle,
            overview,
            poster,
            rated
        )
    }

    override fun getMoviesByCategoryFromRemoteServer(
        genre: Genre,
        setCountsOfMovies: Int,
    ): AppState {
        var result: AppState = AppState.Loading
        val moviesLoc = mutableListOf<Movie>()
        Log.v("Debug1", "RepositoryImpl getMoviesByCategoryFromRemoteServer")
        val data =
            getDataFromRemoteServer(LinkType.MOVIES_BY_CATEGORIES, genre.id.toString())

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
                            Log.v("Debug1",
                                "RepositoryImpl getMoviesByCategoryFromRemoteServer i=$i")
                            moviesLoc.add(
                                parseItemMovie(results[i])
                            )
                        }
                        result = AppState.SuccessMoviesByGenre(MoviesByGenre(genre,
                            moviesLoc))
                    }
                }
            }
            is AppState.Error -> {
                result = data
            }
        }
        return result
    }

    override fun getMoviesByCategoryFromRemoteServerRetroFit(
        genre: Genre,
        lang: String,
        callback: Callback<MoviesListAPI>,
    ) {
        Log.v("Debug1",
            "RepositoryImpl getMoviesByCategoryFromRemoteServerRetroFit($genre.id) begin")
        movieDTOApi.getMoviesByGenre(
            URL_MOVIES_BY_GENRE_DIR_1,
            URL_MOVIES_BY_GENRE_DIR_2,
            VERSION_API,
            BuildConfig.MOVIEDB_API_KEY,
            lang,
            genre.id
        )
            .enqueue(callback)
    }

    override fun getMoviesByGenresFromRemoteServer(
        genres: List<Genre>,
        cntMovies: Int,
    ): AppState {
        val moviesByCategories = mutableListOf<MoviesByGenre>()
        val result: AppState
        var appState: AppState
        for (category in genres) {
            appState = getMoviesByCategoryFromRemoteServer(category, cntMovies)
            when (appState) {
                is AppState.SuccessMoviesByGenre -> {
                    appState.moviesByGenre
                    moviesByCategories.add(appState.moviesByGenre)
                }
            }
        }
        result = AppState.SuccessMoviesByGenres(moviesByCategories)
        return result
    }

    override fun getMoviesFromLocalStorage(): List<MovieAdv> {
        return movies
    }

    override fun getMovieFromRemoteServer(id: Int): AppState {
        val movieAdv: MovieAdv
        var result: AppState = AppState.Loading
        Log.v("Debug1", "RepositoryImpl getMovieFromRemoteServer($id) begin")

        val data =
            getDataFromRemoteServer(LinkType.ITEM_MOVIE, id.toString())

        when (data) {
            is AppState.SuccessRawData -> {
                data.rawData?.let {
                    movieAdv = parseItemMovieAdv(it)
                    result = AppState.SuccessMovie(movieAdv)
                }
            }
            is AppState.Error -> {
                result = data
            }
        }
        return result
    }

    private val movieDTOApi = Retrofit.Builder()
        .baseUrl(BASE_API_URL)
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder().setLenient().create()
            )
        )
        .client(createOkHttpClient(MovieApiInterceptor()))
        .build().create(TheMovieDBAPI::class.java)

    override fun getMovieFromRemoteServerRetroFit(
        movieId: Int,
        lang: String,
        callback: Callback<MovieAdvAPI>,
    ) {
        Log.v("Debug1", "RepositoryImpl getMovieFromRemoteServer2($movieId) begin")

        movieDTOApi.getMovie(
            VERSION_API,
            movieId,
            BuildConfig.MOVIEDB_API_KEY,
            lang)
            .enqueue(callback)
    }

    private fun createOkHttpClient(interceptor: Interceptor): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(interceptor)
        httpClient.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
        return httpClient.build()
    }

    inner class MovieApiInterceptor : Interceptor {

        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            return chain.proceed(chain.request())
        }
    }

    override fun getMovieFromLocalStorage(id: Int): MovieAdv {
        return movies[id]
    }

    override fun getGenresFromRemoteServer(): AppState {
        val categoriesLoc = mutableListOf<Genre>()
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
                                Genre(
                                    id,
                                    name
                                )
                            )
                        }
                    }
                    result = AppState.SuccessGenres(categoriesLoc)
                }
            }
            is AppState.Error -> {
                result = data
            }
        }
        return result
    }

    private val genresDTOApi = Retrofit.Builder()
        .baseUrl(BASE_API_URL)
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder().setLenient().create()
            )
        )
        .client(createOkHttpClient(MovieApiInterceptor()))
        .build().create(TheMovieDBAPI::class.java)

    override fun getGenresFromRemoteServerRetroFit(
        lang: String,
        callback: Callback<GenresAPI>,
    ) {
        genresDTOApi.getGenres(
            URL_GENRES_1,
            URL_GENRES_2,
            URL_GENRES_3,
            VERSION_API,
            BuildConfig.MOVIEDB_API_KEY,
            lang
        )
            .enqueue(callback)
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

    override fun getMoviesByTrendFromRemoteServer(trend: Trend, setCountsOfMovies: Int): AppState {
        val moviesLoc = mutableListOf<Movie>()
        var result: AppState = AppState.Loading
        Log.v("Debug1",
            "RepositoryImpl getMoviesByTrendFromRemoteServer begin trend.URL=" + trend.URL)

        val data =
            getDataFromRemoteServer(LinkType.TREND, trend.URL)

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
                            moviesLoc.add(
                                parseItemMovie(results[i])
                            )
                        }
                    }
                }
                result = AppState.SuccessMoviesByTrend(MoviesByTrend(trend,
                    moviesLoc))
            }
            is AppState.Error -> {
                result = data
            }
        }
        return result
    }

    override fun getMoviesByTrendsFromRemoteServer(
        trends: List<Trend>,
        cntMovies: Int,
    ): AppState {
        val moviesByTrends = mutableListOf<MoviesByTrend>()
        val result: AppState
        var appState: AppState
        for (trend in trends) {
            appState = getMoviesByTrendFromRemoteServer(trend, cntMovies)
            when (appState) {
                is AppState.SuccessMoviesByTrend -> {
                    appState.moviesByTrends
                    moviesByTrends.add(appState.moviesByTrends)
                }
            }
        }
        result = AppState.SuccessMoviesByTrends(moviesByTrends)
        return result
    }

    override fun getMoviesBySearchFromRemoteServer(
        searchRequest: String,
        setCountsOfMovies: Int,
    ): AppState {
        var result: AppState = AppState.Loading
        val moviesLoc = mutableListOf<Movie>()
        Log.v("Debug1", "RepositoryImpl getMoviesBySearchFromRemoteServer")
        val data =
            getDataFromRemoteServer(LinkType.SEARCH, searchRequest)

        when (data) {
            is AppState.SuccessRawData -> {
                data.rawData?.let {
                    //val totalResults = (it["total_results"] as Double).toInt()TODO: Вывести
                    val results = it["results"] as ArrayList<Map<*, *>>
                    if (results.isNotEmpty()) {
                        var countsOfMovies = setCountsOfMovies
                        if (setCountsOfMovies > results.size) {
                            countsOfMovies = results.size
                        }
                        for (i in 0 until countsOfMovies) {
                            Log.v("Debug1",
                                "RepositoryImpl getMoviesBySearchFromRemoteServer i=$i")
                            moviesLoc.add(
                                parseItemMovie(results[i])
                            )
                        }
                        result = AppState.SuccessSearch(moviesLoc)
                    }
                }
            }
            is AppState.Error -> {
                result = data
            }
        }
        return result
    }
}