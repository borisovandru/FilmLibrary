package com.android.filmlibrary.viewmodel.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.filmlibrary.model.AppState
import com.android.filmlibrary.model.data.LinkType
import com.android.filmlibrary.model.repository.RepositoryImpl

class ProfileViewModel(private val liveDataToObserver: MutableLiveData<AppState> = MutableLiveData()) :
    ViewModel() {

    private val repository = RepositoryImpl()
    fun getData(movieId: Int): LiveData<AppState> {
        Log.v("Debug1", "ProfileViewModel getData($movieId)")
        return liveDataToObserver
    }

    fun getDataFromRemoteSource() {
        Log.v("Debug1", "ProfileViewModel getDataFromRemoteSource")
        liveDataToObserver.value = AppState.Loading
        Thread {
            liveDataToObserver.postValue(
                (repository.getDataFromRemoteServer(LinkType.SETTINGS, ""))
            )
        }.start()
    }
}