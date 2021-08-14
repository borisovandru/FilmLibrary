package com.android.filmlibrary.viewmodel.thrends

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.filmlibrary.Constant.COUNT_MOVIES_BY_TREND
import com.android.filmlibrary.model.AppState
import com.android.filmlibrary.model.data.Trend
import com.android.filmlibrary.model.repository.Repository
import com.android.filmlibrary.model.repository.RepositoryImpl

class ThrendsFragmentViewModel(private val repository: Repository = RepositoryImpl()) :
    ViewModel() {

    private val liveDataToObserver = MutableLiveData<AppState>()

    fun getData(): LiveData<AppState> {
        return liveDataToObserver
    }

    fun getTrendsFromRemoteSource(trends: List<Trend>) {
        liveDataToObserver.value = AppState.Loading
        Thread {
            liveDataToObserver.postValue(
                (repository.getMoviesByTrendsFromRemoteServer(
                    trends,
                    COUNT_MOVIES_BY_TREND
                ))
            )
        }.start()
    }

}