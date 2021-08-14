package com.android.filmlibrary.viewmodel.search

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.filmlibrary.Constant.COUNT_MOVIES_BY_CATEGORY
import com.android.filmlibrary.model.AppState
import com.android.filmlibrary.model.repository.RepositoryImpl

class SearchViewModel(private val liveDataToObserver: MutableLiveData<AppState> = MutableLiveData()) :
    ViewModel() {

    private val repository = RepositoryImpl()
    private lateinit var searchRequest: String
    val bundleFromFragmentBToFragmentA = MutableLiveData<Bundle>()

    fun getData(serachRequest: String): LiveData<AppState> {
        this.searchRequest = serachRequest
        return liveDataToObserver
    }


    fun getSearchDataFromRemoteSource() {
        liveDataToObserver.value = AppState.Loading
        Thread {
            liveDataToObserver.postValue(
                (repository.getMoviesBySearchFromRemoteServer(
                    searchRequest,
                    COUNT_MOVIES_BY_CATEGORY
                ))
            )
        }.start()
    }
}