package com.android.filmlibrary.model.repository

import android.annotation.SuppressLint
import com.android.filmlibrary.model.data.Category
import com.android.filmlibrary.model.data.Movie

@SuppressLint("ResourceType")
class RepositoryImpl : Repository {

    private var movies: List<Movie> = listOf<Movie>(
        Movie("Космический джем: Новое поколение", 0, 78, "08 июль 2021"),
        Movie("Чёрная вдова", 1, 79, "07 июл 2021"),
        Movie("Судная ночь навсегда", 2, 78, "30 июн 2021"),
        Movie("Босс-молокосос 2", 3, 79, "01 июл 2021"),
        Movie("Война будущего", 4, 82, "30 июн 2021")
    )
    private var categories: List<Category> = listOf(
        Category("Боевики", movies),
        Category("Комедии", movies),
        Category("Фантастика", movies),
        Category("Ужасы", movies),
        Category("Мультфильмы", movies)
    )

    override fun getMoviesFromLocalStorage(): List<Category> = categories
    override fun getMoviesFromServer(): List<Category> = categories

}