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
import com.android.filmlibrary.model.data.Movie
import com.android.filmlibrary.model.data.MoviesByGenre
import com.android.filmlibrary.model.data.MoviesList
import com.android.filmlibrary.model.repository.Repository
import com.android.filmlibrary.model.repository.RepositoryImpl
import com.android.filmlibrary.model.retrofit.GenresAPI
import com.android.filmlibrary.model.retrofit.MoviesListAPI
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class GenresViewModel(private val repository: Repository = RepositoryImpl()) :
    ViewModel() {

    private val liveDataToObserver = MutableLiveData<AppState>()
    private val liveDataToObserver2 = MutableLiveData<AppState>()

    val genres = mutableListOf<Genre>()

    private var countSuccess: Int = 0

    fun getData(): LiveData<AppState> {
        return liveDataToObserver
    }

    fun getData2(): LiveData<AppState> {
        return liveDataToObserver2
    }

    private val callBackMoviesList = object :
        Callback<MoviesListAPI> {

        override fun onResponse(call: Call<MoviesListAPI>, response: Response<MoviesListAPI>) {
            Log.v("Debug1", "GenresViewModel callBackMoviesList onResponse")

            val genreId: String = (response.raw().networkResponse()
                ?.request()
                ?.url()?.queryParameter("with_genres") ?: "")

            response.raw().networkResponse()

            val genre = genres.first { it.id == genreId.toInt() }

            val serverResponse: MoviesListAPI? = response.body()

            successItemGenre(
                if (response.isSuccessful && serverResponse != null) {
                    checkResponse(serverResponse, genre)
                } else {
                    AppState.Error(Throwable(SERVER_ERROR))
                }
            )
        }

        override fun onFailure(call: Call<MoviesListAPI>, t: Throwable) {
            Log.v("Debug1", "GenresViewModel callBackMoviesList onFailure")
            successItemGenre(AppState.Error(Throwable(t.message ?: REQUEST_ERROR)))
        }

        private fun checkResponse(serverResponse: MoviesListAPI, genre: Genre): AppState {
            Log.v("Debug1", "GenresViewModel callBackMoviesList checkResponse")
            return if (serverResponse.results.isEmpty()) {
                AppState.Error(Throwable(CORRUPTED_DATA))
            } else {
                val movies = mutableListOf<Movie>()
                for (i in serverResponse.results.indices) {

                    var formattedDate = ""
                    serverResponse.results[i].dateRelease?.let {
                        formattedDate = ""
                        if (serverResponse.results[i].dateRelease != "") {
                            val localDate = LocalDate.parse(
                                serverResponse.results[i].dateRelease,
                                DateTimeFormatter.ofPattern("yyyy-MM-dd")
                            )
                            val formatter = DateTimeFormatter.ofPattern("yyyy")
                            formattedDate = localDate.format(formatter)
                        }
                    }

                    Log.v(
                        "Debug1",
                        "MoviesByGenreViewModel callBackMoviesList checkResponse i=" + i + ", id=" + serverResponse.results[i].id
                    )
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

                val moviesByGenre = MoviesByGenre(
                    genre,
                    moviesList
                )

                AppState.SuccessMoviesByGenre(
                    moviesByGenre
                )
            }
        }
    }

    private var moviesByGenres = mutableListOf<MoviesByGenre>()

    fun successItemGenre(appState: AppState) {
        countSuccess++
        when (appState) {
            is AppState.SuccessMoviesByGenre -> {

                moviesByGenres.add(
                    MoviesByGenre(
                        appState.moviesByGenre.genre,
                        appState.moviesByGenre.movies
                    )
                )
            }
        }
        if (countSuccess == genres.size) {
            liveDataToObserver2.postValue((AppState.SuccessMoviesByGenres(moviesByGenres)))
        }
    }

    fun getMoviesByGenresFromRemoteSource(genres: List<Genre>) {
        liveDataToObserver2.value = AppState.Loading

        for (genre in genres) {
            repository.getMoviesByCategoryFromRemoteServerRetroFit(
                genre,
                Constant.LANG_VALUE,
                callBackMoviesList
            )
        }

    }


    //Genres

    private val callBackGenres = object :
        Callback<GenresAPI> {

        override fun onResponse(call: Call<GenresAPI>, response: Response<GenresAPI>) {
            Log.v("Debug1", "GenresViewModel callBackGenres onResponse")
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
            Log.v("Debug1", "GenresViewModel callBackGenres onFailure")
            liveDataToObserver.postValue(AppState.Error(Throwable(t.message ?: REQUEST_ERROR)))
        }

        private fun checkResponse(serverResponse: GenresAPI): AppState {
            Log.v("Debug1", "GenresViewModel callBackGenres checkResponse")
            return if (serverResponse.results.isEmpty()) {
                AppState.Error(Throwable(CORRUPTED_DATA))
            } else {

                for (i in serverResponse.results.indices) {
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
            callBackGenres
        )
    }
}