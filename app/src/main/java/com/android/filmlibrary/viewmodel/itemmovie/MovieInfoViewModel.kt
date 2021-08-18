package com.android.filmlibrary.viewmodel.itemmovie

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.android.filmlibrary.Constant.CORRUPTED_DATA
import com.android.filmlibrary.Constant.LANG_VALUE
import com.android.filmlibrary.Constant.REQUEST_ERROR
import com.android.filmlibrary.Constant.SERVER_ERROR
import com.android.filmlibrary.model.AppState
import com.android.filmlibrary.model.data.MovieAdv
import com.android.filmlibrary.model.repository.RepositoryImpl
import com.android.filmlibrary.model.retrofit.MovieAdvAPI
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MovieInfoViewModel(private val liveDataToObserver: MutableLiveData<AppState> = MutableLiveData()) :
    ViewModel() {

    private val repository = RepositoryImpl()
    private var movieId: Int = 1

    fun getData(movieId: Int): LiveData<AppState> {
        Log.v("Debug1", "MovieInfoViewModel getData($movieId)")
        this.movieId = movieId
        return liveDataToObserver
    }

    private val callBack = object :
        Callback<MovieAdvAPI> {

        override fun onResponse(call: Call<MovieAdvAPI>, response: Response<MovieAdvAPI>) {
            Log.v("Debug1", "MovieInfoViewModel onResponse")
            val serverResponse: MovieAdvAPI? = response.body()
            liveDataToObserver.postValue(
                if (response.isSuccessful && serverResponse != null) {
                    checkResponse(serverResponse)
                } else {
                    AppState.Error(Throwable(SERVER_ERROR))
                }
            )
        }

        override fun onFailure(call: Call<MovieAdvAPI>, t: Throwable) {
            Log.v("Debug1", "MovieInfoViewModel onFailure")
            liveDataToObserver.postValue(AppState.Error(Throwable(t.message ?: REQUEST_ERROR)))
        }

        private fun checkResponse(serverResponse: MovieAdvAPI): AppState {
            Log.v("Debug1", "MovieInfoViewModel checkResponse")
            return if (serverResponse.id == -1) {
                AppState.Error(Throwable(CORRUPTED_DATA))
            } else {

                var formattedDate = ""
                if (serverResponse.dateRelease != "") {
                    val localDate = LocalDate.parse(
                        serverResponse.dateRelease,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    )
                    val formatter = DateTimeFormatter.ofPattern("yyyy")
                    formattedDate = localDate.format(formatter)
                }

                Log.v(
                    "Debug1",
                    "MovieInfoViewModel checkResponse serverResponse.testVal=" + serverResponse.testVal
                )

                AppState.SuccessMovie(
                    MovieAdv(
                        serverResponse.id,
                        serverResponse.title,
                        formattedDate.toInt(),
                        serverResponse.genres,
                        serverResponse.countries,
                        serverResponse.dateRelease,
                        serverResponse.originalTitle,
                        serverResponse.overview,
                        serverResponse.posterUrl,
                        serverResponse.voteAverage,
                        serverResponse.runtime
                    )
                )
            }
        }
    }


    fun getMovieFromRemoteSource() {
        Log.v("Debug1", "MovieInfoViewModel getMovieFromRemoteSource")
        liveDataToObserver.value = AppState.Loading
        repository.getMovieFromRemoteServerRetroFit(movieId, LANG_VALUE, callBack)
    }
}