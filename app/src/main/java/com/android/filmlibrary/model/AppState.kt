package com.android.filmlibrary.model

import com.android.filmlibrary.model.data.Category
import com.android.filmlibrary.model.data.Movie
import com.android.filmlibrary.model.data.MoviesByCategories

sealed class AppState {
    data class SuccessMoviesByCategory(val moviesByCategory: MoviesByCategories) : AppState()
    data class SuccessMoviesByCategories(val moviesByCategories: List<MoviesByCategories>) :
        AppState()

    data class SuccessMovie(val movieData: Movie) : AppState()
    data class SuccessCategories(val categoriesData: List<Category>) : AppState()

    data class SuccessRawData(val rawData: Map<String, *>?) : AppState()

    class Error(val error: Throwable) : AppState()
    object Loading : AppState()
}