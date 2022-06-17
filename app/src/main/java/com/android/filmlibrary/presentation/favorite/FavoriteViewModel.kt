package com.android.filmlibrary.presentation.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.filmlibrary.utils.GlobalVariables
import com.android.filmlibrary.utils.AppState
import com.android.filmlibrary.data.model.Movie
import com.android.filmlibrary.domain.local.db.RepositoryLocalDBImpl
import com.android.filmlibrary.data.room.EntityFavMovies

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