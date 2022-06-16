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

    fun getMovieFromRemoteServerRetroFit(
        movieId: Int,
        lang: String,
        callback: Callback<MovieAdvAPI>
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
        adult: Boolean,
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

    fun getCreditsByMovieFromRemoteServerRetroFit(
        movieId: Int,
        lang: String,
        callback: Callback<CreditsAPI>,
    )

    fun getPersonFromRemoteServerRetroFit(
        personId: Int,
        lang: String,
        callback: Callback<PersonAPI>,
    )
}