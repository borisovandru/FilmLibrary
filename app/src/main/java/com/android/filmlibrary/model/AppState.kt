package com.android.filmlibrary.model

import com.android.filmlibrary.model.data.Movie
import com.android.filmlibrary.model.data.SettingsTMDB

sealed class AppState {
    data class SuccessMoviesByGenre(val moviesByGenre: MoviesByGenre) : AppState()
    data class SuccessMoviesByGenres(val moviesByGenres: List<MoviesByGenre>) :
        AppState()

    data class SuccessMovie(val movieData: Movie) : AppState()
    data class SuccessGenres(val genreData: List<Genre>) : AppState()

    data class SuccessSearch(val moviesBySearch: List<Movie>) : AppState()
    data class SuccessMoviesByTrend(val moviesByTrends: MoviesByTrend) :
        AppState()
    data class SuccessMoviesByTrends(val moviesByTrends: List<MoviesByTrend>) :
        AppState()

    data class SuccessSettings(val settingsTMDB: SettingsTMDB) : AppState()

    data class SuccessRawData(val rawData: Map<String, *>?) : AppState()

    class Error(val error: Throwable) : AppState()
    object Loading : AppState()
}