package com.android.filmlibrary.utils

import com.android.filmlibrary.data.model.*
import com.android.filmlibrary.data.model.credits.Credits

sealed class AppState {
    data class SuccessMoviesByGenre(val moviesByGenre: MoviesByGenre) : AppState()
    data class SuccessMoviesByGenres(val moviesByGenres: List<MoviesByGenre>) :
        AppState()

    data class SuccessSearch(val moviesBySearches: SearchResult) : AppState()

    data class SuccessMoviesByTrend(val moviesByTrends: MoviesByTrend) :
        AppState()

    data class SuccessMoviesByTrends(val moviesByTrends: List<MoviesByTrend>) :
        AppState()

    class Error(val error: Throwable) : AppState()
    object Loading : AppState()

    data class SuccessMovie(val movieAdvData: MovieAdv) : AppState()
    data class SuccessGenres(val genreData: List<Genre>) : AppState()

    data class SuccessGetNote(val note: String?) : AppState()
    data class SuccessSetNote(val countNotes: Long) : AppState()
    data class SuccessDeleteNote(val countNotes: Int) : AppState()

    data class SuccessAddFavorite(val countNotes: Long) : AppState()
    data class SuccessRemoveFavorite(val countNotes: Int) : AppState()
    data class SuccessGetFavorite(val idFav: Long) : AppState()

    data class SuccessGetSearchHistory(val searchHistory: List<String>) : AppState()

    data class SuccessGetFavoriteMovies(val favMovies: List<Movie>) : AppState()

    data class SuccessGetContacts(val contacts: List<Contact>) : AppState()
    data class SuccessGetCredits(val credits: Credits) : AppState()
    data class SuccessGetPerson(val person: Person) : AppState()
    data class SuccessGetMessages(val messages: List<MessageNot>) : AppState()
}