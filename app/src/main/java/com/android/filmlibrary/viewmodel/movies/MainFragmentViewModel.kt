package com.android.filmlibrary.viewmodel.movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.filmlibrary.Constant.COUNT_MOVIES_BY_CATEGORIES
import com.android.filmlibrary.model.AppState
import com.android.filmlibrary.model.data.Category
import com.android.filmlibrary.model.repository.Repository
import com.android.filmlibrary.model.repository.RepositoryImpl

class MainFragmentViewModel(private val repository: Repository = RepositoryImpl()) :
    ViewModel() {

    private val liveDataToObserver = MutableLiveData<AppState>()
    private val liveDataToObserver2 = MutableLiveData<AppState>()

    fun getData(): LiveData<AppState> {
        return liveDataToObserver
    }

    fun getData2(): LiveData<AppState> {
        return liveDataToObserver2
    }

    fun getCategoriesFromRemoteSource() {
        liveDataToObserver.value = AppState.Loading
        Thread {
            liveDataToObserver.postValue(
                (repository.getCategoriesFromRemoteServer())
            )
        }.start()
    }

    fun getMoviesByCategoriesFromRemoteSource(categories: List<Category>) {
        liveDataToObserver2.value = AppState.Loading
        Thread {
            liveDataToObserver2.postValue(
                (repository.getMoviesByCategoriesFromRemoteServer(
                    categories,
                    COUNT_MOVIES_BY_CATEGORIES
                ))
            )
        }.start()

    }
}