package com.android.filmlibrary.model.repository

import com.android.filmlibrary.model.AppState
import com.android.filmlibrary.model.data.Category
import com.android.filmlibrary.model.data.LinkType
import com.android.filmlibrary.model.data.Movie

interface Repository {
    fun getMoviesByCategoryFromRemoteServer(category: Category, cntMovies: Int): AppState
    fun getMoviesByCategoriesFromRemoteServer(
        categories: List<Category>,
        cntMovies: Int
    ): AppState
    fun getMovieFromRemoteServer(id: Int): AppState
    fun getCategoriesFromRemoteServer(): AppState

    fun getSettingsFromRemoteServer(): AppState

    fun getDataFromRemoteServer(linkType: LinkType, param1: String): AppState


    fun getMoviesFromLocalStorage(): List<Movie>
    fun getMovieFromLocalStorage(id: Int): Movie



}