package com.android.filmlibrary.viewmodel.genres

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.android.filmlibrary.Constant
import com.android.filmlibrary.Constant.CORRUPTED_DATA
import com.android.filmlibrary.Constant.REQUEST_ERROR
import com.android.filmlibrary.Constant.SERVER_ERROR
import com.android.filmlibrary.model.AppState
import com.android.filmlibrary.model.data.Genre
import com.android.filmlibrary.model.repository.Repository
import com.android.filmlibrary.model.repository.RepositoryImpl
import com.android.filmlibrary.model.retrofit.GenresAPI

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

    private val callBack = object :
        Callback<GenresAPI> {

        override fun onResponse(call: Call<GenresAPI>, response: Response<GenresAPI>) {
            Log.v("Debug1", "GenresViewModel onResponse")
            val serverResponse: GenresAPI? = response.body()
            liveDataToObserver.postValue(
                if (response.isSuccessful && serverResponse != null) {
                    checkResponse(serverResponse)
                } else {
                    AppState.Error(Throwable(SERVER_ERROR))
                }
            )
        }

        override fun onFailure(call: Call<GenresAPI>, t: Throwable) {
            Log.v("Debug1", "GenresViewModel onFailure")
            liveDataToObserver.postValue(AppState.Error(Throwable(t.message ?: REQUEST_ERROR)))
        }

        private fun checkResponse(serverResponse: GenresAPI): AppState {
            Log.v("Debug1", "GenresViewModel checkResponse")
            return if (serverResponse.results.isEmpty()) {
                AppState.Error(Throwable(CORRUPTED_DATA))
            } else {

                val genres = mutableListOf<Genre>()
                for (i in 0..serverResponse.results.size - 1) {
                    genres.add(
                        Genre(
                            serverResponse.results[i].id,
                            serverResponse.results[i].name
                        )
                    )
                }
                AppState.SuccessGenres(
                    genres
                )
            }
        }
    }

    fun getGenresFromRemoteSource() {
        liveDataToObserver.value = AppState.Loading
        repository.getGenresFromRemoteServerRetroFit(
            Constant.LANG_VALUE,
            callBack
        )
    }
}