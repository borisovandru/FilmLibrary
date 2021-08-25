package com.android.filmlibrary.viewmodel.thrends

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.android.filmlibrary.Constant
import com.android.filmlibrary.Constant.COUNT_MOVIES_BY_TREND
import com.android.filmlibrary.Constant.FORMATED_STRING_DATE_TMDB
import com.android.filmlibrary.Constant.FORMATED_STRING_YEAR
import com.android.filmlibrary.Constant.URL_NOW_PLAYING
import com.android.filmlibrary.Constant.URL_NOW_PLAYING_NAME
import com.android.filmlibrary.Constant.URL_POPULAR
import com.android.filmlibrary.Constant.URL_POPULAR_NAME
import com.android.filmlibrary.Constant.URL_TOP_RATED
import com.android.filmlibrary.Constant.URL_TOP_RATED_NAME
import com.android.filmlibrary.Constant.URL_TREND_POSITION
import com.android.filmlibrary.Constant.URL_UPCOMING
import com.android.filmlibrary.Constant.URL_UPCOMING_NAME
import com.android.filmlibrary.model.AppState
import com.android.filmlibrary.model.data.*
import com.android.filmlibrary.model.repository.remote.RepositoryRemote
import com.android.filmlibrary.model.repository.remote.RepositoryRemoteImpl
import com.android.filmlibrary.model.retrofit.MoviesListAPI
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ThrendsFragmentViewModel(private val repositoryRemote: RepositoryRemote = RepositoryRemoteImpl()) :
    ViewModel() {

    private val liveDataToObserver = MutableLiveData<AppState>()

    private var countSuccess: Int = 0

    private val trends: List<Trend> = listOf(
        Trend(URL_POPULAR_NAME, URL_POPULAR),
        Trend(URL_TOP_RATED_NAME, URL_TOP_RATED),
        Trend(URL_NOW_PLAYING_NAME, URL_NOW_PLAYING),
        Trend(URL_UPCOMING_NAME, URL_UPCOMING),
    )

    fun getData(): LiveData<AppState> {
        return liveDataToObserver
    }

    private val callBackMoviesList = object :
        Callback<MoviesListAPI> {

        override fun onResponse(call: Call<MoviesListAPI>, response: Response<MoviesListAPI>) {

            val trendName: String =
                response.raw().networkResponse()?.request()?.url()?.pathSegments()
                    ?.get(URL_TREND_POSITION) ?: "0"

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
            successItemTrend(AppState.Error(Throwable(t.message ?: Constant.REQUEST_ERROR)))
        }

        private fun checkResponse(serverResponse: MoviesListAPI, trend: Trend): AppState {
            return if (serverResponse.results.isEmpty()) {
                AppState.Error(Throwable(Constant.CORRUPTED_DATA))
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

        trends.forEach { trend ->
            repositoryRemote.getMoviesByTrendFromRemoteServerRetroFit(
                trend,
                COUNT_MOVIES_BY_TREND,
                Constant.LANG_VALUE,
                callBackMoviesList
            )
        }
    }
}