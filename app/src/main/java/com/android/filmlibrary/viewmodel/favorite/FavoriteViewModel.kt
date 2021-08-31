package com.android.filmlibrary.viewmodel.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.filmlibrary.GlobalVariables
import com.android.filmlibrary.model.AppState
import com.android.filmlibrary.model.data.Movie
import com.android.filmlibrary.model.repository.local.db.RepositoryLocalDBImpl
import com.android.filmlibrary.model.room.EntityFavMovies

class FavoriteViewModel(private val liveDataToObserver: MutableLiveData<AppState> = MutableLiveData()) :
    ViewModel() {
    private val repositoryLocal = RepositoryLocalDBImpl(GlobalVariables.getDAO())

    fun getFavoriteStart(): LiveData<AppState> {
        return liveDataToObserver
    }

    fun getFavoriteFromLocalDB() {
        liveDataToObserver.value = AppState.Loading
        Thread {
            liveDataToObserver.postValue(
                (
                        AppState.SuccessGetFavoriteMovies(
                            entityFavMoviesToMovies(
                                repositoryLocal.getFavoriteMovies()
                            )
                        )
                        )
            )
        }.start()
    }

    private fun entityFavMoviesToMovies(favoriteMovies: List<EntityFavMovies>): List<Movie> {
        val result = mutableListOf<Movie>()
        val genderIds = listOf<Int>()
        favoriteMovies.forEach { fm ->
            result.add(
                Movie(
                    fm.idMovie.toInt(),
                    fm.title,
                    fm.year,
                    genderIds,
                    fm.dateRelease,
                    fm.originalTitle,
                    fm.overview,
                    fm.posterUrl,
                    fm.voteAverage
                )
            )
        }
        return result
    }
}