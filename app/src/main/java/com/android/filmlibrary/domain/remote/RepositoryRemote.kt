package com.android.filmlibrary.domain.remote

import com.android.filmlibrary.data.retrofit.PersonAPI
import retrofit2.Callback
import com.android.filmlibrary.data.model.Genre
import com.android.filmlibrary.data.model.MovieAdv
import com.android.filmlibrary.data.model.Trend
import com.android.filmlibrary.data.retrofit.*

interface RepositoryRemote {
    fun getMoviesFromLocalStorage(): List<MovieAdv>
    fun getMovieFromLocalStorage(id: Int): MovieAdv

    fun getMovieFromRemoteServerRetrofit(
        movieId: Int,
        lang: String,
        callback: Callback<MovieAdvAPI>
    )

    fun getGenresFromRemoteServerRetrofit(lang: String, callback: Callback<GenresAPI>)
    fun getMoviesByCategoryFromRemoteServerRetrofit(
        genre: Genre,
        lang: String,
        callback: Callback<MoviesListAPI>
    )

    fun getMoviesBySearchFromRemoteServerRetrofit(
        searchRequest: String,
        setCountsOfMovies: Int,
        lang: String,
        adult: Boolean,
        callback: Callback<MoviesListAPI>
    )

    fun getMoviesByTrendFromRemoteServerRetrofit(
        trend: Trend,
        cntMovies: Int,
        lang: String,
        callback: Callback<MoviesListAPI>,
    )

    fun getSettingsFromRemoteServerRetrofit(
        lang: String,
        callback: Callback<ConfigurationAPI>,
    )

    fun getCreditsByMovieFromRemoteServerRetrofit(
        movieId: Int,
        lang: String,
        callback: Callback<CreditsAPI>,
    )

    fun getPersonFromRemoteServerRetrofit(
        personId: Int,
        lang: String,
        callback: Callback<PersonAPI>,
    )
}