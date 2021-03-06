package com.android.filmlibrary.presentation.itemmovie

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.android.filmlibrary.utils.Constant.CORRUPTED_DATA
import com.android.filmlibrary.utils.Constant.FORMATED_STRING_DATE_TMDB
import com.android.filmlibrary.utils.Constant.FORMATED_STRING_YEAR
import com.android.filmlibrary.utils.Constant.LANG_VALUE
import com.android.filmlibrary.utils.Constant.REQUEST_ERROR
import com.android.filmlibrary.utils.Constant.SERVER_ERROR
import com.android.filmlibrary.utils.GlobalVariables
import com.android.filmlibrary.utils.AppState
import com.android.filmlibrary.data.model.Movie
import com.android.filmlibrary.data.model.MovieAdv
import com.android.filmlibrary.data.model.credits.Cast
import com.android.filmlibrary.data.model.credits.Credits
import com.android.filmlibrary.data.model.credits.Crew
import com.android.filmlibrary.domain.local.db.RepositoryLocalDBImpl
import com.android.filmlibrary.domain.remote.RepositoryRemoteImpl
import com.android.filmlibrary.data.retrofit.CastAPI
import com.android.filmlibrary.data.retrofit.CreditsAPI
import com.android.filmlibrary.data.retrofit.CrewAPI
import com.android.filmlibrary.data.retrofit.MovieAdvAPI
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MovieInfoViewModel : ViewModel() {

    private val liveDataToObserver: MutableLiveData<AppState> = MutableLiveData()
    private val liveDataToObserverAddNote: MutableLiveData<AppState> = MutableLiveData()
    private val liveDataToObserverGetNote: MutableLiveData<AppState> = MutableLiveData()
    private val liveDataToObserverDeleteNote: MutableLiveData<AppState> = MutableLiveData()
    private val liveDataToObserverSetFavorite: MutableLiveData<AppState> = MutableLiveData()
    private val liveDataToObserverGetFavorite: MutableLiveData<AppState> = MutableLiveData()
    private val liveDataToObserverCredits: MutableLiveData<AppState> = MutableLiveData()

    private val repository = RepositoryRemoteImpl()
    private var movieId: Int = 1
    private var noteText: String = ""

    private val repositoryLocal = RepositoryLocalDBImpl(GlobalVariables.getDAO())

    fun setData(movieId: Int): LiveData<AppState> {
        this.movieId = movieId
        return liveDataToObserver
    }

    fun getCreditsByMovieStart(): LiveData<AppState> {
        return liveDataToObserverCredits
    }

    fun addNoteStart(): LiveData<AppState> {
        return liveDataToObserverAddNote
    }

    fun addNote(noteText: String) {
        this.noteText = noteText
        Thread {
            liveDataToObserverAddNote.postValue(
                AppState.SuccessSetNote(
                    repositoryLocal.updateMovieNote(
                        movieId.toLong(), noteText
                    ).toLong()
                )
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

    private val callBackMovieAdv = object :
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
                        formattedDate,
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

    private val callBackCredits = object :
        Callback<CreditsAPI> {

        override fun onResponse(call: Call<CreditsAPI>, response: Response<CreditsAPI>) {
            val serverResponse: CreditsAPI? = response.body()
            liveDataToObserverCredits.postValue(
                if (response.isSuccessful && serverResponse != null) {
                    checkResponse(serverResponse)
                } else {
                    AppState.Error(Throwable(SERVER_ERROR))
                }
            )
        }

        override fun onFailure(call: Call<CreditsAPI>, t: Throwable) {
            liveDataToObserverCredits.postValue(
                AppState.Error(
                    Throwable(
                        t.message ?: REQUEST_ERROR
                    )
                )
            )
        }

        private fun checkResponse(serverResponse: CreditsAPI): AppState {
            return if (serverResponse.id == -1) {
                AppState.Error(Throwable(CORRUPTED_DATA))
            } else {

                val castAPI: List<CastAPI> = serverResponse.cast
                val crewAPI: List<CrewAPI> = serverResponse.crew

                val cast = mutableListOf<Cast>()
                val crew = mutableListOf<Crew>()

                for (itemCastAPI in castAPI) {
                    cast.add(
                        Cast(
                            itemCastAPI.adult,
                            itemCastAPI.gender,
                            itemCastAPI.id,
                            itemCastAPI.knownForDepartment,
                            itemCastAPI.name,
                            itemCastAPI.originalName,
                            itemCastAPI.popularity,
                            itemCastAPI.profilePath,
                            itemCastAPI.castId,
                            itemCastAPI.character,
                            itemCastAPI.creditId,
                            itemCastAPI.order
                        )
                    )
                }

                for (itemCrewAPI in crewAPI) {
                    crew.add(
                        Crew(
                            itemCrewAPI.adult,
                            itemCrewAPI.gender,
                            itemCrewAPI.id,
                            itemCrewAPI.knownForDepartment,
                            itemCrewAPI.name,
                            itemCrewAPI.originalName,
                            itemCrewAPI.popularity,
                            itemCrewAPI.profilePath,
                            itemCrewAPI.creditId,
                            itemCrewAPI.department,
                            itemCrewAPI.job
                        )
                    )
                }

                AppState.SuccessGetCredits(
                    Credits(
                        serverResponse.id,
                        cast,
                        crew
                    )
                )
            }
        }
    }

    fun getMovieFromRemoteSource() {
        liveDataToObserver.value = AppState.Loading
        repository.getMovieFromRemoteServerRetrofit(movieId, LANG_VALUE, callBackMovieAdv)
    }

    fun getCreditsByMovieFromRemoteSource(movieId: Int) {
        liveDataToObserverCredits.value = AppState.Loading
        repository.getCreditsByMovieFromRemoteServerRetrofit(movieId, LANG_VALUE, callBackCredits)
    }
}