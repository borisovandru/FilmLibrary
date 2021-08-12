package com.android.filmlibrary.viewmodel.itemmovie

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.filmlibrary.model.AppState
import com.android.filmlibrary.model.repository.RepositoryImpl

class MovieInfoViewModel(private val liveDataToObserver: MutableLiveData<AppState> = MutableLiveData()) :
    ViewModel() {

    private val repository = RepositoryImpl()
    private var movieId: Int = 1

    fun getData(movieId: Int): LiveData<AppState> {
        Log.v("Debug1", "MovieInfoViewModel getData($movieId)")
        this.movieId = movieId
        return liveDataToObserver
    }


    fun getMovieFromRemoteSource() {
        Log.v("Debug1", "MovieInfoViewModel getMovieFromRemoteSource")
        liveDataToObserver.value = AppState.Loading
        Thread {
            liveDataToObserver.postValue(
                (repository.getMovieFromRemoteServer(movieId))
            )
        }.start()
    }
}