package com.android.filmlibrary.model.repository

import com.android.filmlibrary.model.data.Category

sealed class AppState {
    data class Success(val listCategory: List<Category>) : AppState()
    data class Error(val error: Unit) : AppState()
    object Loading : AppState()
}