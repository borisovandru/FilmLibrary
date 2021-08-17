package com.android.filmlibrary.model.repository

import com.android.filmlibrary.model.AppState
import com.android.filmlibrary.model.data.*
import com.android.filmlibrary.model.retrofit.GenresAPI
import com.android.filmlibrary.model.retrofit.MoviesListAPI
import retrofit2.Callback

interface Repository {
    fun getMoviesByCategoryFromRemoteServer(genre: Genre, setCountsOfMovies: Int): AppState
    fun getMoviesByGenresFromRemoteServer(
        genres: List<Genre>,
        cntMovies: Int
    ): AppState

    fun getMovieFromRemoteServer(id: Int): AppState
    fun getGenresFromRemoteServer(): AppState

    fun getSettingsFromRemoteServer(): AppState

    fun getDataFromRemoteServer(linkType: LinkType, param1: String): AppState

    fun getMoviesByTrendFromRemoteServer(trend: Trend, setCountsOfMovies: Int): AppState
    fun getMoviesByTrendsFromRemoteServer(
        trends: List<Trend>,
        cntMovies: Int,
    ): AppState

    fun getMoviesFromLocalStorage(): List<MovieAdv>
    fun getMovieFromLocalStorage(id: Int): MovieAdv

    fun getMoviesBySearchFromRemoteServer(searchRequest: String, setCountsOfMovies: Int): AppState

    //RetroFit
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
}