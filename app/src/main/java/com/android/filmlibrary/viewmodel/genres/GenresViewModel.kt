package com.android.filmlibrary.viewmodel.genres

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.filmlibrary.Constant
import com.android.filmlibrary.model.AppState
import com.android.filmlibrary.model.data.Genre
import com.android.filmlibrary.model.repository.Repository
import com.android.filmlibrary.model.repository.RepositoryImpl

class GenresViewModel(private val repository: Repository = RepositoryImpl()) :
    ViewModel() {

    private val liveDataToObserver = MutableLiveData<AppState>()
    private val liveDataToObserver2 = MutableLiveData<AppState>()

    fun getData(): LiveData<AppState> {
        return liveDataToObserver
    }

    fun getData2(): LiveData<AppState> {
        return liveDataToObserver2
    }

    fun getGenresFromRemoteSource() {
        liveDataToObserver.value = AppState.Loading
        Thread {
            liveDataToObserver.postValue(
                (repository.getGenresFromRemoteServer())
            )
        }.start()
    }

    fun getMoviesByGenresFromRemoteSource(genres: List<Genre>) {
        liveDataToObserver2.value = AppState.Loading
        Thread {
            liveDataToObserver2.postValue(
                (repository.getMoviesByGenresFromRemoteServer(
                    genres,
                    Constant.COUNT_MOVIES_BY_GENRES
                ))
            )
        }.start()
    }
}