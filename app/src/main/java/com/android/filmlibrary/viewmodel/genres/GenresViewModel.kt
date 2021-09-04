package com.android.filmlibrary.viewmodel.genres

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.filmlibrary.Constant
import com.android.filmlibrary.Constant.CORRUPTED_DATA
import com.android.filmlibrary.Constant.FORMATED_STRING_DATE_TMDB
import com.android.filmlibrary.Constant.FORMATED_STRING_YEAR
import com.android.filmlibrary.Constant.REQUEST_ERROR
import com.android.filmlibrary.Constant.SERVER_ERROR
import com.android.filmlibrary.Constant.URL_GENRES_PATH
import com.android.filmlibrary.GlobalVariables.Companion.genresCache
import com.android.filmlibrary.GlobalVariables.Companion.moviesByGenresCache
import com.android.filmlibrary.model.AppState
import com.android.filmlibrary.model.data.Genre
import com.android.filmlibrary.model.data.Movie
import com.android.filmlibrary.model.data.MoviesByGenre
import com.android.filmlibrary.model.data.MoviesList
import com.android.filmlibrary.model.repository.remote.RepositoryRemote
import com.android.filmlibrary.model.repository.remote.RepositoryRemoteImpl
import com.android.filmlibrary.model.retrofit.GenresAPI
import com.android.filmlibrary.model.retrofit.MoviesListAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class GenresViewModel(private val repositoryRemote: RepositoryRemote = RepositoryRemoteImpl()) :
    ViewModel() {

    private val liveDataToObserverGenres = MutableLiveData<AppState>()
    private val liveDataToObserverMoviesByGenres = MutableLiveData<AppState>()

    var genres = mutableListOf<Genre>()
    private var moviesByGenres = mutableListOf<MoviesByGenre>()

    private var countSuccess: Int = 0

    fun getData(): LiveData<AppState> {
        return liveDataToObserverGenres
    }

    fun getData2(): LiveData<AppState> {
        return liveDataToObserverMoviesByGenres
    }

    private val callBackMoviesList = object :
        Callback<MoviesListAPI> {

        override fun onResponse(call: Call<MoviesListAPI>, response: Response<MoviesListAPI>) {

            val genreId: String = (response.raw().networkResponse()
                ?.request()
                ?.url()?.queryParameter(URL_GENRES_PATH) ?: "")

            response.raw()

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
            successItemGenre(AppState.Error(Throwable(t.message ?: REQUEST_ERROR)))
        }

        private fun checkResponse(serverResponse: MoviesListAPI, genre: Genre): AppState {
            return if (serverResponse.results.isEmpty()) {
                AppState.Error(Throwable(CORRUPTED_DATA))
            } else {
                val movies = mutableListOf<Movie>()
                serverResponse.results.indices.forEach { i ->
                    var formattedDate = ""
                    serverResponse.results[i].dateRelease?.let {
                        formattedDate = ""
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
            moviesByGenresCache = moviesByGenres
            liveDataToObserverMoviesByGenres.postValue(AppState.SuccessMoviesByGenres(moviesByGenres))
        }
    }

    fun getMoviesByGenresFromRemoteSource(genres: List<Genre>) {
        liveDataToObserverMoviesByGenres.value = AppState.Loading

        if (moviesByGenresCache.isNotEmpty()) {
            moviesByGenres = moviesByGenresCache
            liveDataToObserverMoviesByGenres.postValue(AppState.SuccessMoviesByGenres(moviesByGenres))
        } else {

            genres.forEach { genre ->
                repositoryRemote.getMoviesByCategoryFromRemoteServerRetroFit(
                    genre,
                    Constant.LANG_VALUE,
                    callBackMoviesList
                )
            }
        }
    }

    //Genres
    private val callBackGenres = object :
        Callback<GenresAPI> {

        override fun onResponse(call: Call<GenresAPI>, response: Response<GenresAPI>) {
            val serverResponse: GenresAPI? = response.body()
            liveDataToObserverGenres.postValue(
                if (response.isSuccessful && serverResponse != null) {
                    checkResponse(serverResponse)
                } else {
                    AppState.Error(Throwable(SERVER_ERROR))
                }
            )
        }

        override fun onFailure(call: Call<GenresAPI>, t: Throwable) {
            liveDataToObserverGenres.postValue(
                AppState.Error(
                    Throwable(
                        t.message ?: REQUEST_ERROR
                    )
                )
            )
        }

        private fun checkResponse(serverResponse: GenresAPI): AppState {
            return if (serverResponse.results.isEmpty()) {
                AppState.Error(Throwable(CORRUPTED_DATA))
            } else {


                serverResponse.results.indices.forEach { i ->
                    genres.add(
                        Genre(
                            serverResponse.results[i].id,
                            serverResponse.results[i].name
                        )
                    )
                }
                genresCache = genres
                AppState.SuccessGenres(genres)
            }
        }
    }

    fun getGenresFromRemoteSource() {
        liveDataToObserverGenres.value = AppState.Loading

        if (genresCache.isNotEmpty()) {
            genres = genresCache
            liveDataToObserverGenres.postValue(AppState.SuccessGenres(genres))
        } else {
            repositoryRemote.getGenresFromRemoteServerRetroFit(
                Constant.LANG_VALUE,
                callBackGenres
            )
        }
    }
}