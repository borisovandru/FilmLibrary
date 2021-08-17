package com.android.filmlibrary.viewmodel.moviesbygenre

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.android.filmlibrary.Constant
import com.android.filmlibrary.model.AppState
import com.android.filmlibrary.model.data.Genre
import com.android.filmlibrary.model.data.Movie
import com.android.filmlibrary.model.data.MoviesByGenre
import com.android.filmlibrary.model.repository.RepositoryImpl
import com.android.filmlibrary.model.retrofit.MoviesListAPI
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class MoviesByGenreViewModel(private val liveDataToObserver: MutableLiveData<AppState> = MutableLiveData()) :
    ViewModel() {

    private val repository = RepositoryImpl()
    private lateinit var genre: Genre

    fun getData(genre: Genre): LiveData<AppState> {
        this.genre = genre
        return liveDataToObserver
    }

    private val callBack = object :
        Callback<MoviesListAPI> {

        override fun onResponse(call: Call<MoviesListAPI>, response: Response<MoviesListAPI>) {
            Log.v("Debug1", "MoviesByGenreViewModel onResponse")
            val serverResponse: MoviesListAPI? = response.body()
            liveDataToObserver.postValue(
                if (response.isSuccessful && serverResponse != null) {
                    checkResponse(serverResponse)
                } else {
                    AppState.Error(Throwable(Constant.SERVER_ERROR))
                }
            )
        }

        override fun onFailure(call: Call<MoviesListAPI>, t: Throwable) {
            Log.v("Debug1", "MoviesByGenreViewModel onFailure")
            liveDataToObserver.postValue(
                AppState.Error(
                    Throwable(
                        t.message ?: Constant.REQUEST_ERROR
                    )
                )
            )
        }

        private fun checkResponse(serverResponse: MoviesListAPI): AppState {
            Log.v("Debug1", "MoviesByGenreViewModel checkResponse")
            return if (serverResponse.results.isEmpty()) {
                AppState.Error(Throwable(Constant.CORRUPTED_DATA))
            } else {
                val moviesByGenre: MoviesByGenre
                val movies = mutableListOf<Movie>()
                for (i in 0..serverResponse.results.size - 1) {

                    var formattedDate = ""
                    if (serverResponse.results[i].dateRelease != "") {
                        val localDate = LocalDate.parse(
                            serverResponse.results[i].dateRelease,
                            DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        )
                        val formatter = DateTimeFormatter.ofPattern("yyyy")
                        formattedDate = localDate.format(formatter)
                    }
                    Log.v(
                        "Debug1",
                        "MoviesByGenreViewModel checkResponse i=" + i + ", id=" + serverResponse.results[i].id
                    )
                    movies.add(
                        Movie(
                            serverResponse.results[i].id,
                            serverResponse.results[i].title,
                            formattedDate.toInt(),
                            serverResponse.results[i].genre_ids,
                            serverResponse.results[i].dateRelease,
                            serverResponse.results[i].originalTitle,
                            serverResponse.results[i].overview,
                            serverResponse.results[i].posterUrl,
                            serverResponse.results[i].voteAverage
                        )
                    )
                }
                moviesByGenre = MoviesByGenre(
                    genre,
                    movies
                )
                AppState.SuccessMoviesByGenre(
                    moviesByGenre
                )
            }
        }
    }

    fun getDataFromRemoteSource() {
        liveDataToObserver.value = AppState.Loading
        repository.getMoviesByCategoryFromRemoteServerRetroFit(
            genre,
            Constant.LANG_VALUE,
            callBack
        )
    }
}