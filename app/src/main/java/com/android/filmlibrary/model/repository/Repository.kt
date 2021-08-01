package com.android.filmlibrary.model.repository

import com.android.filmlibrary.model.data.Category

interface Repository {
    fun getMoviesFromServer(): List<Category>
    fun getMoviesFromLocalStorage(): List<Category>
}