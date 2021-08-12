package com.android.filmlibrary.viewmodel.moviesbycategory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.filmlibrary.Constant.COUNT_MOVIES_BY_CATEGORY
import com.android.filmlibrary.model.AppState
import com.android.filmlibrary.model.data.Category
import com.android.filmlibrary.model.repository.RepositoryImpl

class MoviesByCategoryViewModel(private val liveDataToObserver: MutableLiveData<AppState> = MutableLiveData()) :
    ViewModel() {

    private val repository = RepositoryImpl()
    private lateinit var category: Category

    fun getData(category: Category): LiveData<AppState> {
        this.category = category
        return liveDataToObserver
    }


    fun getDataFromRemoteSource() {
        liveDataToObserver.value = AppState.Loading
        Thread {
            liveDataToObserver.postValue(
                (repository.getMoviesByCategoryFromRemoteServer(
                    category,
                    COUNT_MOVIES_BY_CATEGORY
                ))
            )
        }.start()
    }
}