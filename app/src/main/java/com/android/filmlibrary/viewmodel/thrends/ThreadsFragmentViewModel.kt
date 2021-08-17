package com.android.filmlibrary.viewmodel.thrends

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.filmlibrary.Constant
import com.android.filmlibrary.Constant.COUNT_MOVIES_BY_TREND
import com.android.filmlibrary.Constant.URL_NOW_PLAYING
import com.android.filmlibrary.Constant.URL_POPULAR
import com.android.filmlibrary.Constant.URL_TOP_RATED
import com.android.filmlibrary.Constant.URL_UPCOMING
import com.android.filmlibrary.model.AppState
import com.android.filmlibrary.model.data.Movie
import com.android.filmlibrary.model.data.MoviesByTrend
import com.android.filmlibrary.model.data.MoviesList
import com.android.filmlibrary.model.data.Trend
import com.android.filmlibrary.model.repository.Repository
import com.android.filmlibrary.model.repository.RepositoryImpl
import com.android.filmlibrary.model.retrofit.MoviesListAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ThrendsFragmentViewModel(private val repository: Repository = RepositoryImpl()) :
    ViewModel() {

    private val liveDataToObserver = MutableLiveData<AppState>()

    private var countSuccess: Int = 0

    private val trends: List<Trend> = listOf(
        Trend("Popular", URL_POPULAR),
        Trend("Rated", URL_TOP_RATED),
        Trend("Now playing", URL_NOW_PLAYING),
        Trend("Upcoming", URL_UPCOMING),
    )

    fun getData(): LiveData<AppState> {
        return liveDataToObserver
    }

    private val callBackMoviesList = object :
        Callback<MoviesListAPI> {

        override fun onResponse(call: Call<MoviesListAPI>, response: Response<MoviesListAPI>) {
            Log.v("Debug1", "GenresViewModel callBackMoviesList onResponse")

            val trendName: String =
                response.raw().networkResponse()?.request()?.url()?.pathSegments()?.get(2) ?: "0"

            val trend = trends.first { it.URL == trendName }

            val serverResponse: MoviesListAPI? = response.body()

            successItemTrend(
                if (response.isSuccessful && serverResponse != null) {
                    checkResponse(serverResponse, trend)
                } else {
                    AppState.Error(Throwable(Constant.SERVER_ERROR))
                }
            )
        }

        override fun onFailure(call: Call<MoviesListAPI>, t: Throwable) {
            Log.v("Debug1", "GenresViewModel callBackMoviesList onFailure")
            successItemTrend(AppState.Error(Throwable(t.message ?: Constant.REQUEST_ERROR)))
        }

        private fun checkResponse(serverResponse: MoviesListAPI, trend: Trend): AppState {
            Log.v("Debug1", "GenresViewModel callBackMoviesList checkResponse")
            return if (serverResponse.results.isEmpty()) {
                AppState.Error(Throwable(Constant.CORRUPTED_DATA))
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

                val moviesByTrend = MoviesByTrend(
                    trend,
                    moviesList
                )

                AppState.SuccessMoviesByTrend(
                    moviesByTrend
                )
            }
        }
    }

    private var moviesByTrends = mutableListOf<MoviesByTrend>()


    fun successItemTrend(appState: AppState) {
        countSuccess++
        when (appState) {
            is AppState.SuccessMoviesByTrend -> {

                moviesByTrends.add(
                    MoviesByTrend(
                        appState.moviesByTrends.trend,
                        appState.moviesByTrends.moviesList
                    )
                )
            }
        }
        if (countSuccess == trends.size) {
            liveDataToObserver.postValue((AppState.SuccessMoviesByTrends(moviesByTrends)))
        }
    }

    fun getTrendsFromRemoteSource() {
        liveDataToObserver.value = AppState.Loading

        for (trend in trends) {
            repository.getMoviesByTrendFromRemoteServerRetroFit(
                trend,
                COUNT_MOVIES_BY_TREND,
                Constant.LANG_VALUE,
                callBackMoviesList
            )
        }
    }
}