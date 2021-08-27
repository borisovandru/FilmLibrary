package com.android.filmlibrary.viewmodel.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.android.filmlibrary.Constant
import com.android.filmlibrary.Constant.COUNT_MOVIES_BY_CATEGORY
import com.android.filmlibrary.Constant.FORMATED_STRING_DATE_TMDB
import com.android.filmlibrary.Constant.FORMATED_STRING_YEAR
import com.android.filmlibrary.GlobalVariables.Companion.getDAO
import com.android.filmlibrary.GlobalVariables.Companion.moviesBySearchCache
import com.android.filmlibrary.GlobalVariables.Companion.searchStringCache
import com.android.filmlibrary.model.AppState
import com.android.filmlibrary.model.data.Movie
import com.android.filmlibrary.model.data.MoviesList
import com.android.filmlibrary.model.data.SearchResult
import com.android.filmlibrary.model.repository.local.db.RepositoryLocalDBImpl
import com.android.filmlibrary.model.repository.remote.RepositoryRemoteImpl
import com.android.filmlibrary.model.retrofit.MoviesListAPI
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SearchViewModel : ViewModel() {

    private val liveDataToObserver: MutableLiveData<AppState> = MutableLiveData()
    private val liveDataToObserver2: MutableLiveData<AppState> = MutableLiveData()

    private val repository = RepositoryRemoteImpl()
    private lateinit var searchRequest: String
    private var adult: Boolean = false

    private val repositoryLocal = RepositoryLocalDBImpl(getDAO())

    fun setData(searchRequest: String, adult: Boolean): LiveData<AppState> {
        this.searchRequest = searchRequest
        this.adult = adult
        repositoryLocal.addSearchQuery(searchRequest)
        return liveDataToObserver
    }

    fun getSearchHistory(): LiveData<AppState> {
        return liveDataToObserver2
    }

    fun getSearchHistory2() {
        liveDataToObserver2.value = AppState.Loading
        Thread {
            liveDataToObserver2.postValue(
                (AppState.SuccessGetSearchHistory(repositoryLocal.getSearchHistory()))
            )
        }.start()
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

                moviesBySearchCache = moviesList

                searchStringCache = searchRequest

                AppState.SuccessSearch(
                    SearchResult(moviesList, searchRequest)
                )
            }
        }
    }

    fun getSearchDataFromRemoteSource() {
        liveDataToObserver.value = AppState.Loading

        if (moviesBySearchCache.results.isNotEmpty()) {
            liveDataToObserver.postValue(
                AppState.SuccessSearch(
                    SearchResult(moviesBySearchCache, searchRequest)
                )
            )
        } else {
            repository.getMoviesBySearchFromRemoteServerRetroFit(
                searchRequest,
                COUNT_MOVIES_BY_CATEGORY,
                Constant.LANG_VALUE,
                adult,
                callBack
            )
        }
    }
}