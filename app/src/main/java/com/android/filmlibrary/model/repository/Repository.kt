package com.android.filmlibrary.model.repository

import com.android.filmlibrary.model.AppState
import com.android.filmlibrary.model.data.*

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

    fun getMoviesFromLocalStorage(): List<Movie>
    fun getMovieFromLocalStorage(id: Int): Movie

    fun getMoviesBySearchFromRemoteServer(searchRequest: String, setCountsOfMovies: Int): AppState


}