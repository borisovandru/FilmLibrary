package com.android.filmlibrary.model.repository

import retrofit2.Callback
import com.android.filmlibrary.model.data.Genre
import com.android.filmlibrary.model.data.MovieAdv
import com.android.filmlibrary.model.data.Trend
import com.android.filmlibrary.model.retrofit.ConfigurationAPI
import com.android.filmlibrary.model.retrofit.GenresAPI
import com.android.filmlibrary.model.retrofit.MoviesListAPI

interface Repository {
    fun getMoviesFromLocalStorage(): List<MovieAdv>
    fun getMovieFromLocalStorage(id: Int): MovieAdv

    fun getMovieFromRemoteServerRetroFit(
        movieId: Int,
        lang: String,
        callback: Callback<com.android.filmlibrary.model.retrofit.MovieAdvAPI>
    )

    fun getGenresFromRemoteServerRetroFit(lang: String, callback: Callback<GenresAPI>)
    fun getMoviesByCategoryFromRemoteServerRetroFit(
        genre: Genre,
        lang: String,
        callback: Callback<MoviesListAPI>
    )

    fun getMoviesBySearchFromRemoteServerRetroFit(
        searchRequest: String,
        setCountsOfMovies: Int,
        lang: String,
        callback: Callback<MoviesListAPI>
    )

    fun getMoviesByTrendFromRemoteServerRetroFit(
        trend: Trend,
        cntMovies: Int,
        lang: String,
        callback: Callback<MoviesListAPI>,
    )

    fun getSettingsFromRemoteServerRetroFit(
        lang: String,
        callback: Callback<ConfigurationAPI>,
    )
}