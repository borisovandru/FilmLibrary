package com.android.filmlibrary.domain.remote

import com.android.filmlibrary.data.retrofit.PersonAPI
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.android.filmlibrary.BuildConfig
import com.android.filmlibrary.utils.Constant.BASE_API_URL
import com.android.filmlibrary.utils.Constant.URL_CONF_1
import com.android.filmlibrary.utils.Constant.URL_CREDITS_1
import com.android.filmlibrary.utils.Constant.URL_CREDITS_2
import com.android.filmlibrary.utils.Constant.URL_GENRES_1
import com.android.filmlibrary.utils.Constant.URL_GENRES_2
import com.android.filmlibrary.utils.Constant.URL_GENRES_3
import com.android.filmlibrary.utils.Constant.URL_MOVIES_BY_GENRE_DIR_1
import com.android.filmlibrary.utils.Constant.URL_MOVIES_BY_GENRE_DIR_2
import com.android.filmlibrary.utils.Constant.URL_PERSON_1
import com.android.filmlibrary.utils.Constant.URL_SEARCH_1
import com.android.filmlibrary.utils.Constant.URL_SEARCH_2
import com.android.filmlibrary.utils.Constant.URL_TRENDS_1
import com.android.filmlibrary.utils.Constant.VERSION_API
import com.android.filmlibrary.data.model.Genre
import com.android.filmlibrary.data.model.MovieAdv
import com.android.filmlibrary.data.model.Trend
import com.android.filmlibrary.data.TheMovieDBAPI
import com.android.filmlibrary.data.retrofit.*
import java.io.IOException

class RepositoryRemoteImpl : RepositoryRemote {

    private val movies = mutableListOf<MovieAdv>()

    override fun getMoviesFromLocalStorage(): List<MovieAdv> {
        return movies
    }

    override fun getMovieFromLocalStorage(id: Int): MovieAdv {
        return movies[id]
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

    private val retrofitBuilder = Retrofit.Builder()
        .baseUrl(BASE_API_URL)
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder().setLenient().create()
            )
        )
        .client(createOkHttpClient(MovieApiInterceptor()))
        .build().create(TheMovieDBAPI::class.java)

    private val genresDTOApi = Retrofit.Builder()
        .baseUrl(BASE_API_URL)
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder().setLenient().create()
            )
        )
        .client(createOkHttpClient(MovieApiInterceptor()))
        .build().create(TheMovieDBAPI::class.java)

    override fun getMoviesByCategoryFromRemoteServerRetrofit(
        genre: Genre,
        lang: String,
        callback: Callback<MoviesListAPI>,
    ) {
        retrofitBuilder.getMoviesByGenre(
            URL_MOVIES_BY_GENRE_DIR_1,
            URL_MOVIES_BY_GENRE_DIR_2,
            VERSION_API,
            BuildConfig.MOVIEDB_API_KEY,
            lang,
            genre.id
        )
            .enqueue(callback)
    }

    override fun getMovieFromRemoteServerRetrofit(
        movieId: Int,
        lang: String,
        callback: Callback<MovieAdvAPI>,
    ) {
        retrofitBuilder.getMovie(
            VERSION_API,
            movieId,
            BuildConfig.MOVIEDB_API_KEY,
            lang
        )
            .enqueue(callback)
    }

    override fun getGenresFromRemoteServerRetrofit(
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

    override fun getSettingsFromRemoteServerRetrofit(
        lang: String,
        callback: Callback<ConfigurationAPI>,
    ) {
        genresDTOApi.getConfiguration(
            URL_CONF_1,
            VERSION_API,
            BuildConfig.MOVIEDB_API_KEY,
        )
            .enqueue(callback)
    }

    override fun getMoviesByTrendFromRemoteServerRetrofit(
        trend: Trend,
        cntMovies: Int,
        lang: String,
        callback: Callback<MoviesListAPI>,
    ) {
        genresDTOApi.getTrends(
            URL_TRENDS_1,
            trend.URL,
            VERSION_API,
            BuildConfig.MOVIEDB_API_KEY,
            lang
        )
            .enqueue(callback)
    }

    override fun getMoviesBySearchFromRemoteServerRetrofit(
        searchRequest: String,
        setCountsOfMovies: Int,
        lang: String,
        adult: Boolean,
        callback: Callback<MoviesListAPI>,
    ) {
        genresDTOApi.getSearch(
            URL_SEARCH_1,
            URL_SEARCH_2,
            VERSION_API,
            BuildConfig.MOVIEDB_API_KEY,
            lang,
            searchRequest,
            adult
        )
            .enqueue(callback)
    }

    override fun getCreditsByMovieFromRemoteServerRetrofit(
        movieId: Int,
        lang: String,
        callback: Callback<CreditsAPI>,
    ) {
        retrofitBuilder.getCredits(
            URL_CREDITS_1,
            movieId,
            URL_CREDITS_2,
            VERSION_API,
            BuildConfig.MOVIEDB_API_KEY,
            lang
        )
            .enqueue(callback)
    }

    override fun getPersonFromRemoteServerRetrofit(
        personId: Int,
        lang: String,
        callback: Callback<PersonAPI>,
    ) {
        retrofitBuilder.getPerson(
            URL_PERSON_1,
            personId,
            VERSION_API,
            BuildConfig.MOVIEDB_API_KEY,
            lang
        )
            .enqueue(callback)
    }
}