package com.android.filmlibrary

import android.app.Application
import com.android.filmlibrary.model.data.Genre
import com.android.filmlibrary.model.data.Movie
import com.android.filmlibrary.model.data.MoviesByGenre
import com.android.filmlibrary.model.data.MoviesByTrend

class GlobalVariables : Application() {
    var moviesByTrend: List<MoviesByTrend> = ArrayList()
    var moviesBySearch = listOf<Movie>()
    var moviesByGenre: MoviesByGenre = MoviesByGenre(Genre(), listOf())
    var moviesByGenres: List<MoviesByGenre> = ArrayList()
    var genres: List<Genre> = ArrayList()

    var positionTrend: Int = 0
    var positionSearch: Int = 0
    var positionGenres: Int = 0
    var positionMoviesByGenres: Int = 0
}