package com.android.filmlibrary.presentation.moviesbygenre

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.android.filmlibrary.Constant
import com.android.filmlibrary.Constant.FORMATED_STRING_DATE_TMDB
import com.android.filmlibrary.Constant.FORMATED_STRING_YEAR
import com.android.filmlibrary.AppState
import com.android.filmlibrary.data.model.Genre
import com.android.filmlibrary.data.model.Movie
import com.android.filmlibrary.data.model.MoviesByGenre
import com.android.filmlibrary.data.model.MoviesList
import com.android.filmlibrary.domain.remote.RepositoryRemoteImpl
import com.android.filmlibrary.data.retrofit.MoviesListAPI
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MoviesByGenreViewModel(private val liveDataToObserver: MutableLiveData<AppState> = MutableLiveData()) :
    ViewModel() {

    private val repository = RepositoryRemoteImpl()
    private lateinit var genre: Genre

    fun getData(genre: Genre): LiveData<AppState> {
        this.genre = genre
        return liveDataToObserver
    }

    private val callBack = object :
        Callback<MoviesListAPI> {

        override fun onResponse(call: Call<MoviesListAPI>, response: Response<MoviesListAPI>) {
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
            liveDataToObserver.postValue(
                AppState.Error(
                    Throwable(
                        t.message
                            ?: Constant.REQUEST_ERROR
                    )
                )
            )
        }

        private fun checkResponse(serverResponse: MoviesListAPI): AppState {
            return if (serverResponse.results.isEmpty()) {
                AppState.Error(Throwable(Constant.CORRUPTED_DATA))
            } else {

                val moviesByGenre: MoviesByGenre
                val movies = mutableListOf<Movie>()
                serverResponse.results.indices.forEach { i ->
                    var formattedDate = ""
                    serverResponse.results[i].dateRelease?.let {
                        if (serverResponse.results[i].dateRelease != "") {
                            val localDate = LocalDate.parse(
                                serverResponse.results[i].dateRelease,
                                DateTimeFormatter.ofPattern(FORMATED_STRING_DATE_TMDB)
                            )
                            val formatter = DateTimeFormatter.ofPattern(FORMATED_STRING_YEAR)
                            formattedDate = localDate.format(formatter)
                        }
                    }

                    movies.add(
                        Movie(
                            serverResponse.results[i].id,

                            serverResponse.results[i].title,
                            formattedDate,
                            serverResponse.results[i].genre_ids,
                            serverResponse.results[i].dateRelease,
                            serverResponse.results[i].originalTitle,
                            serverResponse.results[i].overview,
                            serverResponse.results[i].posterUrl,
                            serverResponse.results[i].voteAverage
                        )
                    )
                }

                val moviesList = MoviesList(
                    movies,
                    serverResponse.totalPages,
                    serverResponse.totalResults
                )

                moviesByGenre = MoviesByGenre(
                    genre,
                    moviesList
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