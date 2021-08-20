package com.android.filmlibrary.viewmodel.itemmovie

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.android.filmlibrary.Constant.CORRUPTED_DATA
import com.android.filmlibrary.Constant.FORMATED_STRING_DATE_TMDB
import com.android.filmlibrary.Constant.FORMATED_STRING_YEAR
import com.android.filmlibrary.Constant.LANG_VALUE
import com.android.filmlibrary.Constant.REQUEST_ERROR
import com.android.filmlibrary.Constant.SERVER_ERROR
import com.android.filmlibrary.GlobalVariables
import com.android.filmlibrary.model.AppState
import com.android.filmlibrary.model.data.Movie
import com.android.filmlibrary.model.data.MovieAdv
import com.android.filmlibrary.model.repository.local.RepositoryLocalImpl
import com.android.filmlibrary.model.repository.remote.RepositoryRemoteImpl
import com.android.filmlibrary.model.retrofit.MovieAdvAPI
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MovieInfoViewModel : ViewModel() {

    private val liveDataToObserver: MutableLiveData<AppState> = MutableLiveData()
    private val liveDataToObserverAddNote: MutableLiveData<AppState> = MutableLiveData()
    private val liveDataToObserverGetNote: MutableLiveData<AppState> = MutableLiveData()
    private val liveDataToObserverDeleteNote: MutableLiveData<AppState> = MutableLiveData()
    private val liveDataToObserverSetFavorite: MutableLiveData<AppState> = MutableLiveData()
    private val liveDataToObserverGetFavorite: MutableLiveData<AppState> = MutableLiveData()

    private val repository = RepositoryRemoteImpl()
    private var movieId: Int = 1
    private var noteText: String = ""

    private val repositoryLocal = RepositoryLocalImpl(GlobalVariables.getDAO())

    fun setData(movieId: Int): LiveData<AppState> {
        this.movieId = movieId
        return liveDataToObserver
    }

    fun addNoteStart(): LiveData<AppState> {
        return liveDataToObserverAddNote
    }

    fun addNote(noteText: String) {
        this.noteText = noteText
        Thread {
            val noteCur: String?
            noteCur = repositoryLocal.getMovieNote(movieId.toLong())
            liveDataToObserverAddNote.postValue(
                if (noteCur != null) {
                    AppState.SuccessSetNote(
                        repositoryLocal.updateMovieNote(
                            movieId.toLong(), noteText
                        ).toLong()
                    )
                } else {
                    AppState.SuccessSetNote(
                        repositoryLocal.insertMovieNote(
                            movieId.toLong(), noteText
                        )
                    )
                }
            )
        }.start()
    }

    fun getNoteStart(): LiveData<AppState> {
        return liveDataToObserverGetNote
    }

    fun getNote(movieId: Int) {
        liveDataToObserverGetNote.value = AppState.Loading
        this.movieId = movieId
        Thread {
            liveDataToObserverGetNote.postValue(
                (AppState.SuccessGetNote(repositoryLocal.getMovieNote(movieId.toLong())))
            )
        }.start()
    }

    fun deleteNoteStart(): LiveData<AppState> {
        return liveDataToObserverDeleteNote
    }

    fun deleteNote(movieId: Int) {
        liveDataToObserverDeleteNote.value = AppState.Loading
        this.movieId = movieId
        Thread {
            liveDataToObserverDeleteNote.postValue(
                (AppState.SuccessDeleteNote(repositoryLocal.removeMovieNote(movieId.toLong())))
            )
        }.start()
    }

    fun favoriteSetStart(): LiveData<AppState> {
        return liveDataToObserverSetFavorite
    }

    fun favoriteSet(movie: Movie) {
        liveDataToObserverSetFavorite.value = AppState.Loading
        this.movieId = movie.id
        Thread {
            val isFav = repositoryLocal.getFavItem(movieId.toLong())
            liveDataToObserverSetFavorite.postValue(
                if (isFav != 0L) {
                    AppState.SuccessRemoveFavorite(repositoryLocal.removeFavoriteMovies(movie.id.toLong()))
                } else {
                    AppState.SuccessAddFavorite(repositoryLocal.addFavoriteMovie(movie))
                }
            )
        }.start()
    }

    fun favoriteGetStart(): LiveData<AppState> {
        return liveDataToObserverGetFavorite
    }

    fun favoriteGet(movieId: Int) {
        liveDataToObserverGetFavorite.value = AppState.Loading
        this.movieId = movieId
        Thread {
            liveDataToObserverGetFavorite.postValue(
                AppState.SuccessGetFavorite(repositoryLocal.getFavItem(movieId.toLong()))
            )
        }.start()
    }

    private val callBack = object :
        Callback<MovieAdvAPI> {

        override fun onResponse(call: Call<MovieAdvAPI>, response: Response<MovieAdvAPI>) {
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
            liveDataToObserver.postValue(AppState.Error(Throwable(t.message ?: REQUEST_ERROR)))
        }

        private fun checkResponse(serverResponse: MovieAdvAPI): AppState {
            return if (serverResponse.id == -1) {
                AppState.Error(Throwable(CORRUPTED_DATA))
            } else {

                var formattedDate = ""
                if (serverResponse.dateRelease != "") {
                    val localDate = LocalDate.parse(
                        serverResponse.dateRelease,
                        DateTimeFormatter.ofPattern(FORMATED_STRING_DATE_TMDB)
                    )
                    val formatter = DateTimeFormatter.ofPattern(FORMATED_STRING_YEAR)
                    formattedDate = localDate.format(formatter)
                }


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
        liveDataToObserver.value = AppState.Loading
        repository.getMovieFromRemoteServerRetroFit(movieId, LANG_VALUE, callBack)
    }
}