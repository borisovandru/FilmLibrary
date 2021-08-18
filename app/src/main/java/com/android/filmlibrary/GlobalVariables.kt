package com.android.filmlibrary

import android.app.Application
import com.android.filmlibrary.model.data.Genre
import com.android.filmlibrary.model.data.MoviesByGenre
import com.android.filmlibrary.model.data.MoviesByTrend
import com.android.filmlibrary.model.data.MoviesList

class GlobalVariables: Application() {
    var moviesByTrend: List<MoviesByTrend> = ArrayList()
    var moviesBySearch = MoviesList(
        listOf(),
        0,
        0
    )
    var moviesByGenre: MoviesByGenre = MoviesByGenre(Genre(), MoviesList(mutableListOf(), 0, 0))
    var moviesList: MoviesList = MoviesList(listOf(), 0, 0)
    var moviesByGenres: List<MoviesByGenre> = ArrayList()
    var genres: List<Genre> = ArrayList()

    var seachString: String = ""
}